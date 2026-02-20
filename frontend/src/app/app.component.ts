import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthUser } from './models/auth.model';
import { ChatMessage } from './models/chat-message.model';
import { MyPostFeed } from './models/my-post-feed.model';
import { OrderPost } from './models/order-post.model';
import { BentoDemoComponent } from './components/ui/bento-demo/bento-demo.component';
import { ParallaxFeatureSectionComponent } from './components/ui/parallax-feature-section/parallax-feature-section.component';
import { SplineSceneBasicComponent } from './components/ui/spline-scene-basic/spline-scene-basic.component';
import { ParallaxScrollDirective } from './directives/parallax-scroll.directive';
import { RevealOnScrollDirective } from './directives/reveal-on-scroll.directive';
import { AuthService } from './services/auth.service';
import { ChatService } from './services/chat.service';
import { CreateOrderPostPayload, OrderPostService } from './services/order-post.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePipe,
    DecimalPipe,
    RevealOnScrollDirective,
    ParallaxScrollDirective,
    SplineSceneBasicComponent,
    BentoDemoComponent,
    ParallaxFeatureSectionComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly postService = inject(OrderPostService);
  private readonly chatService = inject(ChatService);
  private readonly authService = inject(AuthService);

  readonly stores = ['All', 'Walmart', 'Costco', "Sam's Club", 'Instacart', 'DoorDash', 'Uber Eats'];

  posts: OrderPost[] = [];
  loading = false;
  createLoading = false;
  authLoading = false;

  selectedStore = 'All';
  selectedRadiusMiles = 2;

  selectedPost: OrderPost | null = null;
  chatMessages: ChatMessage[] = [];
  chatLoading = false;
  myPosts: MyPostFeed[] = [];
  myPostsLoading = false;

  currentUser: AuthUser | null = null;
  otpExpiresAt: string | null = null;
  devOtpCode: string | null = null;
  authMessage = '';
  postFeedback = '';
  postFeedbackType: 'success' | 'error' | '' = '';

  readonly viewerForm = this.fb.group({
    alias: ['You', [Validators.required, Validators.maxLength(40)]],
    latitude: [37.781, [Validators.required]],
    longitude: [-122.405, [Validators.required]]
  });

  readonly authRequestForm = this.fb.group({
    displayName: ['Neighbor', [Validators.required, Validators.maxLength(40)]],
    phoneNumber: ['', [Validators.required, Validators.maxLength(20), Validators.pattern(/^[0-9+\-()\s]{8,20}$/)]]
  });

  readonly otpForm = this.fb.group({
    otpCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
  });

  readonly createPostForm = this.fb.group({
    initiatorAlias: ['Host', [Validators.required, Validators.maxLength(40)]],
    storeName: ['Walmart', [Validators.required]],
    addressHint: ['Near Downtown', [Validators.required, Validators.maxLength(100)]],
    expectedDeliveryTime: [this.defaultExpectedTime(), [Validators.required]],
    minimumOrderAmount: [35, [Validators.required, Validators.min(1)]],
    currentCartAmount: [31.5, [Validators.required, Validators.min(0)]],
    postRadiusMiles: [2, [Validators.required, Validators.min(1), Validators.max(10)]],
    title: ['Need a few dollars to unlock free delivery', [Validators.required, Validators.maxLength(120)]],
    notes: ['Add your item and pay after handoff.', [Validators.maxLength(300)]],
    maskedPhone: ['+1 (***) ***-0000', [Validators.required, Validators.maxLength(20)]],
    phoneNumber: ['+1-555-000-0000', [Validators.maxLength(20)]]
  });

  readonly messageForm = this.fb.group({
    text: ['', [Validators.required, Validators.maxLength(300)]]
  });

  ngOnInit(): void {
    this.restoreSession();
    this.loadPosts();
  }

  get isAuthenticated(): boolean {
    return this.currentUser !== null;
  }

  requestOtp(): void {
    if (this.authRequestForm.invalid) {
      this.authRequestForm.markAllAsTouched();
      return;
    }

    const phoneNumber = (this.authRequestForm.getRawValue().phoneNumber ?? '').trim();
    this.authLoading = true;
    this.authMessage = '';
    this.devOtpCode = null;

    this.authService.requestOtp(phoneNumber).subscribe({
      next: (response) => {
        this.authLoading = false;
        this.otpExpiresAt = response.expiresAt;
        this.devOtpCode = response.devOtpCode;
        this.authMessage = response.devOtpCode
          ? `OTP generated. Dev code: ${response.devOtpCode}`
          : response.message;
      },
      error: (error) => {
        this.authLoading = false;
        this.authMessage = this.extractErrorMessage(error, 'Failed to generate OTP.');
      }
    });
  }

  verifyOtp(): void {
    if (this.authRequestForm.invalid || this.otpForm.invalid) {
      this.authRequestForm.markAllAsTouched();
      this.otpForm.markAllAsTouched();
      return;
    }

    this.authLoading = true;
    const request = {
      phoneNumber: (this.authRequestForm.getRawValue().phoneNumber ?? '').trim(),
      otpCode: (this.otpForm.getRawValue().otpCode ?? '').replace(/\D/g, '').slice(0, 6),
      displayName: (this.authRequestForm.getRawValue().displayName ?? 'Neighbor').trim()
    };

    this.authService.verifyOtp(request).subscribe({
      next: (response) => {
        this.authLoading = false;
        this.setCurrentUser(response.user);
        this.otpForm.reset();
        this.otpExpiresAt = null;
        this.devOtpCode = null;
        this.authMessage = `Signed in as ${response.user.displayName}`;
        this.loadPosts();
      },
      error: (error) => {
        this.authLoading = false;
        this.authMessage = this.extractErrorMessage(error, 'Failed to verify OTP.');
      }
    });
  }

  logout(): void {
    if (!this.currentUser) {
      return;
    }

    this.authLoading = true;
    this.authService.logout().subscribe({
      next: () => {
        this.authLoading = false;
        this.authService.clearAccessToken();
        this.currentUser = null;
        this.myPosts = [];
        this.postFeedback = '';
        this.postFeedbackType = '';
        this.authMessage = 'Signed out successfully.';
        this.loadPosts();
      },
      error: (error) => {
        this.authLoading = false;
        this.authService.clearAccessToken();
        this.currentUser = null;
        this.myPosts = [];
        this.postFeedback = '';
        this.postFeedbackType = '';
        this.authMessage = this.extractErrorMessage(error, 'Signed out locally.');
        this.loadPosts();
      }
    });
  }

  loadPosts(): void {
    if (this.viewerForm.invalid) {
      this.viewerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const { latitude, longitude } = this.viewerForm.getRawValue();

    this.postService.getPosts(latitude ?? 0, longitude ?? 0, this.selectedRadiusMiles, this.selectedStore)
      .subscribe({
        next: (posts) => {
          this.posts = posts;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  onRadiusChange(nextValue: string): void {
    this.selectedRadiusMiles = Number(nextValue);
    this.loadPosts();
  }

  onStoreChange(store: string): void {
    this.selectedStore = store;
    this.loadPosts();
  }

  createPost(): void {
    if (!this.ensureAuthenticated('publish a post')) {
      return;
    }

    if (this.createPostForm.invalid || this.viewerForm.invalid) {
      this.createPostForm.markAllAsTouched();
      this.viewerForm.markAllAsTouched();
      return;
    }

    const createPayload: CreateOrderPostPayload = {
      ...(this.createPostForm.getRawValue() as CreateOrderPostPayload),
      initiatorAlias: this.currentUser?.displayName ?? this.createPostForm.getRawValue().initiatorAlias ?? 'Host',
      latitude: this.viewerForm.getRawValue().latitude ?? 0,
      longitude: this.viewerForm.getRawValue().longitude ?? 0,
      expectedDeliveryTime: this.formatLocalDate(this.createPostForm.getRawValue().expectedDeliveryTime ?? '')
    };

    this.postFeedback = '';
    this.postFeedbackType = '';
    this.createLoading = true;
    this.postService.createPost(createPayload).subscribe({
      next: () => {
        this.createLoading = false;
        this.postFeedback = 'Post published. You can track it in My Posted Feeds.';
        this.postFeedbackType = 'success';
        this.createPostForm.patchValue({
          title: 'Need a few dollars to unlock free delivery',
          notes: 'Add your item and pay after handoff.',
          currentCartAmount: 31.5,
          expectedDeliveryTime: this.defaultExpectedTime()
        });
        this.loadPosts();
        this.loadMyPosts();
      },
      error: (error) => {
        this.createLoading = false;
        this.postFeedback = this.extractErrorMessage(error, 'Failed to publish post.');
        this.postFeedbackType = 'error';
      }
    });
  }

  expressInterest(post: OrderPost): void {
    if (!this.ensureAuthenticated('join this order')) {
      return;
    }

    this.postService.markInterest(post.id).subscribe({
      next: (updated) => {
        this.patchPost(updated);
      },
      error: (error) => {
        this.authMessage = this.extractErrorMessage(error, 'Could not register interest.');
      }
    });
  }

  toggleContact(post: OrderPost): void {
    if (!this.ensureAuthenticated('manage contact reveal')) {
      return;
    }

    if (!post.canManageContact) {
      this.authMessage = 'Only the post owner can reveal or mask contact.';
      return;
    }

    this.postService.revealContact(post.id, !post.phoneRevealEnabled).subscribe({
      next: (updated) => {
        this.patchPost(updated);
      },
      error: (error) => {
        this.authMessage = this.extractErrorMessage(error, 'Could not update contact visibility.');
      }
    });
  }

  openChat(post: OrderPost): void {
    this.selectedPost = post;
    this.messageForm.reset();
    this.fetchChat(post.id);
  }

  closeChat(): void {
    this.selectedPost = null;
    this.chatMessages = [];
    this.messageForm.reset();
  }

  sendMessage(): void {
    if (!this.ensureAuthenticated('send messages')) {
      return;
    }

    if (!this.selectedPost || this.messageForm.invalid) {
      this.messageForm.markAllAsTouched();
      return;
    }

    const text = this.messageForm.getRawValue().text ?? '';

    this.chatService.sendMessage(this.selectedPost.id, text).subscribe({
      next: (message) => {
        this.chatMessages = [...this.chatMessages, message];
        this.messageForm.reset();
      },
      error: (error) => {
        this.authMessage = this.extractErrorMessage(error, 'Could not send message.');
      }
    });
  }

  trackByPostId(_: number, post: OrderPost): string {
    return post.id;
  }

  trackByMyPostId(_: number, feed: MyPostFeed): string {
    return feed.post.id;
  }

  loadMyPosts(): void {
    if (!this.currentUser) {
      this.myPosts = [];
      return;
    }

    this.myPostsLoading = true;
    this.postService.getMyPosts().subscribe({
      next: (feeds) => {
        this.myPosts = feeds;
        this.myPostsLoading = false;
      },
      error: (error) => {
        this.myPostsLoading = false;
        this.postFeedback = this.extractErrorMessage(error, 'Unable to load your posted feed.');
        this.postFeedbackType = 'error';
      }
    });
  }

  private fetchChat(postId: string): void {
    this.chatLoading = true;
    this.chatService.getMessages(postId).subscribe({
      next: (messages) => {
        this.chatMessages = messages;
        this.chatLoading = false;
      },
      error: () => {
        this.chatLoading = false;
      }
    });
  }

  private patchPost(updated: OrderPost): void {
    this.posts = this.posts.map((post) => post.id === updated.id ? { ...post, ...updated } : post);
    if (this.selectedPost && this.selectedPost.id === updated.id) {
      this.selectedPost = { ...this.selectedPost, ...updated };
    }
  }

  private ensureAuthenticated(action: string): boolean {
    if (this.currentUser) {
      return true;
    }
    this.authMessage = `Sign in with OTP to ${action}.`;
    return false;
  }

  private restoreSession(): void {
    const token = this.authService.getAccessToken();
    if (!token) {
      return;
    }

    this.authService.me().subscribe({
      next: (user) => {
        this.setCurrentUser(user);
      },
      error: () => {
        this.authService.clearAccessToken();
        this.currentUser = null;
      }
    });
  }

  private setCurrentUser(user: AuthUser): void {
    this.currentUser = user;
    this.viewerForm.patchValue({ alias: user.displayName });
    this.createPostForm.patchValue({ initiatorAlias: user.displayName });
    this.authRequestForm.patchValue({ displayName: user.displayName, phoneNumber: user.phoneNumber });
    this.loadMyPosts();
  }

  private defaultExpectedTime(): string {
    const next = new Date(Date.now() + 3 * 60 * 60 * 1000);
    const year = next.getFullYear();
    const month = `${next.getMonth() + 1}`.padStart(2, '0');
    const day = `${next.getDate()}`.padStart(2, '0');
    const hours = `${next.getHours()}`.padStart(2, '0');
    const minutes = `${next.getMinutes()}`.padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  private formatLocalDate(value: string): string {
    const date = new Date(value);
    return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 19);
  }

  private extractErrorMessage(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse && typeof error.error === 'object' && error.error !== null) {
      const message = (error.error as { message?: string }).message;
      if (message) {
        return message;
      }
    }
    return fallback;
  }
}

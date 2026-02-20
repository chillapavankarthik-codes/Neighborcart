import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';

import { environment } from '../../environments/environment';
import { MyPostFeed } from '../models/my-post-feed.model';
import { OrderPost } from '../models/order-post.model';

export interface CreateOrderPostPayload {
  initiatorAlias: string;
  storeName: string;
  latitude: number;
  longitude: number;
  addressHint: string;
  expectedDeliveryTime: string;
  minimumOrderAmount: number;
  currentCartAmount: number;
  postRadiusMiles: number;
  title: string;
  notes: string;
  maskedPhone: string;
  phoneNumber: string;
}

@Injectable({ providedIn: 'root' })
export class OrderPostService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/posts`;
  private readonly localPostsKey = 'neighborcart_demo_posts_v1';

  getPosts(lat: number, lng: number, radiusMiles: number, store: string): Observable<OrderPost[]> {
    let params = new HttpParams()
      .set('lat', `${lat}`)
      .set('lng', `${lng}`)
      .set('radiusMiles', `${radiusMiles}`);

    if (store !== 'All') {
      params = params.set('store', store);
    }

    return this.http.get<OrderPost[]>(this.baseUrl, { params }).pipe(
      map((posts) => this.sortByCreatedAt(posts)),
      catchError(() => of(this.getFilteredLocalPosts(lat, lng, radiusMiles, store)))
    );
  }

  createPost(payload: CreateOrderPostPayload): Observable<OrderPost> {
    return this.http.post<OrderPost>(this.baseUrl, payload);
  }

  getMyPosts(): Observable<MyPostFeed[]> {
    return this.http.get<MyPostFeed[]>(`${this.baseUrl}/mine`);
  }

  markInterest(postId: string): Observable<OrderPost> {
    return this.http.post<OrderPost>(`${this.baseUrl}/${postId}/interest`, {});
  }

  revealContact(postId: string, reveal: boolean): Observable<OrderPost> {
    return this.http.post<OrderPost>(`${this.baseUrl}/${postId}/reveal-contact`, {
      reveal
    });
  }

  private getFilteredLocalPosts(
    viewerLat: number,
    viewerLng: number,
    viewerRadiusMiles: number,
    store: string
  ): OrderPost[] {
    const posts = this.getOrSeedLocalPosts();
    return this.sortByCreatedAt(
      posts
        .map((post) => {
          const distanceMiles = this.haversineMiles(viewerLat, viewerLng, post.latitude, post.longitude);
          const remainingAmount = Math.max(0, this.round2(post.minimumOrderAmount - post.currentCartAmount));
          return {
            ...post,
            distanceMiles: this.round2(distanceMiles),
            remainingAmount
          };
        })
        .filter((post) => store === 'All' || post.storeName.toLowerCase() === store.toLowerCase())
        .filter((post) => post.distanceMiles <= Math.min(viewerRadiusMiles, post.postRadiusMiles))
    );
  }

  private getOrSeedLocalPosts(): OrderPost[] {
    try {
      const raw = localStorage.getItem(this.localPostsKey);
      if (raw) {
        const parsed = JSON.parse(raw) as OrderPost[];
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed;
        }
      }
    } catch {
      // fall through to reseed
    }

    const seeded = this.seedLocalPosts();
    try {
      localStorage.setItem(this.localPostsKey, JSON.stringify(seeded));
    } catch {
      // ignore storage issues and still return seeded posts
    }
    return seeded;
  }

  private seedLocalPosts(): OrderPost[] {
    const now = new Date();
    return [
      this.createLocalPost(
        'demo-zara-uber',
        'Zara',
        'Uber Eats',
        37.7794,
        -122.4098,
        'SoMa food pickup',
        this.plusHoursIso(now, 1),
        20,
        16.5,
        1,
        'Need $3.50 for free delivery',
        'Ordering from a local cafe. Add-ons welcome for next 15 minutes.',
        '+1 (***) ***-7734',
        0,
        this.minutesAgoIso(now, 10)
      ),
      this.createLocalPost(
        'demo-noah-target',
        'Noah',
        'Target',
        37.7801,
        -122.4029,
        '4th St & Mission',
        this.plusHoursIso(now, 5),
        35,
        31,
        2,
        'Need $4 to close Target order',
        'Quick essentials run. I will wait 10 mins before checkout.',
        '+1 (***) ***-4467',
        0,
        this.minutesAgoIso(now, 25)
      ),
      this.createLocalPost(
        'demo-ivy-sams',
        'Ivy',
        "Sam's Club",
        37.7825,
        -122.4072,
        'Howard St pickup zone',
        this.plusHoursIso(now, 3),
        50,
        42,
        2,
        "Sam's Club order needs $8 more",
        'Looking for pantry items only. Can coordinate handoff near lobby.',
        '+1 (***) ***-3321',
        0,
        this.minutesAgoIso(now, 35)
      ),
      this.createLocalPost(
        'demo-host-walmart',
        'HostUser',
        'Walmart',
        37.781,
        -122.405,
        'Near Downtown',
        this.plusHoursIso(now, 4),
        35,
        30,
        2,
        'Need 5 dollars to unlock delivery',
        'Can split milk and eggs.',
        '+1 (***) ***-0000',
        1,
        this.minutesAgoIso(now, 55)
      )
    ];
  }

  private createLocalPost(
    id: string,
    initiatorAlias: string,
    storeName: string,
    latitude: number,
    longitude: number,
    addressHint: string,
    expectedDeliveryTime: string,
    minimumOrderAmount: number,
    currentCartAmount: number,
    postRadiusMiles: number,
    title: string,
    notes: string,
    visiblePhone: string,
    interestedCount: number,
    createdAt: string
  ): OrderPost {
    return {
      id,
      initiatorAlias,
      storeName,
      latitude,
      longitude,
      addressHint,
      expectedDeliveryTime,
      minimumOrderAmount: this.round2(minimumOrderAmount),
      currentCartAmount: this.round2(currentCartAmount),
      remainingAmount: this.round2(Math.max(0, minimumOrderAmount - currentCartAmount)),
      postRadiusMiles,
      title,
      notes,
      visiblePhone,
      phoneRevealEnabled: false,
      interestedCount,
      createdAt,
      distanceMiles: 0,
      viewerInterested: false,
      canManageContact: false
    };
  }

  private sortByCreatedAt(posts: OrderPost[]): OrderPost[] {
    return [...posts].sort((a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt));
  }

  private plusHoursIso(date: Date, hours: number): string {
    return new Date(date.getTime() + hours * 60 * 60 * 1000).toISOString().slice(0, 19);
  }

  private minutesAgoIso(date: Date, minutes: number): string {
    return new Date(date.getTime() - minutes * 60 * 1000).toISOString();
  }

  private haversineMiles(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const earthRadiusMiles = 3958.8;
    const dLat = this.toRad(lat2 - lat1);
    const dLon = this.toRad(lon2 - lon1);
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
      + Math.cos(this.toRad(lat1)) * Math.cos(this.toRad(lat2))
      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadiusMiles * c;
  }

  private toRad(value: number): number {
    return value * (Math.PI / 180);
  }

  private round2(value: number): number {
    return Math.round(value * 100) / 100;
  }
}

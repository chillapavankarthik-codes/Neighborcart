import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

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

  getPosts(lat: number, lng: number, radiusMiles: number, store: string): Observable<OrderPost[]> {
    let params = new HttpParams()
      .set('lat', `${lat}`)
      .set('lng', `${lng}`)
      .set('radiusMiles', `${radiusMiles}`);

    if (store !== 'All') {
      params = params.set('store', store);
    }

    return this.http.get<OrderPost[]>(this.baseUrl, { params });
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
}

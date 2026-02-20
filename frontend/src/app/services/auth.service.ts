import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { AuthResponse, AuthUser, RequestOtpResponse } from '../models/auth.model';

const ACCESS_TOKEN_KEY = 'neighborcart_access_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  requestOtp(phoneNumber: string): Observable<RequestOtpResponse> {
    return this.http.post<RequestOtpResponse>(`${environment.apiUrl}/auth/request-otp`, {
      phoneNumber
    });
  }

  verifyOtp(payload: { phoneNumber: string; otpCode: string; displayName: string; }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/verify-otp`, payload).pipe(
      tap((response) => this.setAccessToken(response.accessToken))
    );
  }

  me(): Observable<AuthUser> {
    return this.http.get<AuthUser>(`${environment.apiUrl}/auth/me`);
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/logout`, {}).pipe(
      tap(() => this.clearAccessToken())
    );
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  setAccessToken(token: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
  }

  clearAccessToken(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
  }
}

export interface AuthUser {
  id: string;
  displayName: string;
  phoneNumber: string;
}

export interface RequestOtpResponse {
  message: string;
  expiresAt: string;
  devOtpCode: string | null;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresAt: string;
  user: AuthUser;
}

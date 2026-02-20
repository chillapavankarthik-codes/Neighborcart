package com.ordershare.backend.dto;

import java.time.LocalDateTime;

public record AuthResponse(
        String accessToken,
        String tokenType,
        LocalDateTime expiresAt,
        AuthUserResponse user
) {
}

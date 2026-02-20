package com.ordershare.backend.dto;

import java.time.LocalDateTime;

public record RequestOtpResponse(
        String message,
        LocalDateTime expiresAt,
        String devOtpCode
) {
}

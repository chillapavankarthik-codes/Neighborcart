package com.ordershare.backend.dto;

public record AuthUserResponse(
        String id,
        String displayName,
        String phoneNumber
) {
}

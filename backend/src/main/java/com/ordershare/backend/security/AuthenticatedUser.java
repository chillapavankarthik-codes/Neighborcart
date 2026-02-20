package com.ordershare.backend.security;

public record AuthenticatedUser(
        String userId,
        String displayName,
        String phoneNumber
) {
}

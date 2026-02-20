package com.ordershare.backend.dto;

import java.time.LocalDateTime;

public record InterestedNeighborResponse(
        String userId,
        String displayName,
        String phoneNumber,
        LocalDateTime interestedAt
) {
}

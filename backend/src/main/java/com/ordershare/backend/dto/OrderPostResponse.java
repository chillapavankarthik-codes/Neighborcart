package com.ordershare.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderPostResponse(
        String id,
        String initiatorAlias,
        String storeName,
        double latitude,
        double longitude,
        String addressHint,
        LocalDateTime expectedDeliveryTime,
        BigDecimal minimumOrderAmount,
        BigDecimal currentCartAmount,
        BigDecimal remainingAmount,
        int postRadiusMiles,
        String title,
        String notes,
        String visiblePhone,
        boolean phoneRevealEnabled,
        int interestedCount,
        LocalDateTime createdAt,
        double distanceMiles,
        boolean viewerInterested,
        boolean canManageContact
) {
}

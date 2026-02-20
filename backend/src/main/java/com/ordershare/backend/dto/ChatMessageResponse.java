package com.ordershare.backend.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        String id,
        String postId,
        String senderAlias,
        String text,
        LocalDateTime sentAt
) {
}

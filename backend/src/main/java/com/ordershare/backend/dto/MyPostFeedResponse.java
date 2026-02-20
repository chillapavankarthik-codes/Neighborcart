package com.ordershare.backend.dto;

import java.util.List;

public record MyPostFeedResponse(
        OrderPostResponse post,
        List<InterestedNeighborResponse> interestedUsers
) {
}

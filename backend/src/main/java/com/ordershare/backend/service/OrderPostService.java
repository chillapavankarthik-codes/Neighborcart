package com.ordershare.backend.service;

import com.ordershare.backend.dto.CreateOrderPostRequest;
import com.ordershare.backend.dto.InterestedNeighborResponse;
import com.ordershare.backend.dto.MyPostFeedResponse;
import com.ordershare.backend.dto.OrderPostResponse;
import com.ordershare.backend.entity.AppUser;
import com.ordershare.backend.entity.OrderPost;
import com.ordershare.backend.entity.PostInterest;
import com.ordershare.backend.repository.AppUserRepository;
import com.ordershare.backend.repository.OrderPostRepository;
import com.ordershare.backend.repository.PostInterestRepository;
import com.ordershare.backend.security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderPostService {

    private final OrderPostRepository orderPostRepository;
    private final PostInterestRepository postInterestRepository;
    private final AppUserRepository appUserRepository;

    public OrderPostService(
            OrderPostRepository orderPostRepository,
            PostInterestRepository postInterestRepository,
            AppUserRepository appUserRepository
    ) {
        this.orderPostRepository = orderPostRepository;
        this.postInterestRepository = postInterestRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public OrderPostResponse createPost(CreateOrderPostRequest request, AuthenticatedUser currentUser) {
        AppUser ownerUser = appUserRepository.findById(currentUser.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        String notes = request.getNotes() == null ? "" : request.getNotes();
        String realPhone = request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()
                ? ownerUser.getPhoneNumber()
                : request.getPhoneNumber();

        OrderPost post = new OrderPost(
                ownerUser,
                request.getStoreName(),
                request.getLatitude(),
                request.getLongitude(),
                request.getAddressHint(),
                request.getExpectedDeliveryTime(),
                request.getMinimumOrderAmount(),
                request.getCurrentCartAmount(),
                request.getPostRadiusMiles(),
                request.getTitle(),
                notes,
                request.getMaskedPhone(),
                realPhone
        );

        orderPostRepository.save(post);
        return toResponse(post, 0, currentUser.userId(), false);
    }

    @Transactional
    public OrderPostResponse registerInterest(String postId, AuthenticatedUser currentUser) {
        OrderPost post = orderPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId));

        if (post.getOwnerUser().getId().equals(currentUser.userId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot join your own post");
        }

        AppUser user = appUserRepository.findById(currentUser.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        boolean alreadyInterested = postInterestRepository.existsByPost_IdAndUser_Id(postId, user.getId());
        if (!alreadyInterested) {
            postInterestRepository.save(new PostInterest(post, user));
            post.registerInterest();
            orderPostRepository.save(post);
        }

        return toResponse(post, 0, currentUser.userId(), true);
    }

    @Transactional
    public OrderPostResponse setPhoneReveal(String postId, boolean reveal, AuthenticatedUser currentUser) {
        OrderPost post = orderPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId));

        if (!post.getOwnerUser().getId().equals(currentUser.userId())) {
            throw new AccessDeniedException("Only the post owner can toggle contact reveal");
        }

        post.setPhoneRevealEnabled(reveal);
        orderPostRepository.save(post);

        boolean viewerInterested = postInterestRepository.existsByPost_IdAndUser_Id(postId, currentUser.userId());
        return toResponse(post, 0, currentUser.userId(), viewerInterested);
    }

    @Transactional
    public List<MyPostFeedResponse> getMyPosts(AuthenticatedUser currentUser) {
        List<OrderPost> posts = orderPostRepository.findAllByOwnerUser_IdOrderByCreatedAtDesc(currentUser.userId());
        if (posts.isEmpty()) {
            return List.of();
        }

        List<String> postIds = posts.stream().map(OrderPost::getId).toList();
        Map<String, List<PostInterest>> interestsByPostId = postInterestRepository.findAllByPostIdsWithUsers(postIds).stream()
                .collect(Collectors.groupingBy(
                        interest -> interest.getPost().getId(),
                        Collectors.toList()
                ));

        return posts.stream()
                .map(post -> {
                    List<InterestedNeighborResponse> interestedUsers = interestsByPostId
                            .getOrDefault(post.getId(), Collections.emptyList())
                            .stream()
                            .map(interest -> new InterestedNeighborResponse(
                                    interest.getUser().getId(),
                                    interest.getUser().getDisplayName(),
                                    interest.getUser().getPhoneNumber(),
                                    interest.getCreatedAt()
                            ))
                            .toList();
                    OrderPostResponse postSummary = toResponse(post, 0, currentUser.userId(), false);
                    return new MyPostFeedResponse(postSummary, interestedUsers);
                })
                .toList();
    }

    @Transactional
    public List<OrderPostResponse> getPosts(
            Double viewerLat,
            Double viewerLng,
            Integer viewerRadiusMiles,
            String store,
            String viewerUserId
    ) {
        List<OrderPost> posts = orderPostRepository.findAllByOrderByCreatedAtDesc();
        Set<String> viewerInterestedPostIds = viewerUserId == null
                ? Set.of()
                : postInterestRepository.findPostIdsByUserId(viewerUserId);

        return posts.stream()
                .map(post -> {
                    double distance = 0;
                    if (viewerLat != null && viewerLng != null) {
                        distance = haversineMiles(viewerLat, viewerLng, post.getLatitude(), post.getLongitude());
                    }
                    return new PostWithDistance(post, distance);
                })
                .filter(row -> store == null || store.isBlank() || row.post().getStoreName().equalsIgnoreCase(store.trim()))
                .filter(row -> {
                    if (viewerLat == null || viewerLng == null || viewerRadiusMiles == null) {
                        return true;
                    }
                    int effectiveRadius = Math.min(viewerRadiusMiles, row.post().getPostRadiusMiles());
                    return row.distanceMiles() <= effectiveRadius;
                })
                .map(row -> {
                    boolean viewerInterested = viewerInterestedPostIds.contains(row.post().getId());
                    return toResponse(row.post(), row.distanceMiles(), viewerUserId, viewerInterested);
                })
                .toList();
    }

    @Transactional
    public OrderPostResponse getById(String postId, Double viewerLat, Double viewerLng, String viewerUserId) {
        OrderPost post = orderPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId));

        double distance = 0;
        if (viewerLat != null && viewerLng != null) {
            distance = haversineMiles(viewerLat, viewerLng, post.getLatitude(), post.getLongitude());
        }

        boolean viewerInterested = viewerUserId != null
                && postInterestRepository.existsByPost_IdAndUser_Id(postId, viewerUserId);

        return toResponse(post, distance, viewerUserId, viewerInterested);
    }

    private OrderPostResponse toResponse(
            OrderPost post,
            double distanceMiles,
            String viewerUserId,
            boolean viewerInterested
    ) {
        boolean canManageContact = viewerUserId != null && viewerUserId.equals(post.getOwnerUser().getId());
        String visiblePhone = resolveVisiblePhone(post, viewerUserId, viewerInterested);

        return new OrderPostResponse(
                post.getId(),
                post.getOwnerUser().getDisplayName(),
                post.getStoreName(),
                post.getLatitude(),
                post.getLongitude(),
                post.getAddressHint(),
                post.getExpectedDeliveryTime(),
                post.getMinimumOrderAmount(),
                post.getCurrentCartAmount(),
                post.remainingAmount(),
                post.getPostRadiusMiles(),
                post.getTitle(),
                post.getNotes(),
                visiblePhone,
                post.isPhoneRevealEnabled(),
                post.getInterestedCount(),
                post.getCreatedAt(),
                BigDecimal.valueOf(distanceMiles).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                viewerInterested,
                canManageContact
        );
    }

    private String resolveVisiblePhone(OrderPost post, String viewerUserId, boolean viewerInterested) {
        if (viewerUserId != null && viewerUserId.equals(post.getOwnerUser().getId())) {
            return post.getPhoneNumber();
        }
        if (post.isPhoneRevealEnabled() && viewerInterested) {
            return post.getPhoneNumber();
        }
        return post.getMaskedPhone();
    }

    private static double haversineMiles(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusMiles = 3958.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusMiles * c;
    }

    private record PostWithDistance(OrderPost post, double distanceMiles) {
    }
}

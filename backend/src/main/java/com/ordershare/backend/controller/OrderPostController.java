package com.ordershare.backend.controller;

import com.ordershare.backend.dto.CreateOrderPostRequest;
import com.ordershare.backend.dto.MyPostFeedResponse;
import com.ordershare.backend.dto.OrderPostResponse;
import com.ordershare.backend.dto.RevealContactRequest;
import com.ordershare.backend.security.AuthenticatedUser;
import com.ordershare.backend.security.CurrentUserProvider;
import com.ordershare.backend.service.OrderPostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class OrderPostController {

    private final OrderPostService postService;
    private final CurrentUserProvider currentUserProvider;

    public OrderPostController(OrderPostService postService, CurrentUserProvider currentUserProvider) {
        this.postService = postService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public List<OrderPostResponse> listPosts(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Integer radiusMiles,
            @RequestParam(required = false) String store
    ) {
        String viewerUserId = currentUserProvider.getCurrentUser().map(AuthenticatedUser::userId).orElse(null);
        return postService.getPosts(lat, lng, radiusMiles, store, viewerUserId);
    }

    @GetMapping("/{postId}")
    public OrderPostResponse getPost(
            @PathVariable String postId,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        String viewerUserId = currentUserProvider.getCurrentUser().map(AuthenticatedUser::userId).orElse(null);
        return postService.getById(postId, lat, lng, viewerUserId);
    }

    @GetMapping("/mine")
    public List<MyPostFeedResponse> listMyPosts() {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        return postService.getMyPosts(user);
    }

    @PostMapping
    public OrderPostResponse createPost(@Valid @RequestBody CreateOrderPostRequest request) {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        return postService.createPost(request, user);
    }

    @PostMapping("/{postId}/interest")
    public OrderPostResponse registerInterest(@PathVariable String postId) {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        return postService.registerInterest(postId, user);
    }

    @PostMapping("/{postId}/reveal-contact")
    public OrderPostResponse revealContact(@PathVariable String postId, @Valid @RequestBody RevealContactRequest request) {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        return postService.setPhoneReveal(postId, request.isReveal(), user);
    }
}

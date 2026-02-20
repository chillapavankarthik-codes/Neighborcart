package com.ordershare.backend.controller;

import com.ordershare.backend.dto.ChatMessageResponse;
import com.ordershare.backend.dto.CreateChatMessageRequest;
import com.ordershare.backend.security.AuthenticatedUser;
import com.ordershare.backend.security.CurrentUserProvider;
import com.ordershare.backend.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/chat")
public class ChatController {

    private final ChatService chatService;
    private final CurrentUserProvider currentUserProvider;

    public ChatController(ChatService chatService, CurrentUserProvider currentUserProvider) {
        this.chatService = chatService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public List<ChatMessageResponse> getChat(@PathVariable String postId) {
        return chatService.listByPostId(postId);
    }

    @PostMapping
    public ChatMessageResponse sendMessage(@PathVariable String postId, @Valid @RequestBody CreateChatMessageRequest request) {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        return chatService.addMessage(postId, request, user);
    }
}

package com.ordershare.backend.service;

import com.ordershare.backend.dto.ChatMessageResponse;
import com.ordershare.backend.dto.CreateChatMessageRequest;
import com.ordershare.backend.entity.AppUser;
import com.ordershare.backend.entity.ChatMessage;
import com.ordershare.backend.entity.OrderPost;
import com.ordershare.backend.repository.AppUserRepository;
import com.ordershare.backend.repository.ChatMessageRepository;
import com.ordershare.backend.repository.OrderPostRepository;
import com.ordershare.backend.security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final OrderPostRepository orderPostRepository;
    private final AppUserRepository appUserRepository;

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            OrderPostRepository orderPostRepository,
            AppUserRepository appUserRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.orderPostRepository = orderPostRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public List<ChatMessageResponse> listByPostId(String postId) {
        return chatMessageRepository.findByPost_IdOrderBySentAtAsc(postId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChatMessageResponse addMessage(String postId, CreateChatMessageRequest request, AuthenticatedUser currentUser) {
        OrderPost post = orderPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId));

        AppUser sender = appUserRepository.findById(currentUser.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        ChatMessage message = new ChatMessage(post, sender, sender.getDisplayName(), request.getText());
        chatMessageRepository.save(message);
        return toResponse(message);
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                String.valueOf(message.getId()),
                message.getPost().getId(),
                message.getSenderAlias(),
                message.getText(),
                message.getSentAt()
        );
    }
}

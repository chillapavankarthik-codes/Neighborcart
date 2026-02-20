package com.ordershare.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private OrderPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private AppUser senderUser;

    @Column(nullable = false, length = 40)
    private String senderAlias;

    @Column(nullable = false, length = 300)
    private String text;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    protected ChatMessage() {
    }

    public ChatMessage(OrderPost post, AppUser senderUser, String senderAlias, String text) {
        this.post = post;
        this.senderUser = senderUser;
        this.senderAlias = senderAlias;
        this.text = text;
    }

    @PrePersist
    void onCreate() {
        sentAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public OrderPost getPost() {
        return post;
    }

    public AppUser getSenderUser() {
        return senderUser;
    }

    public String getSenderAlias() {
        return senderAlias;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}

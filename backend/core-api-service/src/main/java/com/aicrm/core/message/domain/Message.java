package com.aicrm.core.message.domain;

import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sender_type", nullable = false, columnDefinition = "sender_type")
    private SenderType senderType;

    @Column(name = "sender_id")
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "message_type", nullable = false, columnDefinition = "message_type")
    private MessageType messageType = MessageType.TEXT;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Message() {
    }

    public static Message customerText(Conversation conversation, Long customerId, String content) {
        ensureContent(content);
        Message message = new Message();
        message.conversation = conversation;
        message.senderType = SenderType.CUSTOMER;
        message.senderId = customerId;
        message.messageType = MessageType.TEXT;
        message.content = content.trim();
        message.createdAt = Instant.now();
        return message;
    }

    public static Message systemNotice(Conversation conversation, String content) {
        ensureContent(content);
        Message message = new Message();
        message.conversation = conversation;
        message.senderType = SenderType.SYSTEM;
        message.messageType = MessageType.TEXT;
        message.content = content.trim();
        message.createdAt = Instant.now();
        return message;
    }

    public static Message agentMessage(
            Conversation conversation,
            Long agentId,
            String content,
            MessageType messageType
    ) {
        ensureContent(content);
        MessageType resolvedType = messageType == null ? MessageType.TEXT : messageType;
        Message message = new Message();
        message.conversation = conversation;
        message.senderId = agentId;
        message.messageType = resolvedType;
        message.content = content.trim();
        message.createdAt = Instant.now();
        if (resolvedType == MessageType.AI_DRAFT) {
            message.senderType = SenderType.AI;
        } else {
            message.senderType = SenderType.AGENT;
        }
        return message;
    }

    private static void ensureContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.MESSAGE_CONTENT_REQUIRED);
        }
    }

    public Long getId() {
        return id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

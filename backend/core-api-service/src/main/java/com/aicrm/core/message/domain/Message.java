package com.aicrm.core.message.domain;

import com.aicrm.core.conversation.domain.Conversation;
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

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Message() {
    }

    public static Message customerText(Conversation conversation, Long customerId, String content) {
        Message message = new Message();
        message.conversation = conversation;
        message.senderType = SenderType.CUSTOMER;
        message.senderId = customerId;
        message.content = content;
        message.createdAt = Instant.now();
        return message;
    }

    public static Message systemNotice(Conversation conversation, String content) {
        Message message = new Message();
        message.conversation = conversation;
        message.senderType = SenderType.SYSTEM;
        message.content = content;
        message.createdAt = Instant.now();
        return message;
    }

    public static Message agentText(Conversation conversation, Long agentId, String content) {
        Message message = new Message();
        message.conversation = conversation;
        message.senderType = SenderType.AGENT;
        message.senderId = agentId;
        message.content = content;
        message.createdAt = Instant.now();
        return message;
    }

    public Long getId() {
        return id;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public String getContent() {
        return content;
    }
}

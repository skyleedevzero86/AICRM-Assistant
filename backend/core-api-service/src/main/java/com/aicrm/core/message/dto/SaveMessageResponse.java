package com.aicrm.core.message.dto;

import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.domain.SenderType;
import java.time.Instant;

public record SaveMessageResponse(
        Long messageId,
        Long ticketId,
        SenderType senderType,
        MessageType messageType,
        String content,
        Instant createdAt
) {
}

package com.aicrm.core.message.application;

import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.dto.MessageResponse;
import com.aicrm.core.message.dto.SaveMessageResponse;

final class MessageResponseMapper {

    private MessageResponseMapper() {
    }

    static MessageResponse toResponse(Message message, Long ticketId) {
        return new MessageResponse(
                message.getId(),
                ticketId,
                message.getSenderType(),
                message.getSenderId(),
                message.getMessageType(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    static SaveMessageResponse toSaveResponse(Message message, Long ticketId) {
        return new SaveMessageResponse(
                message.getId(),
                ticketId,
                message.getSenderType(),
                message.getMessageType(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

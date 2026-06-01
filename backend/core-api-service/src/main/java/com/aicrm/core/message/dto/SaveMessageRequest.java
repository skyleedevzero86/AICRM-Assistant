package com.aicrm.core.message.dto;

import com.aicrm.core.message.domain.MessageType;
import jakarta.validation.constraints.NotBlank;

public record SaveMessageRequest(
        @NotBlank String content,
        MessageType messageType
) {
}

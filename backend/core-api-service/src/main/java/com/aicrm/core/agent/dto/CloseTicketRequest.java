package com.aicrm.core.agent.dto;

import jakarta.validation.constraints.NotBlank;

public record CloseTicketRequest(
        @NotBlank(message = "해결 내용은 필수입니다")
        String resolution
) {
}

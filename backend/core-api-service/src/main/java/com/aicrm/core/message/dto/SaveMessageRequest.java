package com.aicrm.core.message.dto;

import com.aicrm.core.message.domain.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "메시지 저장 요청")
public record SaveMessageRequest(
        @Schema(description = "메시지 내용", example = "추가 문의 드립니다.")
        @NotBlank String content,
        @Schema(description = "메시지 유형", example = "TEXT")
        MessageType messageType
) {
}

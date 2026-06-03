package com.aicrm.core.message.dto;

import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.domain.SenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "메시지 저장 응답")
public record SaveMessageResponse(
        @Schema(description = "메시지 ID", example = "10")
        Long messageId,
        @Schema(description = "티켓 ID", example = "1")
        Long ticketId,
        @Schema(description = "발신자 유형", example = "CUSTOMER")
        SenderType senderType,
        @Schema(description = "메시지 유형", example = "TEXT")
        MessageType messageType,
        @Schema(description = "메시지 내용", example = "추가 문의 드립니다.")
        String content,
        @Schema(description = "생성 일시", example = "2025-06-03T11:30:00Z")
        Instant createdAt
) {
}

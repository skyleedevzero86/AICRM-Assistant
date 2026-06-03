package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "티켓 종료 응답")
public record CloseTicketResponse(
        @Schema(description = "티켓 ID", example = "3")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0003")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "CLOSED")
        TicketStatus status,
        @Schema(description = "해결 내용", example = "환불 처리가 완료되었습니다.")
        String resolution,
        @Schema(description = "종료 일시", example = "2025-06-03T14:00:00Z")
        Instant closedAt,
        @Schema(description = "대화 종료 일시", example = "2025-06-03T14:00:00Z")
        Instant conversationEndedAt
) {
}

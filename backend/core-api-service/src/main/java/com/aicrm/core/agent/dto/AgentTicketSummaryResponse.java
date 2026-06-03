package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "상담원 담당 티켓 목록 항목")
public record AgentTicketSummaryResponse(
        @Schema(description = "티켓 ID", example = "2")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0002")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "IN_PROGRESS")
        TicketStatus status,
        @Schema(description = "문의 제목", example = "결제 오류 문의")
        String subject,
        @Schema(description = "고객 이름", example = "홍길동")
        String customerName,
        @Schema(description = "카테고리명", example = "결제/청구")
        String categoryName,
        @Schema(description = "접수 채널", example = "MOBILE")
        ChannelType channel,
        @Schema(description = "생성 일시", example = "2025-06-03T08:00:00Z")
        Instant createdAt,
        @Schema(description = "종료 일시", example = "2025-06-03T16:00:00Z")
        Instant closedAt
) {
}

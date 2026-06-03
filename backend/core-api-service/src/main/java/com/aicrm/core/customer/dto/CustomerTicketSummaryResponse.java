package com.aicrm.core.customer.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "고객 티켓 목록 항목")
public record CustomerTicketSummaryResponse(
        @Schema(description = "티켓 ID", example = "1")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0001")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "IN_PROGRESS")
        TicketStatus status,
        @Schema(description = "문의 제목", example = "배송 지연 문의")
        String subject,
        @Schema(description = "카테고리명", example = "배송/교환")
        String categoryName,
        @Schema(description = "생성 일시", example = "2025-06-03T09:00:00Z")
        Instant createdAt
) {
}

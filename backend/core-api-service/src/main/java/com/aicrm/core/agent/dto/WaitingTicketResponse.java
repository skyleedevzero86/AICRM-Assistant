package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "대기 티켓 목록 항목")
public record WaitingTicketResponse(
        @Schema(description = "티켓 ID", example = "3")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0003")
        String ticketNo,
        @Schema(description = "문의 제목", example = "환불 요청")
        String subject,
        @Schema(description = "고객 이름", example = "홍길동")
        String customerName,
        @Schema(description = "카테고리명", example = "환불/취소")
        String categoryName,
        @Schema(description = "접수 채널", example = "WEB")
        ChannelType channel,
        @Schema(description = "생성 일시", example = "2025-06-03T10:00:00Z")
        Instant createdAt
) {
}

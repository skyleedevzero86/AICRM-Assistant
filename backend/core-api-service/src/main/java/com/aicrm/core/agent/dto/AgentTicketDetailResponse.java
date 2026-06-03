package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "상담원 티켓 상세 응답")
public record AgentTicketDetailResponse(
        @Schema(description = "티켓 ID", example = "2")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0002")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "IN_PROGRESS")
        TicketStatus status,
        @Schema(description = "문의 제목", example = "결제 오류 문의")
        String subject,
        @Schema(description = "접수 채널", example = "MOBILE")
        ChannelType channel,
        @Schema(description = "생성 일시", example = "2025-06-03T08:00:00Z")
        Instant createdAt,
        @Schema(description = "고객 이름", example = "홍길동")
        String customerName,
        @Schema(description = "고객 연락처", example = "010-1234-5678")
        String customerPhone,
        @Schema(description = "고객 이메일", example = "hong@example.com")
        String customerEmail,
        @Schema(description = "카테고리 ID", example = "3")
        Long categoryId,
        @Schema(description = "카테고리명", example = "결제/청구")
        String categoryName,
        @Schema(description = "문의 내용", example = "카드 결제가 두 번 되었습니다.")
        String inquiryContent,
        @Schema(description = "담당 상담원 ID", example = "2")
        Long agentId,
        @Schema(description = "해결 내용", example = "중복 결제 건 환불 처리 완료")
        String resolution,
        @Schema(description = "종료 일시", example = "2025-06-03T16:00:00Z")
        Instant closedAt
) {
}

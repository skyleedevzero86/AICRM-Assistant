package com.aicrm.core.customer.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "고객 티켓 상세 응답")
public record CustomerTicketDetailResponse(
        @Schema(description = "티켓 ID", example = "1")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0001")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "IN_PROGRESS")
        TicketStatus status,
        @Schema(description = "문의 제목", example = "배송 지연 문의")
        String subject,
        @Schema(description = "카테고리 ID", example = "5")
        Long categoryId,
        @Schema(description = "카테고리명", example = "배송/교환")
        String categoryName,
        @Schema(description = "생성 일시", example = "2025-06-03T09:00:00Z")
        Instant createdAt,
        @Schema(description = "고객 이름", example = "홍길동")
        String customerName,
        @Schema(description = "고객 연락처", example = "010-1234-5678")
        String customerPhone,
        @Schema(description = "고객 이메일", example = "hong@example.com")
        String customerEmail,
        @Schema(description = "문의 내용", example = "주문한 상품이 아직 도착하지 않았습니다.")
        String inquiryContent,
        @Schema(description = "해결 내용", example = "배송 추적 결과 내일 도착 예정입니다.")
        String resolution,
        @Schema(description = "종료 일시", example = "2025-06-03T12:00:00Z")
        Instant closedAt
) {
}

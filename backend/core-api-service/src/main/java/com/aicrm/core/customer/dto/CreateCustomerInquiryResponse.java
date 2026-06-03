package com.aicrm.core.customer.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "고객 문의 등록 응답")
public record CreateCustomerInquiryResponse(
        @Schema(description = "티켓 ID", example = "10")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0010")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "WAITING")
        TicketStatus status
) {
}

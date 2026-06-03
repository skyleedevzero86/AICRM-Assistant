package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "티켓 접수 응답")
public record AcceptTicketResponse(
        @Schema(description = "티켓 ID", example = "3")
        Long ticketId,
        @Schema(description = "티켓 번호", example = "TK-20250603-0003")
        String ticketNo,
        @Schema(description = "티켓 상태", example = "IN_PROGRESS")
        TicketStatus status,
        @Schema(description = "담당 상담원 ID", example = "2")
        Long agentId
) {
}

package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.TicketStatus;

public record AcceptTicketResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        Long agentId
) {
}

package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record CloseTicketResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        String resolution,
        Instant closedAt,
        Instant conversationEndedAt
) {
}

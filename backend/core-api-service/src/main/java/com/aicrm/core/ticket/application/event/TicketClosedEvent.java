package com.aicrm.core.ticket.application.event;

import java.time.Instant;

public record TicketClosedEvent(
        Long ticketId,
        String ticketNo,
        Long agentId,
        Long customerId,
        Long conversationId,
        String resolution,
        Instant closedAt,
        Instant conversationEndedAt
) {
}

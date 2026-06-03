package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record AgentTicketSummaryResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        String subject,
        String customerName,
        String categoryName,
        ChannelType channel,
        Instant createdAt,
        Instant closedAt
) {
}

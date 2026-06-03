package com.aicrm.core.admin.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record AdminTicketSummaryResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        String subject,
        String customerName,
        String categoryName,
        ChannelType channel,
        Long agentId,
        String agentName,
        String agentEmployeeNo,
        Instant createdAt,
        Instant closedAt
) {
}

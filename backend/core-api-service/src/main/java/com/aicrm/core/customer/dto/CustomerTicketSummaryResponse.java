package com.aicrm.core.customer.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record CustomerTicketSummaryResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        String subject,
        String categoryName,
        Instant createdAt
) {
}

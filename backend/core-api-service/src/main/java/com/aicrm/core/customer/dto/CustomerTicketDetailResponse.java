package com.aicrm.core.customer.dto;

import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record CustomerTicketDetailResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        String subject,
        Long categoryId,
        String categoryName,
        Instant createdAt,
        String customerName,
        String customerPhone,
        String customerEmail,
        String inquiryContent,
        String resolution,
        Instant closedAt
) {
}

package com.aicrm.core.admin.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record AdminTicketDetailResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status,
        String subject,
        ChannelType channel,
        Instant createdAt,
        String customerName,
        String customerPhone,
        String customerEmail,
        Long categoryId,
        String categoryName,
        String inquiryContent,
        Long agentId,
        String agentName,
        String agentEmployeeNo,
        String resolution,
        Instant closedAt
) {
}

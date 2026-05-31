package com.aicrm.core.ticket.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;

public record CreateInquiryResponse(
        Long ticketId,
        Long conversationId,
        Long messageId,
        Long categoryId,
        String categoryCode,
        String categoryName,
        TicketStatus status,
        ChannelType channel,
        String subject,
        Instant createdAt
) {
}

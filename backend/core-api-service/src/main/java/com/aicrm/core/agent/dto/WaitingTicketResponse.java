package com.aicrm.core.agent.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import java.time.Instant;

public record WaitingTicketResponse(
        Long ticketId,
        String ticketNo,
        String subject,
        String customerName,
        String categoryName,
        ChannelType channel,
        Instant createdAt
) {
}

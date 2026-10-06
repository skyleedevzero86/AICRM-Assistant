package com.aicrm.core.customer.dto;

import com.aicrm.core.ticket.domain.TicketStatus;

public record CreateCustomerInquiryResponse(
        Long ticketId,
        String ticketNo,
        TicketStatus status
) {
}

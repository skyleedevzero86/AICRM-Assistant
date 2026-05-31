package com.aicrm.core.ticket.domain;

import java.time.Instant;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Ticket getById(Long ticketId);

    long countByCreatedAtBetween(Instant start, Instant end);
}

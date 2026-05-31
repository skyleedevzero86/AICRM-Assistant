package com.aicrm.core.ticket.domain;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Ticket getById(Long ticketId);
}

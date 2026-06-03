package com.aicrm.core.ticket.domain;

import java.time.Instant;
import java.util.List;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Ticket getById(Long ticketId);

    Ticket getByIdForUpdate(Long ticketId);

    List<Ticket> findAllOrderByCreatedAtDesc();

    List<Ticket> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Ticket> findAllByAgentIdOrderByCreatedAtDesc(Long agentId);

    Ticket getByIdAndCustomerId(Long ticketId, Long customerId);

    List<Ticket> findAllByStatusOrderByCreatedAtAsc(TicketStatus status);

    long countByCreatedAtBetween(Instant start, Instant end);
}

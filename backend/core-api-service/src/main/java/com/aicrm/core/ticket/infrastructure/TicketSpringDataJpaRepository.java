package com.aicrm.core.ticket.infrastructure;

import com.aicrm.core.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSpringDataJpaRepository extends JpaRepository<Ticket, Long> {
}

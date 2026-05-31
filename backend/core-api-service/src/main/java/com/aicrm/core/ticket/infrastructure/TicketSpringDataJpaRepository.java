package com.aicrm.core.ticket.infrastructure;

import com.aicrm.core.ticket.domain.Ticket;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSpringDataJpaRepository extends JpaRepository<Ticket, Long> {

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(Instant start, Instant end);
}

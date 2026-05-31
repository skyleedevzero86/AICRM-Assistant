package com.aicrm.core.ticket.infrastructure;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTicketRepository implements TicketRepository {

    private final TicketSpringDataJpaRepository springDataJpaRepository;

    public JpaTicketRepository(TicketSpringDataJpaRepository springDataJpaRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return springDataJpaRepository.save(ticket);
    }

    @Override
    public Ticket getById(Long ticketId) {
        return springDataJpaRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.TICKET_NOT_FOUND,
                        "Ticket not found: " + ticketId
                ));
    }
}

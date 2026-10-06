package com.aicrm.core.ticket.infrastructure;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;
import java.util.List;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND, "TICKET_NOT_FOUND", ticketId));
    }

    @Override
    public Ticket getByIdForUpdate(Long ticketId) {
        return springDataJpaRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND, "TICKET_NOT_FOUND", ticketId));
    }

    @Override
    public List<Ticket> findAllOrderByCreatedAtDesc() {
        return springDataJpaRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Ticket> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId) {
        return springDataJpaRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<Ticket> findAllByAgentIdOrderByCreatedAtDesc(Long agentId) {
        return springDataJpaRepository.findAllByAgentIdOrderByCreatedAtDesc(agentId);
    }

    @Override
    public Ticket getByIdAndCustomerId(Long ticketId, Long customerId) {
        return springDataJpaRepository.findByIdAndCustomerId(ticketId, customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND, "TICKET_NOT_FOUND", ticketId));
    }

    @Override
    public List<Ticket> findAllByStatusOrderByCreatedAtAsc(TicketStatus status) {
        return springDataJpaRepository.findAllByStatusOrderByCreatedAtAsc(status);
    }

    @Override
    public long countByCreatedAtBetween(Instant start, Instant end) {
        return springDataJpaRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end);
    }
}

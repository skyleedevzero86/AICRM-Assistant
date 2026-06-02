package com.aicrm.core.customer.application;

import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.message.application.GetTicketMessagesService;
import com.aicrm.core.message.domain.SenderType;
import com.aicrm.core.message.dto.MessageResponse;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerTicketsService {

    private final TicketRepository ticketRepository;
    private final GetTicketMessagesService getTicketMessagesService;
    private final CurrentUserProvider currentUserProvider;

    public GetCustomerTicketsService(
            TicketRepository ticketRepository,
            GetTicketMessagesService getTicketMessagesService,
            CurrentUserProvider currentUserProvider
    ) {
        this.ticketRepository = ticketRepository;
        this.getTicketMessagesService = getTicketMessagesService;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public List<CustomerTicketSummaryResponse> getTickets() {
        Long userId = requireCustomerUserId();
        return ticketRepository.findAllByCustomerUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerTicketDetailResponse getTicket(Long ticketId) {
        Long userId = requireCustomerUserId();
        Ticket ticket = ticketRepository.getByIdAndCustomerUserId(ticketId, userId);
        String inquiryContent = getTicketMessagesService.getMessages(ticketId).stream()
                .filter(message -> message.senderType() == SenderType.CUSTOMER)
                .min(Comparator.comparing(MessageResponse::createdAt))
                .map(MessageResponse::content)
                .orElse("");

        return new CustomerTicketDetailResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getCategory().getId(),
                ticket.getCategory().getName(),
                ticket.getCreatedAt(),
                ticket.getCustomer().getName(),
                ticket.getCustomer().getPhone(),
                ticket.getCustomer().getEmail(),
                inquiryContent,
                ticket.getResolution(),
                ticket.getClosedAt()
        );
    }

    private CustomerTicketSummaryResponse toSummary(Ticket ticket) {
        return new CustomerTicketSummaryResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getCategory().getName(),
                ticket.getCreatedAt()
        );
    }

    private Long requireCustomerUserId() {
        AuthenticatedUser user = currentUserProvider.require();
        if (user.role() != UserRole.CUSTOMER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return user.userId();
    }
}

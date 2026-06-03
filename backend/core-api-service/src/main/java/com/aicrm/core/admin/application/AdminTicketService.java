package com.aicrm.core.admin.application;

import com.aicrm.core.admin.dto.AdminTicketDetailResponse;
import com.aicrm.core.admin.dto.AdminTicketSummaryResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminTicketService {

    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public AdminTicketService(
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminTicketSummaryResponse> getTickets(TicketStatus status) {
        List<Ticket> tickets = status == null
                ? ticketRepository.findAllOrderByCreatedAtDesc()
                : ticketRepository.findAllByStatusOrderByCreatedAtAsc(status);
        return tickets.stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public AdminTicketDetailResponse getTicket(Long ticketId) {
        Ticket ticket = ticketRepository.getById(ticketId);
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        String inquiryContent = messageRepository.findFirstCustomerMessageByConversationId(conversation.getId())
                .map(Message::getContent)
                .orElse("");
        Customer customer = ticket.getCustomer();
        ConsultationCategory category = ticket.getCategory();
        return new AdminTicketDetailResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getChannel(),
                ticket.getCreatedAt(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null,
                inquiryContent,
                ticket.getAgentId(),
                ticket.getResolution(),
                ticket.getClosedAt()
        );
    }

    private AdminTicketSummaryResponse toSummary(Ticket ticket) {
        ConsultationCategory category = ticket.getCategory();
        return new AdminTicketSummaryResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getCustomer().getName(),
                category != null ? category.getName() : "",
                ticket.getChannel(),
                ticket.getAgentId(),
                ticket.getCreatedAt(),
                ticket.getClosedAt()
        );
    }
}

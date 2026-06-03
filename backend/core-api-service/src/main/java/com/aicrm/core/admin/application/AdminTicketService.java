package com.aicrm.core.admin.application;

import com.aicrm.core.admin.dto.AdminTicketDetailResponse;
import com.aicrm.core.admin.dto.AdminTicketSummaryResponse;
import com.aicrm.core.auth.domain.AgentAccount;
import com.aicrm.core.auth.infrastructure.AgentAccountRepository;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminTicketService {

    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AgentAccountRepository agentAccountRepository;

    public AdminTicketService(
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            AgentAccountRepository agentAccountRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.agentAccountRepository = agentAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminTicketSummaryResponse> getTickets(TicketStatus status) {
        List<Ticket> tickets = status == null
                ? ticketRepository.findAllOrderByCreatedAtDesc()
                : ticketRepository.findAllByStatusOrderByCreatedAtAsc(status);
        Map<Long, AgentAccount> agentsById = loadAgentsById(tickets);
        return tickets.stream().map(ticket -> toSummary(ticket, agentsById.get(ticket.getAgentId()))).toList();
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
        AgentAccount agent = ticket.getAgentId() == null
                ? null
                : agentAccountRepository.findById(ticket.getAgentId()).orElse(null);
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
                agent != null ? agent.getName() : null,
                agent != null ? agent.getEmployeeNo() : null,
                ticket.getResolution(),
                ticket.getClosedAt()
        );
    }

    private AdminTicketSummaryResponse toSummary(Ticket ticket, AgentAccount agent) {
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
                agent != null ? agent.getName() : null,
                agent != null ? agent.getEmployeeNo() : null,
                ticket.getCreatedAt(),
                ticket.getClosedAt()
        );
    }

    private Map<Long, AgentAccount> loadAgentsById(List<Ticket> tickets) {
        List<Long> agentIds = tickets.stream()
                .map(Ticket::getAgentId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (agentIds.isEmpty()) {
            return Map.of();
        }
        return agentAccountRepository.findAllById(agentIds).stream()
                .collect(Collectors.toMap(AgentAccount::getId, Function.identity()));
    }
}

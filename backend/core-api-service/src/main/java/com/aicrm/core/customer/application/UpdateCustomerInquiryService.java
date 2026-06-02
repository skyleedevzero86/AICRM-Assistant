package com.aicrm.core.customer.application;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.category.domain.ConsultationCategoryRepository;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.UpdateCustomerInquiryRequest;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCustomerInquiryService {

    private final TicketRepository ticketRepository;
    private final ConsultationCategoryRepository consultationCategoryRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final GetCustomerTicketsService getCustomerTicketsService;
    private final CurrentUserProvider currentUserProvider;

    public UpdateCustomerInquiryService(
            TicketRepository ticketRepository,
            ConsultationCategoryRepository consultationCategoryRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            GetCustomerTicketsService getCustomerTicketsService,
            CurrentUserProvider currentUserProvider
    ) {
        this.ticketRepository = ticketRepository;
        this.consultationCategoryRepository = consultationCategoryRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.getCustomerTicketsService = getCustomerTicketsService;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public CustomerTicketDetailResponse update(Long ticketId, UpdateCustomerInquiryRequest request) {
        Long userId = currentUserProvider.require().userId();
        Ticket ticket = ticketRepository.getByIdAndCustomerUserId(ticketId, userId);
        ConsultationCategory category = consultationCategoryRepository.getEnabledLeafCategory(request.categoryId());

        ticket.updateWaitingInquiry(category, request.title());
        ticket.getCustomer().updateProfile(request.customerName(), request.email());

        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        Message inquiryMessage = messageRepository.findFirstCustomerMessageByConversationId(conversation.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "문의 메시지를 찾을 수 없습니다: " + ticketId
                ));
        inquiryMessage.updateContent(request.content());

        return getCustomerTicketsService.getTicket(ticketId);
    }
}

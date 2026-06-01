package com.aicrm.core.message.controller;

import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.message.application.GetTicketMessagesService;
import com.aicrm.core.message.dto.MessageResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketMessageController {

    private final GetTicketMessagesService getTicketMessagesService;

    public TicketMessageController(GetTicketMessagesService getTicketMessagesService) {
        this.getTicketMessagesService = getTicketMessagesService;
    }

    @GetMapping("/{ticketId}/messages")
    public ApiResponse<List<MessageResponse>> getMessages(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getTicketMessagesService.getMessages(ticketId));
    }
}

package com.aicrm.core.agent.controller;

import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.message.application.SaveAgentTicketMessageService;
import com.aicrm.core.message.dto.SaveMessageRequest;
import com.aicrm.core.message.dto.SaveMessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent/tickets")
public class AgentTicketMessageController {

    private final SaveAgentTicketMessageService saveAgentTicketMessageService;

    public AgentTicketMessageController(SaveAgentTicketMessageService saveAgentTicketMessageService) {
        this.saveAgentTicketMessageService = saveAgentTicketMessageService;
    }

    @PostMapping("/{ticketId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaveMessageResponse> saveMessage(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody SaveMessageRequest request
    ) {
        return ApiResponse.ok(saveAgentTicketMessageService.save(ticketId, request));
    }
}

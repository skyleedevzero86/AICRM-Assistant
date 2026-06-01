package com.aicrm.core.agent.controller;

import com.aicrm.core.agent.application.AcceptTicketService;
import com.aicrm.core.agent.application.CloseTicketService;
import com.aicrm.core.agent.application.GetAgentTicketDetailService;
import com.aicrm.core.agent.application.GetWaitingTicketsService;
import com.aicrm.core.agent.dto.AcceptTicketResponse;
import com.aicrm.core.agent.dto.AgentTicketDetailResponse;
import com.aicrm.core.agent.dto.CloseTicketRequest;
import com.aicrm.core.agent.dto.CloseTicketResponse;
import com.aicrm.core.agent.dto.WaitingTicketResponse;
import com.aicrm.core.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent/tickets")
public class AgentTicketController {

    private final GetWaitingTicketsService getWaitingTicketsService;
    private final GetAgentTicketDetailService getAgentTicketDetailService;
    private final AcceptTicketService acceptTicketService;
    private final CloseTicketService closeTicketService;

    public AgentTicketController(
            GetWaitingTicketsService getWaitingTicketsService,
            GetAgentTicketDetailService getAgentTicketDetailService,
            AcceptTicketService acceptTicketService,
            CloseTicketService closeTicketService
    ) {
        this.getWaitingTicketsService = getWaitingTicketsService;
        this.getAgentTicketDetailService = getAgentTicketDetailService;
        this.acceptTicketService = acceptTicketService;
        this.closeTicketService = closeTicketService;
    }

    @GetMapping("/waiting")
    public ApiResponse<List<WaitingTicketResponse>> getWaitingTickets() {
        return ApiResponse.ok(getWaitingTicketsService.getWaitingTickets());
    }

    @GetMapping("/{ticketId}")
    public ApiResponse<AgentTicketDetailResponse> getTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getAgentTicketDetailService.getDetail(ticketId));
    }

    @PostMapping("/{ticketId}/accept")
    public ApiResponse<AcceptTicketResponse> acceptTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(acceptTicketService.accept(ticketId));
    }

    @PostMapping("/{ticketId}/close")
    public ApiResponse<CloseTicketResponse> closeTicket(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody CloseTicketRequest request
    ) {
        return ApiResponse.ok(closeTicketService.close(ticketId, request));
    }
}

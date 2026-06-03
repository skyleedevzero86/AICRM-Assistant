package com.aicrm.core.agent.controller;

import com.aicrm.core.agent.application.AcceptTicketService;
import com.aicrm.core.agent.application.CloseTicketService;
import com.aicrm.core.agent.application.GetAgentTicketDetailService;
import com.aicrm.core.agent.application.GetAgentTicketsService;
import com.aicrm.core.agent.application.GetWaitingTicketsService;
import com.aicrm.core.agent.dto.AcceptTicketResponse;
import com.aicrm.core.agent.dto.AgentTicketDetailResponse;
import com.aicrm.core.agent.dto.AgentTicketSummaryResponse;
import com.aicrm.core.agent.dto.CloseTicketRequest;
import com.aicrm.core.agent.dto.CloseTicketResponse;
import com.aicrm.core.agent.dto.WaitingTicketResponse;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상담원", description = "상담원 티켓 접수, 처리, 종료 API")
@RestController
@RequestMapping("/api/agent/tickets")
public class AgentTicketController {

    private final GetWaitingTicketsService getWaitingTicketsService;
    private final GetAgentTicketsService getAgentTicketsService;
    private final GetAgentTicketDetailService getAgentTicketDetailService;
    private final AcceptTicketService acceptTicketService;
    private final CloseTicketService closeTicketService;

    public AgentTicketController(
            GetWaitingTicketsService getWaitingTicketsService,
            GetAgentTicketsService getAgentTicketsService,
            GetAgentTicketDetailService getAgentTicketDetailService,
            AcceptTicketService acceptTicketService,
            CloseTicketService closeTicketService
    ) {
        this.getWaitingTicketsService = getWaitingTicketsService;
        this.getAgentTicketsService = getAgentTicketsService;
        this.getAgentTicketDetailService = getAgentTicketDetailService;
        this.acceptTicketService = acceptTicketService;
        this.closeTicketService = closeTicketService;
    }

    @Operation(summary = "대기 티켓 목록 조회", description = "아직 배정되지 않은 대기 중인 티켓 목록을 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping("/waiting")
    public ApiResponse<List<WaitingTicketResponse>> getWaitingTickets() {
        return ApiResponse.ok(getWaitingTicketsService.getWaitingTickets());
    }

    @Operation(summary = "내 담당 티켓 목록 조회", description = "로그인한 상담원이 담당 중인 티켓 목록을 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping
    public ApiResponse<List<AgentTicketSummaryResponse>> getMyTickets() {
        return ApiResponse.ok(getAgentTicketsService.getMyTickets());
    }

    @Operation(summary = "티켓 상세 조회", description = "티켓 ID로 상세 정보를 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping("/{ticketId}")
    public ApiResponse<AgentTicketDetailResponse> getTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getAgentTicketDetailService.getDetail(ticketId));
    }

    @Operation(summary = "티켓 접수", description = "대기 중인 티켓을 상담원이 접수합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PostMapping("/{ticketId}/accept")
    public ApiResponse<AcceptTicketResponse> acceptTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(acceptTicketService.accept(ticketId));
    }

    @Operation(summary = "티켓 종료", description = "상담을 완료하고 티켓을 종료합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PostMapping("/{ticketId}/close")
    public ApiResponse<CloseTicketResponse> closeTicket(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody CloseTicketRequest request
    ) {
        return ApiResponse.ok(closeTicketService.close(ticketId, request));
    }
}

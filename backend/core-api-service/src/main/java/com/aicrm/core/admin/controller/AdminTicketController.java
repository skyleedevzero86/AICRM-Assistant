package com.aicrm.core.admin.controller;

import com.aicrm.core.admin.application.AdminTicketService;
import com.aicrm.core.admin.dto.AdminTicketDetailResponse;
import com.aicrm.core.admin.dto.AdminTicketSummaryResponse;
import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminTicketController {

    private final AdminTicketService adminTicketService;

    public AdminTicketController(AdminTicketService adminTicketService) {
        this.adminTicketService = adminTicketService;
    }

    @GetMapping
    public ApiResponse<List<AdminTicketSummaryResponse>> getTickets(
            @RequestParam(value = "status", required = false) TicketStatus status
    ) {
        return ApiResponse.ok(adminTicketService.getTickets(status));
    }

    @GetMapping("/{ticketId}")
    public ApiResponse<AdminTicketDetailResponse> getTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(adminTicketService.getTicket(ticketId));
    }
}

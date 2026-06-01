package com.aicrm.core.customer.controller;

import com.aicrm.core.customer.application.GetCustomerTicketsService;
import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.global.response.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/tickets")
public class CustomerTicketController {

    private final GetCustomerTicketsService getCustomerTicketsService;

    public CustomerTicketController(GetCustomerTicketsService getCustomerTicketsService) {
        this.getCustomerTicketsService = getCustomerTicketsService;
    }

    @GetMapping
    public ApiResponse<List<CustomerTicketSummaryResponse>> getTickets() {
        return ApiResponse.ok(getCustomerTicketsService.getTickets());
    }

    @GetMapping("/{ticketId}")
    public ApiResponse<CustomerTicketDetailResponse> getTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getCustomerTicketsService.getTicket(ticketId));
    }
}

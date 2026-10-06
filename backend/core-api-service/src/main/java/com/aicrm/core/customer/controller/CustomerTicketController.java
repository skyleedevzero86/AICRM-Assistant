package com.aicrm.core.customer.controller;

import com.aicrm.core.customer.application.GetCustomerTicketsService;
import com.aicrm.core.customer.application.UpdateCustomerInquiryService;
import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.customer.dto.UpdateCustomerInquiryRequest;
import com.aicrm.core.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/tickets")
public class CustomerTicketController {

    private final GetCustomerTicketsService getCustomerTicketsService;
    private final UpdateCustomerInquiryService updateCustomerInquiryService;

    public CustomerTicketController(
            GetCustomerTicketsService getCustomerTicketsService,
            UpdateCustomerInquiryService updateCustomerInquiryService
    ) {
        this.getCustomerTicketsService = getCustomerTicketsService;
        this.updateCustomerInquiryService = updateCustomerInquiryService;
    }

    @GetMapping
    public ApiResponse<List<CustomerTicketSummaryResponse>> getTickets() {
        return ApiResponse.ok(getCustomerTicketsService.getTickets());
    }

    @GetMapping("/{ticketId}")
    public ApiResponse<CustomerTicketDetailResponse> getTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getCustomerTicketsService.getTicket(ticketId));
    }

    @PutMapping("/{ticketId}")
    public ApiResponse<CustomerTicketDetailResponse> updateTicket(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody UpdateCustomerInquiryRequest request
    ) {
        return ApiResponse.ok(updateCustomerInquiryService.update(ticketId, request));
    }
}

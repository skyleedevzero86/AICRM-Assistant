package com.aicrm.core.customer.controller;

import com.aicrm.core.customer.application.GetCustomerTicketsService;
import com.aicrm.core.customer.application.UpdateCustomerInquiryService;
import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.customer.dto.UpdateCustomerInquiryRequest;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "고객", description = "고객 문의 등록 및 티켓 관리 API")
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

    @Operation(summary = "내 티켓 목록 조회", description = "로그인한 고객의 티켓 목록을 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping
    public ApiResponse<List<CustomerTicketSummaryResponse>> getTickets() {
        return ApiResponse.ok(getCustomerTicketsService.getTickets());
    }

    @Operation(summary = "티켓 상세 조회", description = "티켓 ID로 상세 정보를 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping("/{ticketId}")
    public ApiResponse<CustomerTicketDetailResponse> getTicket(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getCustomerTicketsService.getTicket(ticketId));
    }

    @Operation(summary = "티켓 문의 수정", description = "WAITING 상태의 티켓 문의 내용을 수정합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PutMapping("/{ticketId}")
    public ApiResponse<CustomerTicketDetailResponse> updateTicket(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody UpdateCustomerInquiryRequest request
    ) {
        return ApiResponse.ok(updateCustomerInquiryService.update(ticketId, request));
    }
}

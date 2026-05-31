package com.aicrm.core.ticket.controller;

import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.ticket.application.CreateCustomerInquiryService;
import com.aicrm.core.ticket.dto.CreateInquiryRequest;
import com.aicrm.core.ticket.dto.CreateInquiryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets/inquiries")
public class CustomerInquiryController {

    private final CreateCustomerInquiryService createCustomerInquiryService;

    public CustomerInquiryController(CreateCustomerInquiryService createCustomerInquiryService) {
        this.createCustomerInquiryService = createCustomerInquiryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateInquiryResponse> create(@Valid @RequestBody CreateInquiryRequest request) {
        return ApiResponse.ok(createCustomerInquiryService.create(request));
    }
}

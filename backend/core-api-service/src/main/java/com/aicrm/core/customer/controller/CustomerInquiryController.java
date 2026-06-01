package com.aicrm.core.customer.controller;

import com.aicrm.core.customer.application.CreateCustomerInquiryService;
import com.aicrm.core.customer.dto.CreateCustomerInquiryRequest;
import com.aicrm.core.customer.dto.CreateCustomerInquiryResponse;
import com.aicrm.core.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/inquiries")
public class CustomerInquiryController {

    private final CreateCustomerInquiryService createCustomerInquiryService;

    public CustomerInquiryController(CreateCustomerInquiryService createCustomerInquiryService) {
        this.createCustomerInquiryService = createCustomerInquiryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateCustomerInquiryResponse> create(@Valid @RequestBody CreateCustomerInquiryRequest request) {
        return ApiResponse.ok(createCustomerInquiryService.create(request));
    }
}

package com.aicrm.core.customer.controller;

import com.aicrm.core.customer.application.CreateCustomerInquiryService;
import com.aicrm.core.customer.dto.CreateCustomerInquiryRequest;
import com.aicrm.core.customer.dto.CreateCustomerInquiryResponse;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "고객", description = "고객 문의 등록 및 티켓 관리 API")
@RestController
@RequestMapping("/api/customer/inquiries")
public class CustomerInquiryController {

    private final CreateCustomerInquiryService createCustomerInquiryService;

    public CustomerInquiryController(CreateCustomerInquiryService createCustomerInquiryService) {
        this.createCustomerInquiryService = createCustomerInquiryService;
    }

    @Operation(summary = "문의 등록", description = "새 상담 문의 티켓을 생성합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateCustomerInquiryResponse> create(@Valid @RequestBody CreateCustomerInquiryRequest request) {
        return ApiResponse.ok(createCustomerInquiryService.create(request));
    }
}

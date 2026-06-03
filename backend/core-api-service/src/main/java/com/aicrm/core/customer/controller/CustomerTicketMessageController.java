package com.aicrm.core.customer.controller;

import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.message.application.SaveCustomerTicketMessageService;
import com.aicrm.core.message.dto.SaveMessageRequest;
import com.aicrm.core.message.dto.SaveMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "고객", description = "고객 문의 등록 및 티켓 관리 API")
@RestController
@RequestMapping("/api/customer/tickets")
public class CustomerTicketMessageController {

    private final SaveCustomerTicketMessageService saveCustomerTicketMessageService;

    public CustomerTicketMessageController(SaveCustomerTicketMessageService saveCustomerTicketMessageService) {
        this.saveCustomerTicketMessageService = saveCustomerTicketMessageService;
    }

    @Operation(summary = "티켓 메시지 전송", description = "고객이 티켓 대화에 메시지를 추가합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PostMapping("/{ticketId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaveMessageResponse> saveMessage(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody SaveMessageRequest request
    ) {
        return ApiResponse.ok(saveCustomerTicketMessageService.save(ticketId, request));
    }
}

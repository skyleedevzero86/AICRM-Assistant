package com.aicrm.core.customer.controller;

import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.message.application.SaveCustomerTicketMessageService;
import com.aicrm.core.message.dto.SaveMessageRequest;
import com.aicrm.core.message.dto.SaveMessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/tickets")
public class CustomerTicketMessageController {

    private final SaveCustomerTicketMessageService saveCustomerTicketMessageService;

    public CustomerTicketMessageController(SaveCustomerTicketMessageService saveCustomerTicketMessageService) {
        this.saveCustomerTicketMessageService = saveCustomerTicketMessageService;
    }

    @PostMapping("/{ticketId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaveMessageResponse> saveMessage(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody SaveMessageRequest request
    ) {
        return ApiResponse.ok(saveCustomerTicketMessageService.save(ticketId, request));
    }
}

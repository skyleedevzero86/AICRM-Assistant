package com.aicrm.core.attachment.controller;

import com.aicrm.core.attachment.application.UploadCustomerTicketAttachmentService;
import com.aicrm.core.attachment.dto.UploadAttachmentResponse;
import com.aicrm.core.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer/tickets")
public class CustomerTicketAttachmentController {

    private final UploadCustomerTicketAttachmentService uploadCustomerTicketAttachmentService;

    public CustomerTicketAttachmentController(
            UploadCustomerTicketAttachmentService uploadCustomerTicketAttachmentService
    ) {
        this.uploadCustomerTicketAttachmentService = uploadCustomerTicketAttachmentService;
    }

    @PostMapping("/{ticketId}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UploadAttachmentResponse> upload(
            @PathVariable("ticketId") Long ticketId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "messageId", required = false) Long messageId
    ) {
        return ApiResponse.ok(uploadCustomerTicketAttachmentService.upload(ticketId, file, messageId));
    }
}

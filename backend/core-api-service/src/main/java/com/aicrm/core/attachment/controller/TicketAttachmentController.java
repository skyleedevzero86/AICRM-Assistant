package com.aicrm.core.attachment.controller;

import com.aicrm.core.attachment.application.GetTicketAttachmentsService;
import com.aicrm.core.attachment.dto.AttachmentResponse;
import com.aicrm.core.global.response.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketAttachmentController {

    private final GetTicketAttachmentsService getTicketAttachmentsService;

    public TicketAttachmentController(GetTicketAttachmentsService getTicketAttachmentsService) {
        this.getTicketAttachmentsService = getTicketAttachmentsService;
    }

    @GetMapping("/{ticketId}/attachments")
    public ApiResponse<List<AttachmentResponse>> getAttachments(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getTicketAttachmentsService.getAttachments(ticketId));
    }
}

package com.aicrm.core.attachment.controller;

import com.aicrm.core.attachment.application.GetTicketAttachmentsService;
import com.aicrm.core.attachment.dto.AttachmentResponse;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "첨부파일", description = "티켓 첨부파일 업로드, 조회, 다운로드 URL API")
@RestController
@RequestMapping("/api/tickets")
public class TicketAttachmentController {

    private final GetTicketAttachmentsService getTicketAttachmentsService;

    public TicketAttachmentController(GetTicketAttachmentsService getTicketAttachmentsService) {
        this.getTicketAttachmentsService = getTicketAttachmentsService;
    }

    @Operation(summary = "티켓 첨부파일 목록 조회", description = "티켓에 업로드된 첨부파일 목록을 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping("/{ticketId}/attachments")
    public ApiResponse<List<AttachmentResponse>> getAttachments(@PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(getTicketAttachmentsService.getAttachments(ticketId));
    }
}

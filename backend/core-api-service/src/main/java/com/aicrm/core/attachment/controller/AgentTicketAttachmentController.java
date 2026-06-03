package com.aicrm.core.attachment.controller;

import com.aicrm.core.attachment.application.UploadAgentTicketAttachmentService;
import com.aicrm.core.attachment.dto.UploadAttachmentResponse;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "첨부파일", description = "티켓 첨부파일 업로드, 조회, 다운로드 URL API")
@RestController
@RequestMapping("/api/agent/tickets")
public class AgentTicketAttachmentController {

    private final UploadAgentTicketAttachmentService uploadAgentTicketAttachmentService;

    public AgentTicketAttachmentController(UploadAgentTicketAttachmentService uploadAgentTicketAttachmentService) {
        this.uploadAgentTicketAttachmentService = uploadAgentTicketAttachmentService;
    }

    @Operation(summary = "상담원 첨부파일 업로드", description = "상담원이 티켓에 파일을 업로드합니다. 최대 10MB, 허용 확장자: jpg, png, pdf 등")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PostMapping(value = "/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UploadAttachmentResponse> upload(
            @PathVariable("ticketId") Long ticketId,
            @Parameter(description = "업로드할 파일", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "연결할 메시지 ID (선택)")
            @RequestParam(value = "messageId", required = false) Long messageId
    ) {
        return ApiResponse.ok(uploadAgentTicketAttachmentService.upload(ticketId, file, messageId));
    }
}

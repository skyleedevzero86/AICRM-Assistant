package com.aicrm.core.attachment.controller;

import com.aicrm.core.attachment.application.GetAttachmentDownloadUrlService;
import com.aicrm.core.attachment.dto.AttachmentDownloadUrlResponse;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "첨부파일", description = "티켓 첨부파일 업로드, 조회, 다운로드 URL API")
@RestController
@RequestMapping("/api/attachments")
public class AttachmentDownloadController {

    private final GetAttachmentDownloadUrlService getAttachmentDownloadUrlService;

    public AttachmentDownloadController(GetAttachmentDownloadUrlService getAttachmentDownloadUrlService) {
        this.getAttachmentDownloadUrlService = getAttachmentDownloadUrlService;
    }

    @Operation(summary = "다운로드 URL 조회", description = "첨부파일의 사전 서명 다운로드 URL을 발급합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping("/{attachmentId}/download-url")
    public ApiResponse<AttachmentDownloadUrlResponse> getDownloadUrl(
            @PathVariable("attachmentId") Long attachmentId
    ) {
        return ApiResponse.ok(getAttachmentDownloadUrlService.getDownloadUrl(attachmentId));
    }
}

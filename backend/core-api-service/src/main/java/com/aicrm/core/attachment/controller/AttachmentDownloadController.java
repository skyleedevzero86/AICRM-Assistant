package com.aicrm.core.attachment.controller;

import com.aicrm.core.attachment.application.GetAttachmentDownloadUrlService;
import com.aicrm.core.attachment.dto.AttachmentDownloadUrlResponse;
import com.aicrm.core.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentDownloadController {

    private final GetAttachmentDownloadUrlService getAttachmentDownloadUrlService;

    public AttachmentDownloadController(GetAttachmentDownloadUrlService getAttachmentDownloadUrlService) {
        this.getAttachmentDownloadUrlService = getAttachmentDownloadUrlService;
    }

    @GetMapping("/{attachmentId}/download-url")
    public ApiResponse<AttachmentDownloadUrlResponse> getDownloadUrl(
            @PathVariable("attachmentId") Long attachmentId
    ) {
        return ApiResponse.ok(getAttachmentDownloadUrlService.getDownloadUrl(attachmentId));
    }
}

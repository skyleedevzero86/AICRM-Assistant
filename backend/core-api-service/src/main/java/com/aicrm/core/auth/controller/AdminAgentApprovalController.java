package com.aicrm.core.auth.controller;

import com.aicrm.core.auth.application.AuthService;
import com.aicrm.core.global.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/agents")
public class AdminAgentApprovalController {

    private final AuthService authService;

    public AdminAgentApprovalController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/{agentId}/approve")
    public ApiResponse<Void> approve(@PathVariable("agentId") Long agentId) {
        authService.approveAgent(agentId);
        return ApiResponse.ok();
    }
}

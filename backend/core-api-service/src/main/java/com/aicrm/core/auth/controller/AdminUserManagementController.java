package com.aicrm.core.auth.controller;

import com.aicrm.core.auth.application.AdminUserManagementService;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.AgentGrade;
import com.aicrm.core.auth.dto.AdminAgentUserResponse;
import com.aicrm.core.auth.dto.AdminCustomerUserResponse;
import com.aicrm.core.auth.dto.AdminUpdateAgentRequest;
import com.aicrm.core.auth.dto.AdminUpdateCustomerRequest;
import com.aicrm.core.auth.dto.UserStatusUpdateRequest;
import com.aicrm.core.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;

    public AdminUserManagementController(AdminUserManagementService adminUserManagementService) {
        this.adminUserManagementService = adminUserManagementService;
    }

    @GetMapping("/customers")
    public ApiResponse<List<AdminCustomerUserResponse>> customers(
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.ok(adminUserManagementService.getCustomerUsers(keyword));
    }

    @GetMapping("/agents")
    public ApiResponse<List<AdminAgentUserResponse>> agents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "approvalStatus", required = false) AgentAccountStatus approvalStatus,
            @RequestParam(value = "grade", required = false) AgentGrade grade
    ) {
        return ApiResponse.ok(adminUserManagementService.getAgentUsers(keyword, approvalStatus, grade));
    }

    @PatchMapping("/customers/{userId}")
    public ApiResponse<AdminCustomerUserResponse> updateCustomer(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody AdminUpdateCustomerRequest request
    ) {
        return ApiResponse.ok(adminUserManagementService.updateCustomerUser(userId, request));
    }

    @PatchMapping("/agents/{userId}")
    public ApiResponse<AdminAgentUserResponse> updateAgent(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody AdminUpdateAgentRequest request
    ) {
        return ApiResponse.ok(adminUserManagementService.updateAgentUser(userId, request));
    }

    @PostMapping("/{userId}/suspension")
    public ApiResponse<Void> suspension(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        adminUserManagementService.updateSuspendedYn(userId, request.value());
        return ApiResponse.ok();
    }

    @PostMapping("/{userId}/withdrawal")
    public ApiResponse<Void> withdrawal(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        adminUserManagementService.updateWithdrawnYn(userId, request.value());
        return ApiResponse.ok();
    }
}

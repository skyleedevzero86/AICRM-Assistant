package com.aicrm.core.auth.controller;

import com.aicrm.core.auth.application.AuthService;
import com.aicrm.core.auth.dto.AgentSignUpRequest;
import com.aicrm.core.auth.dto.LoginRequest;
import com.aicrm.core.auth.dto.LoginResponse;
import com.aicrm.core.auth.dto.MeResponse;
import com.aicrm.core.auth.dto.SignUpRequest;
import com.aicrm.core.auth.dto.SignUpResponse;
import com.aicrm.core.auth.dto.UpdateMeRequest;
import com.aicrm.core.global.config.OpenApiConfig;
import com.aicrm.core.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "로그인, 회원가입, 내 정보 조회 및 수정 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @Operation(summary = "고객 회원가입", description = "고객 계정을 생성합니다. 가입 즉시 ACTIVE 상태로 활성화됩니다.")
    @PostMapping("/signup/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignUpResponse> signupCustomer(@Valid @RequestBody SignUpRequest request) {
        return ApiResponse.ok(authService.signupCustomer(request));
    }

    @Operation(summary = "상담원 회원가입", description = "상담원 계정을 생성합니다. 관리자 승인 후 ACTIVE 상태가 됩니다.")
    @PostMapping("/signup/agent")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignUpResponse> signupAgent(@Valid @RequestBody AgentSignUpRequest request) {
        return ApiResponse.ok(authService.signupAgent(request));
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @GetMapping("/me")
    public ApiResponse<MeResponse> me() {
        return ApiResponse.ok(authService.me());
    }

    @Operation(summary = "내 정보 수정", description = "이름, 비밀번호, 전화번호를 수정합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PatchMapping("/me")
    public ApiResponse<MeResponse> updateMe(@Valid @RequestBody UpdateMeRequest request) {
        return ApiResponse.ok(authService.updateCurrentUser(request));
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자 계정을 탈퇴 처리합니다.")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw() {
        authService.withdrawCurrentUser();
        return ApiResponse.ok();
    }
}

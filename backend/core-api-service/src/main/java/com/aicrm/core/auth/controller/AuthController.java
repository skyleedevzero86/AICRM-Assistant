package com.aicrm.core.auth.controller;

import com.aicrm.core.auth.application.AuthService;
import com.aicrm.core.auth.dto.LoginRequest;
import com.aicrm.core.auth.dto.LoginResponse;
import com.aicrm.core.auth.dto.MeResponse;
import com.aicrm.core.auth.dto.SignUpRequest;
import com.aicrm.core.auth.dto.SignUpResponse;
import com.aicrm.core.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/signup/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignUpResponse> signupCustomer(@Valid @RequestBody SignUpRequest request) {
        return ApiResponse.ok(authService.signupCustomer(request));
    }

    @PostMapping("/signup/agent")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignUpResponse> signupAgent(@Valid @RequestBody SignUpRequest request) {
        return ApiResponse.ok(authService.signupAgent(request));
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me() {
        return ApiResponse.ok(authService.me());
    }
}

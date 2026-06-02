package com.aicrm.core.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.auth.application.AuthService;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.dto.LoginResponse;
import com.aicrm.core.auth.dto.MeResponse;
import com.aicrm.core.auth.dto.SignUpResponse;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuthController.class, AdminAgentApprovalController.class})
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void loginReturnsTokenResponse() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse(
                "jwt-token",
                "Bearer",
                1L,
                "agent@test.com",
                UserRole.AGENT
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "agent@test.com",
                                  "password": "password1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.data.role").value("AGENT"));
    }

    @Test
    void signupCustomerReturnsCreatedResponse() throws Exception {
        when(authService.signupCustomer(any())).thenReturn(new SignUpResponse(
                3L,
                "customer@test.com",
                UserRole.CUSTOMER,
                "ACTIVE"
        ));

        mockMvc.perform(post("/api/auth/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "일반 고객",
                                  "email": "customer@test.com",
                                  "password": "password1234"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.signupStatus").value("ACTIVE"));
    }

    @Test
    void signupAgentReturnsPendingResponse() throws Exception {
        when(authService.signupAgent(any())).thenReturn(new SignUpResponse(
                10L,
                "agent-new@test.com",
                UserRole.AGENT,
                "PENDING"
        ));

        mockMvc.perform(post("/api/auth/signup/agent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "신규 상담원",
                                  "email": "agent-new@test.com",
                                  "password": "password1234",
                                  "employeeNo": "2026060207120101"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.signupStatus").value("PENDING"));
    }

    @Test
    void meReturnsCurrentUser() throws Exception {
        when(authService.me()).thenReturn(new MeResponse(
                1L,
                "agent@test.com",
                "상담원",
                UserRole.AGENT,
                ""
        ));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.role").value("AGENT"));
    }

    @Test
    void approveAgentReturnsOk() throws Exception {
        mockMvc.perform(post("/api/admin/agents/{agentId}/approve", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateAgentGradeReturnsOk() throws Exception {
        mockMvc.perform(post("/api/admin/agents/{agentId}/grade", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "grade": "TEAM_LEAD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateMeReturnsOk() throws Exception {
        when(authService.updateCurrentUser(any())).thenReturn(new MeResponse(
                1L,
                "agent@test.com",
                "변경된 이름",
                UserRole.AGENT,
                ""
        ));

        mockMvc.perform(patch("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "변경된 이름"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("변경된 이름"));
    }

    @Test
    void withdrawReturnsOk() throws Exception {
        mockMvc.perform(post("/api/auth/withdraw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

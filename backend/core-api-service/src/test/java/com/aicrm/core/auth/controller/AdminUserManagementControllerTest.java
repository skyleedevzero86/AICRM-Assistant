package com.aicrm.core.auth.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.auth.application.AdminUserManagementService;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.AgentGrade;
import com.aicrm.core.auth.dto.AdminAgentUserResponse;
import com.aicrm.core.auth.dto.AdminCustomerUserResponse;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminUserManagementController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserManagementService adminUserManagementService;

    @Test
    void customersReturnsList() throws Exception {
        when(adminUserManagementService.getCustomerUsers(anyString()))
                .thenReturn(List.of(new AdminCustomerUserResponse(1L, "고객", "c@test.com", "010", "N", "N")));

        mockMvc.perform(get("/api/admin/users/customers").param("keyword", "고객"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("c@test.com"));
    }

    @Test
    void agentsReturnsList() throws Exception {
        when(adminUserManagementService.getAgentUsers(nullable(String.class), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(new AdminAgentUserResponse(
                        2L, 5L, "2026060207120101", "상담원", "a@test.com", AgentAccountStatus.PENDING, AgentGrade.COUNSELOR, "N", "N"
                )));

        mockMvc.perform(get("/api/admin/users/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].approvalStatus").value("PENDING"));
    }

    @Test
    void updateCustomerReturnsOk() throws Exception {
        when(adminUserManagementService.updateCustomerUser(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new AdminCustomerUserResponse(1L, "고객", "c@test.com", "010", "N", "N"));

        mockMvc.perform(patch("/api/admin/users/customers/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "고객",
                                  "email": "c@test.com",
                                  "phone": "010"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("c@test.com"));
    }

    @Test
    void updateAgentReturnsOk() throws Exception {
        when(adminUserManagementService.updateAgentUser(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new AdminAgentUserResponse(
                        2L, 5L, "2026060207120101", "상담원", "a@test.com", AgentAccountStatus.ACTIVE, AgentGrade.COUNSELOR, "N", "N"
                ));

        mockMvc.perform(patch("/api/admin/users/agents/{userId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "상담원",
                                  "email": "a@test.com",
                                  "employeeNo": "2026060207120101"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employeeNo").value("2026060207120101"));
    }

    @Test
    void suspensionUpdatesYn() throws Exception {
        mockMvc.perform(post("/api/admin/users/{userId}/suspension", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"Y\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void withdrawalUpdatesYn() throws Exception {
        mockMvc.perform(post("/api/admin/users/{userId}/withdrawal", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"Y\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

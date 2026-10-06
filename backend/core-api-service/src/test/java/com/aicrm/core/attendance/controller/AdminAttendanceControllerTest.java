package com.aicrm.core.attendance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.attendance.application.AgentAttendanceService;
import com.aicrm.core.attendance.dto.AdminAgentAttendanceResponse;
import com.aicrm.core.auth.domain.AgentGrade;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminAttendanceController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminAttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentAttendanceService agentAttendanceService;

    @Test
    void agentsReturnsAttendanceRows() throws Exception {
        when(agentAttendanceService.getAttendances(any(), any(), nullable(String.class)))
                .thenReturn(List.of(new AdminAgentAttendanceResponse(
                        1L, "상담원", "a@test.com", AgentGrade.COUNSELOR, LocalDate.parse("2026-06-02"), "0", 1, 0, 480
                )));

        mockMvc.perform(get("/api/admin/attendance/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].loginMark").value("0"));
    }
}

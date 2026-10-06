package com.aicrm.core.attendance.controller;

import com.aicrm.core.attendance.application.AgentAttendanceService;
import com.aicrm.core.attendance.dto.AdminAgentAttendanceResponse;
import com.aicrm.core.global.response.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/attendance")
public class AdminAttendanceController {

    private final AgentAttendanceService agentAttendanceService;

    public AdminAttendanceController(AgentAttendanceService agentAttendanceService) {
        this.agentAttendanceService = agentAttendanceService;
    }

    @GetMapping("/agents")
    public ApiResponse<List<AdminAgentAttendanceResponse>> agents(
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.ok(agentAttendanceService.getAttendances(from, to, keyword));
    }
}

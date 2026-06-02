package com.aicrm.core.attendance.dto;

import com.aicrm.core.auth.domain.AgentGrade;
import java.time.LocalDate;

public record AdminAgentAttendanceResponse(
        Long agentId,
        String agentName,
        String email,
        AgentGrade grade,
        LocalDate workDate,
        String loginMark,
        int loginCount,
        int breakMinutes,
        int workMinutes
) {
}

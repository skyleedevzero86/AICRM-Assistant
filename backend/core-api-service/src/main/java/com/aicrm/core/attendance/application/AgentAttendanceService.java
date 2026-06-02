package com.aicrm.core.attendance.application;

import com.aicrm.core.attendance.domain.AgentAttendanceDaily;
import com.aicrm.core.attendance.dto.AdminAgentAttendanceResponse;
import com.aicrm.core.attendance.infrastructure.AgentAttendanceDailyRepository;
import com.aicrm.core.auth.domain.AgentAccount;
import com.aicrm.core.auth.domain.UserAccount;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.infrastructure.AgentAccountRepository;
import com.aicrm.core.auth.infrastructure.UserAccountRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentAttendanceService {

    private final AgentAttendanceDailyRepository attendanceRepository;
    private final AgentAccountRepository agentAccountRepository;
    private final UserAccountRepository userAccountRepository;

    public AgentAttendanceService(
            AgentAttendanceDailyRepository attendanceRepository,
            AgentAccountRepository agentAccountRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.agentAccountRepository = agentAccountRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public void markAgentLogin(Long userId) {
        AgentAccount agent = agentAccountRepository.findByUserId(userId).orElse(null);
        if (agent == null) {
            return;
        }
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        AgentAttendanceDaily attendance = attendanceRepository.findByAgentIdAndWorkDate(agent.getId(), today)
                .orElseGet(() -> AgentAttendanceDaily.create(agent.getId(), today));
        attendance.recordLogin(Instant.now());
        attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<AdminAgentAttendanceResponse> getAttendances(LocalDate from, LocalDate to, String keyword) {
        LocalDate start = from != null ? from : LocalDate.now(ZoneOffset.UTC).minusDays(30);
        LocalDate end = to != null ? to : LocalDate.now(ZoneOffset.UTC);
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        Map<Long, AgentAccount> agentMap = agentAccountRepository.findAll().stream()
                .collect(Collectors.toMap(AgentAccount::getId, agent -> agent));
        Map<Long, UserAccount> userMap = userAccountRepository.findAllByRole(UserRole.AGENT).stream()
                .collect(Collectors.toMap(UserAccount::getId, user -> user));

        return attendanceRepository.findAllByWorkDateBetweenOrderByWorkDateDesc(start, end).stream()
                .map(attendance -> {
                    AgentAccount agent = agentMap.get(attendance.getAgentId());
                    if (agent == null) {
                        return null;
                    }
                    UserAccount user = userMap.get(agent.getUserId());
                    if (user == null) {
                        return null;
                    }
                    return new AdminAgentAttendanceResponse(
                            agent.getId(),
                            user.getName(),
                            user.getEmail(),
                            agent.getGrade(),
                            attendance.getWorkDate(),
                            attendance.getLoginMark(),
                            attendance.getLoginCount(),
                            attendance.getBreakMinutes(),
                            attendance.getWorkMinutes()
                    );
                })
                .filter(item -> item != null)
                .filter(byKeyword(normalized))
                .toList();
    }

    private Predicate<AdminAgentAttendanceResponse> byKeyword(String keyword) {
        if (keyword.isBlank()) {
            return response -> true;
        }
        return response -> response.agentName().toLowerCase(Locale.ROOT).contains(keyword)
                || response.email().toLowerCase(Locale.ROOT).contains(keyword);
    }
}

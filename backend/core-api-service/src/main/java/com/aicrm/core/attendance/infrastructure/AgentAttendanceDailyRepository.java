package com.aicrm.core.attendance.infrastructure;

import com.aicrm.core.attendance.domain.AgentAttendanceDaily;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentAttendanceDailyRepository extends JpaRepository<AgentAttendanceDaily, Long> {

    Optional<AgentAttendanceDaily> findByAgentIdAndWorkDate(Long agentId, LocalDate workDate);

    List<AgentAttendanceDaily> findAllByWorkDateBetweenOrderByWorkDateDesc(LocalDate from, LocalDate to);
}

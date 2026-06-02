package com.aicrm.core.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "agent_attendance_daily")
public class AgentAttendanceDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "login_mark", nullable = false, columnDefinition = "char(1)")
    private String loginMark = "X";

    @Column(name = "login_count", nullable = false)
    private int loginCount = 0;

    @Column(name = "break_minutes", nullable = false)
    private int breakMinutes = 0;

    @Column(name = "work_minutes", nullable = false)
    private int workMinutes = 0;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected AgentAttendanceDaily() {
    }

    public static AgentAttendanceDaily create(Long agentId, LocalDate date) {
        AgentAttendanceDaily attendance = new AgentAttendanceDaily();
        attendance.agentId = agentId;
        attendance.workDate = date;
        return attendance;
    }

    public void recordLogin(Instant loginAt) {
        this.loginMark = "0";
        this.loginCount += 1;
        this.lastLoginAt = loginAt;
        this.updatedAt = Instant.now();
    }

    public Long getAgentId() {
        return agentId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public String getLoginMark() {
        return loginMark;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public int getWorkMinutes() {
        return workMinutes;
    }
}

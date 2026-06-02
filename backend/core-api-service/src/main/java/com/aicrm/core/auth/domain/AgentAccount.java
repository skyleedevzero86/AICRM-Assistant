package com.aicrm.core.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "agents")
public class AgentAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AgentAccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AgentGrade grade;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected AgentAccount() {
    }

    public static AgentAccount createPending(Long userId, String name) {
        AgentAccount agent = new AgentAccount();
        agent.userId = userId;
        agent.name = name;
        agent.status = AgentAccountStatus.PENDING;
        agent.grade = AgentGrade.COUNSELOR;
        agent.createdAt = Instant.now();
        agent.updatedAt = Instant.now();
        return agent;
    }

    public void approve() {
        this.status = AgentAccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public AgentAccountStatus getStatus() {
        return status;
    }

    public AgentGrade getGrade() {
        return grade;
    }

    public void changeGrade(AgentGrade grade) {
        this.grade = grade;
        this.updatedAt = Instant.now();
    }
}

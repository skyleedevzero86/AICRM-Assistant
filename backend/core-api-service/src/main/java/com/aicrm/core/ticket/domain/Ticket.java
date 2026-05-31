package com.aicrm.core.ticket.domain;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_no", nullable = false, unique = true, length = 30)
    private String ticketNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "agent_id")
    private Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ConsultationCategory category;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "ticket_status")
    private TicketStatus status = TicketStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "channel_type")
    private ChannelType channel = ChannelType.WEB_INQUIRY;

    @Column(length = 255)
    private String subject;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    protected Ticket() {
    }

    public static Ticket createWaiting(
            Customer customer,
            ConsultationCategory category,
            ChannelType channel,
            String subject,
            String ticketNo
    ) {
        Ticket ticket = new Ticket();
        ticket.ticketNo = ticketNo;
        ticket.customer = customer;
        ticket.category = category;
        ticket.channel = channel;
        ticket.subject = subject;
        ticket.status = TicketStatus.WAITING;
        ticket.createdAt = Instant.now();
        ticket.updatedAt = Instant.now();
        return ticket;
    }

    public void accept(Long agentId) {
        if (!status.canAccept()) {
            throw new BusinessException(ErrorCode.TICKET_CANNOT_ACCEPT);
        }

        this.agentId = agentId;
        this.status = TicketStatus.IN_PROGRESS;
        this.assignedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void close(Long agentId, String resolution) {
        if (!Objects.equals(this.agentId, agentId)) {
            throw new BusinessException(ErrorCode.NOT_ASSIGNED_AGENT);
        }

        if (!status.canClose()) {
            throw new BusinessException(ErrorCode.TICKET_CANNOT_CLOSE);
        }

        if (resolution == null || resolution.isBlank()) {
            throw new BusinessException(ErrorCode.RESOLUTION_REQUIRED);
        }

        this.status = TicketStatus.CLOSED;
        this.closedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ConsultationCategory getCategory() {
        return category;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public ChannelType getChannel() {
        return channel;
    }

    public String getSubject() {
        return subject;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getAgentId() {
        return agentId;
    }
}

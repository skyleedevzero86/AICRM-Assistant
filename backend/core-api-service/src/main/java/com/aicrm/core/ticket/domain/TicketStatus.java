package com.aicrm.core.ticket.domain;

public enum TicketStatus {
    WAITING,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    ESCALATED;

    public boolean canUpdate() {
        return this == WAITING;
    }

    public boolean canAccept() {
        return this == WAITING || this == ASSIGNED;
    }

    public boolean canClose() {
        return this == IN_PROGRESS || this == RESOLVED;
    }

    public boolean canAddMessage() {
        return this != CLOSED;
    }
}

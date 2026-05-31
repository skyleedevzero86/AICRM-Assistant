package com.aicrm.core.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "Invalid request"),
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND, "Resource not found"),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND, "Consultation category not found"),
    INVALID_CATEGORY_DEPTH("INVALID_CATEGORY_DEPTH", HttpStatus.BAD_REQUEST, "Consultation category must be a depth-3 leaf category"),
    TICKET_NOT_FOUND("TICKET_NOT_FOUND", HttpStatus.NOT_FOUND, "Ticket not found"),
    TICKET_CANNOT_ACCEPT("TICKET_CANNOT_ACCEPT", HttpStatus.BAD_REQUEST, "Ticket cannot be accepted"),
    TICKET_CANNOT_CLOSE("TICKET_CANNOT_CLOSE", HttpStatus.BAD_REQUEST, "Ticket cannot be closed"),
    NOT_ASSIGNED_AGENT("NOT_ASSIGNED_AGENT", HttpStatus.FORBIDDEN, "Agent is not assigned to this ticket"),
    RESOLUTION_REQUIRED("RESOLUTION_REQUIRED", HttpStatus.BAD_REQUEST, "Resolution is required"),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

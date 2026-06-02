package com.aicrm.core.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다"),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND, "상담 카테고리를 찾을 수 없습니다"),
    INVALID_CATEGORY_DEPTH("INVALID_CATEGORY_DEPTH", HttpStatus.BAD_REQUEST, "상담 카테고리는 3단계 리프 카테고리여야 합니다"),
    TICKET_NOT_FOUND("TICKET_NOT_FOUND", HttpStatus.NOT_FOUND, "티켓을 찾을 수 없습니다"),
    AGENT_ID_REQUIRED("AGENT_ID_REQUIRED", HttpStatus.BAD_REQUEST, "상담원 ID가 필요합니다"),
    TICKET_CANNOT_UPDATE("TICKET_CANNOT_UPDATE", HttpStatus.BAD_REQUEST, "수정할 수 없는 티켓입니다"),
    TICKET_CANNOT_ACCEPT("TICKET_CANNOT_ACCEPT", HttpStatus.BAD_REQUEST, "수락할 수 없는 티켓입니다"),
    TICKET_ALREADY_ASSIGNED("TICKET_ALREADY_ASSIGNED", HttpStatus.CONFLICT, "이미 배정된 티켓입니다"),
    TICKET_CANNOT_CLOSE("TICKET_CANNOT_CLOSE", HttpStatus.BAD_REQUEST, "종료할 수 없는 티켓입니다"),
    NOT_ASSIGNED_AGENT("NOT_ASSIGNED_AGENT", HttpStatus.FORBIDDEN, "이 티켓에 배정된 상담원이 아닙니다"),
    RESOLUTION_REQUIRED("RESOLUTION_REQUIRED", HttpStatus.BAD_REQUEST, "처리 결과가 필요합니다"),
    MESSAGE_CONTENT_REQUIRED("MESSAGE_CONTENT_REQUIRED", HttpStatus.BAD_REQUEST, "메시지 내용이 필요합니다"),
    TICKET_CANNOT_ADD_MESSAGE("TICKET_CANNOT_ADD_MESSAGE", HttpStatus.BAD_REQUEST, "종료된 티켓에는 메시지를 등록할 수 없습니다"),
    INVALID_MESSAGE_TYPE("INVALID_MESSAGE_TYPE", HttpStatus.BAD_REQUEST, "지원하지 않는 메시지 유형입니다"),
    AUTH_FAILED("AUTH_FAILED", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
    AGENT_APPROVAL_REQUIRED("AGENT_APPROVAL_REQUIRED", HttpStatus.FORBIDDEN, "상담원 계정 승인이 필요합니다"),
    ACCOUNT_WITHDRAWN("ACCOUNT_WITHDRAWN", HttpStatus.FORBIDDEN, "탈퇴 처리된 계정입니다"),
    ACCOUNT_SUSPENDED("ACCOUNT_SUSPENDED", HttpStatus.FORBIDDEN, "정지된 계정입니다"),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다");

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

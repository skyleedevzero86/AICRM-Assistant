package com.aicrm.core.global.exception;

import com.aicrm.core.global.message.MessagesHolder;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(MessagesHolder.get().error(errorCode));
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String messageKey, Object... args) {
        super(args.length == 0
                ? MessagesHolder.get().error(messageKey)
                : MessagesHolder.get().formatError(messageKey, args));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

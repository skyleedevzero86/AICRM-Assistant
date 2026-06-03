package com.aicrm.core.global.exception;

import com.aicrm.core.global.message.MessagesHolder;
import com.aicrm.core.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(MessagesHolder.get().error(ErrorCode.INVALID_REQUEST));
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ErrorCode.INVALID_REQUEST.getCode(), message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(
                        ErrorCode.FILE_TOO_LARGE.getCode(),
                        MessagesHolder.get().error(ErrorCode.FILE_TOO_LARGE)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.failure(
                        ErrorCode.INTERNAL_ERROR.getCode(),
                        MessagesHolder.get().error(ErrorCode.INTERNAL_ERROR)));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException exception) {
        return ResponseEntity
                .status(ErrorCode.UNAUTHORIZED.getStatus())
                .body(ApiResponse.failure(ErrorCode.UNAUTHORIZED.getCode(), MessagesHolder.get().error(ErrorCode.UNAUTHORIZED)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getStatus())
                .body(ApiResponse.failure(ErrorCode.FORBIDDEN.getCode(), MessagesHolder.get().error(ErrorCode.FORBIDDEN)));
    }
}

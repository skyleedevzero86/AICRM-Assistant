package com.aicrm.core.global.dto;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorDetail error
) {

    public record ErrorDetail(
            String code,
            String message
    ) {
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorDetail(code, message));
    }
}

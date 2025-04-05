package com.BANnerIt.server.global.exception;

import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        @Nullable String jwt,
        @Nullable T user_data,
        @Nullable ExceptionDto error
) {
    public static <T> ApiResponse<T> ok(@Nullable final T user_data) {
        return new ApiResponse<>(null, user_data, null);
    }

    public static <T> ApiResponse<T> success(@Nullable final String jwt, @Nullable final T user_data) {
        return new ApiResponse<>(jwt, user_data, null);
    }

    public static <T> ApiResponse<T> fail(final CustomException e) {
        return fail(e.getErrorCode());
    }

    public static <T> ApiResponse<T> fail(final ErrorCode errorCode) {
        return new ApiResponse<>(null, null, ExceptionDto.of(errorCode));
    }

    public static <T> ApiResponse<T> fail(final String message, final HttpStatus status) {
        return new ApiResponse<>(null, null, new ExceptionDto(status.value(), message));
    }
}
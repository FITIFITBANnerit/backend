package com.BANnerIt.server.global.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        @Nullable @JsonProperty("user_data")T data,
        @Nullable @JsonProperty("error") ExceptionDto error
) {
    public static <T> ApiResponse<T> success(@Nullable final T data) {
        return new ApiResponse<>(data, null);
    }
    public static <T> ApiResponse<T> ok(@Nullable final T user_data) {
        return new ApiResponse<>(user_data, null);
    }
    public static <T> ApiResponse<T> fail(final CustomException e) {
        return fail(e.getErrorCode());
    }

    public static <T> ApiResponse<T> fail(final ErrorCode errorCode) {
        return new ApiResponse<>(null, ExceptionDto.of(errorCode));
    }

    public static <T> ApiResponse<T> fail(final String message, final HttpStatus status) {
        return new ApiResponse<>(null, new ExceptionDto(status.value(), message));
    }

}

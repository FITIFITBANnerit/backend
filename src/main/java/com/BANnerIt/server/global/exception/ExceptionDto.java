package com.BANnerIt.server.global.exception;

import com.BANnerIt.server.global.exception.ErrorCode;

public class ExceptionDto {
    private final int code;
    private final String message;

    public ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ExceptionDto(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode);
    }

    // Getter 메서드 추가
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
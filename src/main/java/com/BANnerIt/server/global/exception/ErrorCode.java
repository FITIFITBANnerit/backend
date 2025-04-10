package com.BANnerIt.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Test Error
    TEST_ERROR(10000, HttpStatus.BAD_REQUEST, "테스트 에러입니다."),

    // 404 Not Found
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    NOT_FOUND_MEMBER(40401, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN(30302, HttpStatus.NOT_FOUND, "RefreshToken을 찾을 수 없습니다."),
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "승인되지 않은 접근입니다."),
    INVALID_TOKEN(40101, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    AUTHENTICATION_FAILED(40102, HttpStatus.UNAUTHORIZED, "올바르지 않은 JWT거나 인증에 실패했습니다."),
    EXPIRED_REFRESH_TOKEN(40103,HttpStatus.UNAUTHORIZED, "RefreshToken이 만료되었습니다."),

    // 400 Bad Request
    INVALID_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
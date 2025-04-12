package com.BANnerIt.server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiResponseUtil {
    public static ResponseEntity<Map<String, Object>> ok(String key, Object value) {
        Map<String, Object> body = new HashMap<>();
        body.put(key, value);
        body.put("error", null);
        return ResponseEntity.ok(body);
    }

    public static ResponseEntity<Map<String, Object>> fail(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", new ExceptionDto(status.value(), message));
        return new ResponseEntity<>(body, status);
    }

    public static ResponseEntity<Map<String, Object>> fail(ErrorCode errorCode) {
        return fail(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    public static ResponseEntity<Map<String, Object>> fail(CustomException e) {
        return fail(e.getErrorCode());
    }
}

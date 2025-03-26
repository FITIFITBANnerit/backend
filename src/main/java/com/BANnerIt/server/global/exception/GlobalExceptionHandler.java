package com.BANnerIt.server.global.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiResponse<?>> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        ApiResponse<?> response = ApiResponse.fail(new CustomException(ErrorCode.NOT_FOUND_END_POINT,e.getMessage()));
        return ResponseEntity.status(ErrorCode.NOT_FOUND_END_POINT.getHttpStatus()).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getErrorCode().getHttpStatus().value(), ex.getMessage());

        ApiResponse<?> response = new ApiResponse<>( null, null, exceptionDto);

        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode().getHttpStatus().value());
        return new ResponseEntity<>(response, status);
    }
    @ExceptionHandler(value = {GeneralSecurityException.class, IOException.class})
    public ResponseEntity<ApiResponse<?>> handleSecurityAndIoExceptions(Exception e) {
        log.error("GeneralSecurityException or IOException in GlobalExceptionHandler: {}", e.getMessage());
        ApiResponse<?> response = ApiResponse.fail(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }


    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("handleException() in GlobalExceptionHandler throw Exception : {}", e.getMessage());
        e.printStackTrace();
        ApiResponse<?> response = ApiResponse.fail(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR,e.getMessage()));
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }




}
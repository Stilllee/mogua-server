package com.fesi6.team1.study_group.global.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleExceptions(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.errorResponse(exception.getMessage()));
    }

}

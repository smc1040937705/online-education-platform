package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("Bad credentials: {}", e.getMessage());
        return ApiResponse.error(401, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage());
        return ApiResponse.error(403, "没有权限访问该资源");
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("Validation error: {}", message);
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ApiResponse.error("服务器内部错误");
    }
}

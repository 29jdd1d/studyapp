package com.studyapp.exception;

import com.studyapp.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * Global Exception Handler
 * 
 * Handles all exceptions thrown by controllers and provides
 * consistent error responses to clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     * Handle business exceptions
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("Business exception at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(e.getMessage()));
    }
    
    /**
     * 处理资源未找到异常
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result<Void>> handleResourceNotFoundException(
            ResourceNotFoundException e, HttpServletRequest request) {
        logger.warn("Resource not found at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Result.error(e.getMessage()));
    }
    
    /**
     * 处理参数验证异常
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("Validation failed: {}", errors);
        return Result.error("参数验证失败 (Validation failed)", errors);
    }
    
    /**
     * 处理参数类型不匹配异常
     * Handle parameter type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String error = String.format("参数 '%s' 类型错误，期望类型: %s (Parameter '%s' type error, expected: %s)",
                e.getName(), e.getRequiredType().getSimpleName(),
                e.getName(), e.getRequiredType().getSimpleName());
        logger.warn("Type mismatch at {}: {}", request.getRequestURI(), error);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(error));
    }
    
    /**
     * 处理认证失败异常
     * Handle authentication exceptions
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<Void>> handleBadCredentials(
            BadCredentialsException e, HttpServletRequest request) {
        logger.warn("Authentication failed at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error("认证失败，用户名或密码错误 (Authentication failed)"));
    }
    
    /**
     * 处理权限不足异常
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDenied(
            AccessDeniedException e, HttpServletRequest request) {
        logger.warn("Access denied at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Result.error("权限不足 (Access denied)"));
    }
    
    /**
     * 处理非法参数异常
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(
            IllegalArgumentException e, HttpServletRequest request) {
        logger.warn("Illegal argument at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error("参数错误: " + e.getMessage()));
    }
    
    /**
     * 处理空指针异常
     * Handle null pointer exceptions
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<Void>> handleNullPointer(
            NullPointerException e, HttpServletRequest request) {
        logger.error("Null pointer exception at {}", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("服务器内部错误 (Internal server error)"));
    }
    
    /**
     * 处理所有未捕获的异常
     * Handle all uncaught exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleAllExceptions(Exception e, HttpServletRequest request) {
        logger.error("Unexpected exception at {}", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("服务器内部错误，请稍后重试 (Internal server error, please try again later)"));
    }
}

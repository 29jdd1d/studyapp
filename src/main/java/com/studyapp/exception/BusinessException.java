package com.studyapp.exception;

/**
 * 业务异常
 * Business Exception
 * 
 * Thrown when business logic validation fails
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.studyapp.exception;

/**
 * 资源未找到异常
 * Resource Not Found Exception
 * 
 * Thrown when a requested resource cannot be found
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s not found with id: %s", resourceName, id));
    }
}

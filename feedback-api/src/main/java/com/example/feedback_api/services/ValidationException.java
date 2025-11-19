package com.example.feedback_api.services;

/**
 * Exception thrown when feedback validation fails business rules
 * Used for business logic validation (not DTO validation)
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
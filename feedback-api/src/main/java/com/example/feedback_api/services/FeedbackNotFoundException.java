package com.example.feedback_api.services;

/**
 * Exception thrown when requested feedback is not found
 * Results in HTTP 404 Not Found response
 */
public class FeedbackNotFoundException extends RuntimeException {
    
    public FeedbackNotFoundException(String message) {
        super(message);
    }
    
    public FeedbackNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
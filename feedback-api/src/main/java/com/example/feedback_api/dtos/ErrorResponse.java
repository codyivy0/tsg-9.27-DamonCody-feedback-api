package com.example.feedback_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Error response DTO for validation failures
 * Returned when validation fails (HTTP 400)
 */
@Schema(description = "Error response containing validation failure details")
public class ErrorResponse {

    @Schema(description = "List of field-specific validation errors")
    private List<FieldError> errors;

    // Default constructor
    public ErrorResponse() {
    }

    // Constructor
    public ErrorResponse(List<FieldError> errors) {
        this.errors = errors;
    }

    // Getters and Setters
    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    /**
     * Represents a single field validation error
     */
    @Schema(description = "Individual field validation error details")
    public static class FieldError {

        @Schema(description = "Name of the field that failed validation", example = "comment")
        private String field;

        @Schema(description = "Human-readable error message describing the validation failure", example = "Must be 200 characters or less")
        private String message;

        // Default constructor
        public FieldError() {
        }

        // Constructor
        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // Getters and Setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errors=" + errors +
                '}';
    }
}
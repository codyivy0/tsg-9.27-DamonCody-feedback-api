package com.example.feedback_api.controllers;

import com.example.feedback_api.dtos.FeedbackRequest;
import com.example.feedback_api.dtos.FeedbackResponse;
import com.example.feedback_api.dtos.ErrorResponse;
import com.example.feedback_api.services.FeedbackService;
import com.example.feedback_api.services.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for feedback operations
 * Handles HTTP requests and delegates business logic to FeedbackService
 */
@Tag(name = "Feedback", description = "Provider feedback management API")
@RestController
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(summary = "Submit new provider feedback", 
               description = "Create a new feedback entry for a healthcare provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Feedback created successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = FeedbackResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data or business rule violation",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/feedback")
    public ResponseEntity<?> createFeedback(
            @RequestBody @Valid FeedbackRequest request) {
        
        try {
            // Call service to validate and save feedback
            FeedbackResponse response = feedbackService.validateAndSave(request);
            return ResponseEntity.status(201).body(response);
            
        } catch (ValidationException e) {
            // Business validation failed - return 400 with error details
            ErrorResponse errorResponse = new ErrorResponse(
                List.of(new ErrorResponse.FieldError("business", e.getMessage()))
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "Health check endpoint", 
               description = "Simple health check to verify the API is running")
    @ApiResponse(responseCode = "200", description = "API is healthy")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Feedback API is healthy!");
    }
}
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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for feedback operations
 * Handles HTTP requests and delegates business logic to FeedbackService
 */
@RestController
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final RestTemplate restTemplate;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
        this.restTemplate = new RestTemplate();
    }

    @Operation(
        summary = "Health check endpoint", 
        description = "Health status of the feedback API service",
        tags = {"Health"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is healthy"),
            @ApiResponse(responseCode = "503", description = "API is unavailable")
    })
    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        try {
            // Call the local actuator health endpoint
            ResponseEntity<Object> response = restTemplate.getForEntity(
                "http://localhost:8080/actuator/health", Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Return service unavailable if health check fails
            return ResponseEntity.status(503).body(
                Map.of("status", "DOWN", 
                       "service", "feedback-api",
                       "error", "Service unavailable",
                       "details", e.getClass().getSimpleName() + ": " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Analytics Consumer health check", 
        description = "Health status of the analytics consumer service",
        tags = {"Health"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics service is healthy"),
            @ApiResponse(responseCode = "503", description = "Analytics service is unavailable")
    })
    @GetMapping("/health/analytics")
    public ResponseEntity<Object> analyticsHealth() {
        try {
            // Call the analytics consumer health endpoint
            // Use container name for inter-container communication
            ResponseEntity<Object> response = restTemplate.getForEntity(
                "http://feedback-analytics-consumer:8081/actuator/health", Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Return service unavailable if analytics service is down with detailed error
            return ResponseEntity.status(503).body(
                Map.of("status", "DOWN", 
                       "service", "analytics-consumer",
                       "error", "Service unavailable",
                       "details", e.getClass().getSimpleName() + ": " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Submit new provider feedback", 
        description = "Create a new feedback entry for a healthcare provider",
        tags = {"Feedback Operations"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/feedback")
    public ResponseEntity<Object> createFeedback(
            @RequestBody @Valid FeedbackRequest request) {

        try {
            // Call service to validate and save feedback
            FeedbackResponse response = feedbackService.validateAndSave(request);
            return ResponseEntity.status(201).body(response);

        } catch (ValidationException e) {
            // Business validation failed - return 400 with error details
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of(new ErrorResponse.FieldError("business", e.getMessage())));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(
        summary = "Get feedback entries", 
        description = "Retrieve all feedback entries, optionally filtered by member ID",
        tags = {"Feedback Operations"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback entries retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackResponse.class)))
    })
    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedback(
            @RequestParam(required = false) String memberId) {

        List<FeedbackResponse> feedback = feedbackService.getFeedback(memberId);
        return ResponseEntity.ok(feedback);
    }

    @Operation(
        summary = "Get individual feedback by ID", 
        description = "Retrieve a singular feedback entry by it's UUID",
        tags = {"Feedback Operations"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @GetMapping("/feedback/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable String id) {
        FeedbackResponse feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }
}

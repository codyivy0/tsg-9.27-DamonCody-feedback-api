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

    @Operation(summary = "Submit new provider feedback", description = "Create a new feedback entry for a healthcare provider")
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

    @Operation(summary = "Get feedback entries", description = "Retrieve all feedback entries, optionally filtered by member ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback entries retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackResponse.class)))
    })
    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedback(
            @RequestParam(required = false) String memberId) {

        List<FeedbackResponse> feedback = feedbackService.getFeedback(memberId);
        return ResponseEntity.ok(feedback);
    }

    @Operation(summary = "Health check endpoint", description = "Simple health check to verify the API is running")
    @ApiResponse(responseCode = "200", description = "API is healthy")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Feedback API is healthy!");
    }

    @Operation(summary = "Get individual feedback by ID", description = "Retrieve a singular feedback entry by it's UUID")
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

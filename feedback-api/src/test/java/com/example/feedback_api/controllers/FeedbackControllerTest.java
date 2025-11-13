package com.example.feedback_api.controllers;

import com.example.feedback_api.dtos.FeedbackRequest;
import com.example.feedback_api.dtos.FeedbackResponse;
import com.example.feedback_api.services.FeedbackService;
import com.example.feedback_api.services.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for FeedbackController
 * Tests HTTP request/response handling without starting full application
 */
@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    private FeedbackRequest validRequest;
    private FeedbackResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new FeedbackRequest();
        validRequest.setMemberId("member-123");
        validRequest.setProviderName("Dr. Smith");
        validRequest.setRating(4);
        validRequest.setComment("Great service!");

        mockResponse = new FeedbackResponse(
                UUID.randomUUID(),
                "member-123",
                "Dr. Smith",
                4,
                "Great service!",
                Instant.now()
        );
    }

    @Test
    void createFeedback_ValidRequest_ShouldReturn201() throws Exception {
        // Arrange
        when(feedbackService.validateAndSave(any(FeedbackRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.memberId").value("member-123"))
                .andExpect(jsonPath("$.providerName").value("Dr. Smith"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Great service!"));
    }

    @Test
    void createFeedback_ValidationError_ShouldReturn400() throws Exception {
        // Arrange
        when(feedbackService.validateAndSave(any(FeedbackRequest.class)))
                .thenThrow(new ValidationException("Member ID is required"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("business"))
                .andExpect(jsonPath("$.errors[0].message").value("Member ID is required"));
    }

    @Test
    void createFeedback_InvalidJson_ShouldReturn400() throws Exception {
        // Arrange
        FeedbackRequest invalidRequest = new FeedbackRequest();
        // Missing required fields will trigger validation

        // Act & Assert
        mockMvc.perform(post("/api/v1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFeedback_WithMemberId_ShouldReturn200() throws Exception {
        // Arrange
        List<FeedbackResponse> mockResponses = List.of(mockResponse);
        when(feedbackService.getFeedback("member-123")).thenReturn(mockResponses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/feedback")
                        .param("memberId", "member-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].memberId").value("member-123"));
    }

    @Test
    void getFeedback_WithoutMemberId_ShouldReturn200() throws Exception {
        // Arrange
        List<FeedbackResponse> mockResponses = List.of(mockResponse);
        when(feedbackService.getFeedback(null)).thenReturn(mockResponses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void healthCheck_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Feedback API is healthy!"));
    }
}
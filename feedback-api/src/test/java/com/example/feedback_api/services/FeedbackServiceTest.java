package com.example.feedback_api.services;

import com.example.feedback_api.dtos.FeedbackRequest;
import com.example.feedback_api.dtos.FeedbackResponse;
import com.example.feedback_api.messaging.FeedbackEventPublisher;
import com.example.feedback_api.model.FeedbackEntity;
import com.example.feedback_api.repositories.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FeedbackService
 * Tests business logic in isolation using mocks for dependencies
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private FeedbackEventPublisher eventPublisher;

    @InjectMocks
    private FeedbackService feedbackService;

    private FeedbackRequest validRequest;
    private FeedbackEntity mockEntity;

    @BeforeEach
    void setUp() {
        // Arrange - Set up common test data
        validRequest = new FeedbackRequest();
        validRequest.setMemberId("member-123");
        validRequest.setProviderName("Dr. Smith");
        validRequest.setRating(4);
        validRequest.setComment("Great service!");

        mockEntity = new FeedbackEntity();
        mockEntity.setId(UUID.randomUUID());
        mockEntity.setMemberId("member-123");
        mockEntity.setProviderName("Dr. Smith");
        mockEntity.setRating(4);
        mockEntity.setComment("Great service!");
        mockEntity.setSubmittedAt(Instant.now());
    }

    @Test
    void validateAndSave_ValidRequest_ShouldReturnFeedbackResponse() {
        // Arrange
        when(feedbackRepository.existsByMemberIdAndProviderName(anyString(), anyString())).thenReturn(false);
        when(feedbackRepository.save(any(FeedbackEntity.class))).thenReturn(mockEntity);

        // Act
        FeedbackResponse response = feedbackService.validateAndSave(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(mockEntity.getId(), response.getId());
        assertEquals("member-123", response.getMemberId());
        assertEquals("Dr. Smith", response.getProviderName());
        assertEquals(4, response.getRating());
        assertEquals("Great service!", response.getComment());

        // Verify interactions
        verify(feedbackRepository).existsByMemberIdAndProviderName("member-123", "Dr. Smith");
        verify(feedbackRepository).save(any(FeedbackEntity.class));
        verify(eventPublisher).publishFeedbackSubmitted(mockEntity);
    }

    @Test
    void validateAndSave_DuplicateFeedback_ShouldThrowValidationException() {
        // Arrange - Mock repository to return true (duplicate exists)
        when(feedbackRepository.existsByMemberIdAndProviderName(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            feedbackService.validateAndSave(validRequest);
        });

        assertEquals("You have already submitted feedback for Dr. Smith", exception.getMessage());

        // Verify repository was called to check for duplicates
        verify(feedbackRepository).existsByMemberIdAndProviderName("member-123", "Dr. Smith");

        // Verify save and publish were NOT called
        verify(feedbackRepository, never()).save(any());
        verify(eventPublisher, never()).publishFeedbackSubmitted(any());
    }

    @Test
    void validateAndSave_EmptyMemberId_ShouldThrowValidationException() {
        // Arrange
        validRequest.setMemberId("   "); // Empty after trimming

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            feedbackService.validateAndSave(validRequest);
        });

        assertEquals("Member ID is required", exception.getMessage());
    }

    @Test
    void validateAndSave_RatingOutOfRange_ShouldThrowValidationException() {
        // Arrange
        validRequest.setRating(6); // Invalid rating

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            feedbackService.validateAndSave(validRequest);
        });

        assertEquals("Rating must be between 1 and 5", exception.getMessage());
    }

    @Test
    void getFeedback_WithMemberId_ShouldReturnFilteredList() {
        // Arrange
        List<FeedbackEntity> mockEntities = List.of(mockEntity);
        when(feedbackRepository.findByMemberIdOrderBySubmittedAtDesc("member-123")).thenReturn(mockEntities);

        // Act
        List<FeedbackResponse> responses = feedbackService.getFeedback("member-123");

        // Assert
        assertEquals(1, responses.size());
        assertEquals("member-123", responses.get(0).getMemberId());

        // Verify correct repository method was called
        verify(feedbackRepository).findByMemberIdOrderBySubmittedAtDesc("member-123");
        verify(feedbackRepository, never()).findAllByOrderBySubmittedAtDesc();
    }

    @Test
    void getFeedback_WithoutMemberId_ShouldReturnAllFeedback() {
        // Arrange
        List<FeedbackEntity> mockEntities = List.of(mockEntity);
        when(feedbackRepository.findAllByOrderBySubmittedAtDesc()).thenReturn(mockEntities);

        // Act
        List<FeedbackResponse> responses = feedbackService.getFeedback(null);

        // Assert
        assertEquals(1, responses.size());

        // Verify correct repository method was called
        verify(feedbackRepository).findAllByOrderBySubmittedAtDesc();
        verify(feedbackRepository, never()).findByMemberIdOrderBySubmittedAtDesc(anyString());
    }

    @Test
    void getFeedback_EmptyMemberId_ShouldReturnAllFeedback() {
        // Arrange
        List<FeedbackEntity> mockEntities = new ArrayList<>();
        when(feedbackRepository.findAllByOrderBySubmittedAtDesc()).thenReturn(mockEntities);

        // Act
        List<FeedbackResponse> responses = feedbackService.getFeedback("  "); // Whitespace

        // Assert
        assertEquals(0, responses.size());

        // Verify correct repository method was called (empty string treated as null)
        verify(feedbackRepository).findAllByOrderBySubmittedAtDesc();
    }
}
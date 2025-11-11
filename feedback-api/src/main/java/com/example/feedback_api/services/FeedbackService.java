package com.example.feedback_api.services;

import com.example.feedback_api.dtos.FeedbackRequest;
import com.example.feedback_api.dtos.FeedbackResponse;
import com.example.feedback_api.model.FeedbackEntity;
import com.example.feedback_api.repositories.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service layer for feedback operations
 * Contains business logic and validation for feedback creation
 */
@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Validate feedback request and save to database
     * Applies additional service-layer validation beyond DTO validation
     *
     * @param request validated feedback request DTO from controller
     * @return feedback response DTO with generated ID and timestamp
     * @throws ValidationException if business validation fails
     */
    public FeedbackResponse validateAndSave(FeedbackRequest request) {
        // Additional service-layer validation
        validateBusinessRules(request);
        
        // Map DTO → Entity
        FeedbackEntity entity = mapRequestToEntity(request);
        
        // Set timestamp manually to ensure it's not null
        // @CreationTimestamp should handle this, but we ensure it's set
        entity.setSubmittedAt(Instant.now());
        
        // Save to database
        FeedbackEntity savedEntity = feedbackRepository.save(entity);
        
        // Map Entity → Response DTO
        return mapEntityToResponse(savedEntity);
    }

    /**
     * Apply business validation rules
     * Additional validation beyond DTO annotations
     *
     * @param request feedback request to validate
     * @throws ValidationException if validation fails
     */
    private void validateBusinessRules(FeedbackRequest request) {
        // Validate required fields (additional check beyond DTO)
        if (request.getMemberId() == null || request.getMemberId().trim().isEmpty()) {
            throw new ValidationException("Member ID is required");
        }
        
        if (request.getProviderName() == null || request.getProviderName().trim().isEmpty()) {
            throw new ValidationException("Provider name is required");
        }
        
        if (request.getRating() == null) {
            throw new ValidationException("Rating is required");
        }
        
        // Validate rating range (1-5)
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        
        // Validate comment length if provided
        if (request.getComment() != null && request.getComment().length() > 200) {
            throw new ValidationException("Comment must be 200 characters or less");
        }
        
        // Business rule: Check for duplicate feedback
        boolean duplicateExists = feedbackRepository.existsByMemberIdAndProviderName(
                request.getMemberId().trim(), 
                request.getProviderName().trim()
        );
        
        if (duplicateExists) {
            throw new ValidationException(
                "You have already submitted feedback for " + request.getProviderName()
            );
        }
    }

    /**
     * Map FeedbackRequest DTO to FeedbackEntity
     *
     * @param request validated DTO from controller
     * @return entity ready for persistence
     */
    private FeedbackEntity mapRequestToEntity(FeedbackRequest request) {
        FeedbackEntity entity = new FeedbackEntity();
        entity.setMemberId(request.getMemberId().trim());
        entity.setProviderName(request.getProviderName().trim());
        entity.setRating(request.getRating());
        entity.setComment(request.getComment() != null ? request.getComment().trim() : null);
        return entity;
    }

    /**
     * Map FeedbackEntity to FeedbackResponse DTO
     *
     * @param entity persisted entity from database
     * @return response DTO for controller
     */
    private FeedbackResponse mapEntityToResponse(FeedbackEntity entity) {
        return new FeedbackResponse(
            entity.getId(),
            entity.getMemberId(),
            entity.getProviderName(),
            entity.getRating(),
            entity.getComment(),
            entity.getSubmittedAt()
        );
    }
}
package com.example.feedback_api.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for creating feedback
 * Maps to POST /api/v1/feedback request body
 */
@Schema(description = "Request payload for submitting provider feedback")
public class FeedbackRequest {
    
    @Schema(description = "Unique identifier for the member/patient providing feedback", 
            example = "m-123456", 
            maxLength = 36, 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Member ID is required")
    @Size(max = 36, message = "Member ID must be 36 characters or less")
    private String memberId;
    
    @Schema(description = "Name of the healthcare provider being reviewed", 
            example = "Dr. Sarah Johnson", 
            maxLength = 80, 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Provider name is required")
    @Size(max = 80, message = "Provider name must be 80 characters or less")
    private String providerName;
    
    @Schema(description = "Rating score for the provider experience (1-5 scale)", 
            example = "4", 
            minimum = "1", 
            maximum = "5", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    @Schema(description = "Optional detailed feedback comment about the provider experience", 
            example = "Dr. Johnson was very professional and explained everything clearly. Great bedside manner!", 
            maxLength = 200, 
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 200, message = "Comment must be 200 characters or less")
    private String comment;
    
    // Default constructor for Jackson
    public FeedbackRequest() {}
    
    // Constructor for testing
    public FeedbackRequest(String memberId, String providerName, Integer rating, String comment) {
        this.memberId = memberId;
        this.providerName = providerName;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public String toString() {
        return "FeedbackRequest{" +
                "memberId='" + memberId + '\'' +
                ", providerName='" + providerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
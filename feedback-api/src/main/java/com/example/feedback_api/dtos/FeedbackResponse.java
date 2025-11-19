package com.example.feedback_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for feedback operations
 * Returned by GET and POST endpoints
 */
@Schema(description = "Response payload containing feedback details")
public class FeedbackResponse {

    @Schema(description = "Unique identifier for the feedback entry", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Unique identifier for the member/patient who provided feedback", example = "m-123456")
    private String memberId;

    @Schema(description = "Name of the healthcare provider that was reviewed", example = "Dr. Sarah Johnson")
    private String providerName;

    @Schema(description = "Rating score given for the provider experience", example = "4", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(description = "Detailed feedback comment about the provider experience", example = "Dr. Johnson was very professional and explained everything clearly. Great bedside manner!")
    private String comment;

    @Schema(description = "Timestamp when the feedback was submitted", example = "2025-11-10T20:23:00Z")
    private Instant submittedAt;

    // Default constructor for Jackson
    public FeedbackResponse() {
    }

    // Constructor for building responses
    public FeedbackResponse(UUID id, String memberId, String providerName,
            Integer rating, String comment, Instant submittedAt) {
        this.id = id;
        this.memberId = memberId;
        this.providerName = providerName;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "FeedbackResponse{" +
                "id=" + id +
                ", memberId='" + memberId + '\'' +
                ", providerName='" + providerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
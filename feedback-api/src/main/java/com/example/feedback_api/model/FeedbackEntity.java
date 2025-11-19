package com.example.feedback_api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity representing the feedback table in PostgreSQL
 * Maps to the database schema defined in the project spec
 */
@Schema(description = "Provider feedback entity representing a patient's feedback about their healthcare provider")
@Entity
@Table(name = "feedback")
public class FeedbackEntity {

    @Schema(description = "Unique identifier for the feedback entry", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Schema(description = "Unique identifier for the member/patient providing feedback", example = "m-123456", maxLength = 36, requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "member_id", nullable = false, length = 36)
    private String memberId;

    @Schema(description = "Name of the healthcare provider being reviewed", example = "Dr. Sarah Johnson", maxLength = 80, requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "provider_name", nullable = false, length = 80)
    private String providerName;

    @Schema(description = "Rating score for the provider experience", example = "4", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Schema(description = "Optional detailed feedback comment about the provider experience", example = "Dr. Johnson was very professional and explained everything clearly. Great bedside manner!", maxLength = 200, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Column(name = "comment", length = 200)
    private String comment;

    @Schema(description = "Timestamp when the feedback was submitted", example = "2025-11-10T20:23:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    // Default constructor (required by JPA)
    public FeedbackEntity() {
    }

    // Constructor for creating new feedback
    public FeedbackEntity(String memberId, String providerName, Integer rating, String comment) {
        this.memberId = memberId;
        this.providerName = providerName;
        this.rating = rating;
        this.comment = comment;
    }

    // Full constructor
    public FeedbackEntity(UUID id, String memberId, String providerName, Integer rating, String comment,
            Instant submittedAt) {
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

    // equals and hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FeedbackEntity that = (FeedbackEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FeedbackEntity{" +
                "id=" + id +
                ", memberId='" + memberId + '\'' +
                ", providerName='" + providerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
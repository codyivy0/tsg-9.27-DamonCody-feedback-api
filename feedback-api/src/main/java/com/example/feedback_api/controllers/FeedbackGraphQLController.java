package com.example.feedback_api.controllers;

import com.example.feedback_api.dtos.FeedbackRequest;
import com.example.feedback_api.dtos.FeedbackResponse;
import com.example.feedback_api.services.FeedbackService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL controller exposing feedback operations.
 *
 * Located alongside REST controllers so related endpoints live in the same package.
 */
@Controller
public class FeedbackGraphQLController {

    private final FeedbackService feedbackService;

    public FeedbackGraphQLController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @QueryMapping
    public List<FeedbackResponse> getFeedback(@Argument String memberId) {
        return feedbackService.getFeedback(memberId);
    }

    @QueryMapping
    public FeedbackResponse getFeedbackById(@Argument String id) {
        return feedbackService.getFeedbackById(id);
    }

    @MutationMapping
    public FeedbackResponse createFeedback(@Argument("input") FeedbackInput input) {
        // Map GraphQL input -> existing FeedbackRequest DTO
        FeedbackRequest req = new FeedbackRequest(
                input.getMemberId(),
                input.getProviderName(),
                input.getRating(),
                input.getComment()
        );
        return feedbackService.validateAndSave(req);
    }

    /**
     * POJO that mirrors the GraphQL `FeedbackInput` input type defined in schema.graphqls.
     * The default constructor is intentionally empty; it's required by the binder.
     */
    public static class FeedbackInput {
        private String memberId;
        private String providerName;
        private Integer rating;
        private String comment;

        // Default constructor required by the binder
        public FeedbackInput() {
            // intentionally empty
        }

        // Getters and setters
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
    }
}

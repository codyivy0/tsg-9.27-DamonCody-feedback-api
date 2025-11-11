package com.example.feedback_api.messaging;

import com.example.feedback_api.model.FeedbackEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeedbackEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackEventPublisher.class);
    private static final String FEEDBACK_TOPIC = "feedback-submitted";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public FeedbackEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SuppressWarnings("null") // Kafka template requires this for framework integration
    public void publishFeedbackSubmitted(FeedbackEntity feedback) {
        if (feedback == null || feedback.getId() == null) {
            logger.warn("Cannot publish feedback event: feedback or ID is null");
            return;
        }
        
        try {
            // Create message payload matching consumer's expected format
            Map<String, Object> message = new HashMap<>();
            message.put("id", feedback.getId().toString());
            message.put("memberId", feedback.getMemberId());
            message.put("providerName", feedback.getProviderName());
            message.put("rating", feedback.getRating());
            message.put("comment", feedback.getComment());
            message.put("submittedAt", feedback.getSubmittedAt().toString());

            // Send message asynchronously - fire and forget
            String feedbackId = feedback.getId().toString(); // ID is guaranteed non-null here
            kafkaTemplate.send(FEEDBACK_TOPIC, feedbackId, message);

        } catch (Exception e) {
            logger.error("Failed to publish feedback event for ID {}: {}",
                    feedback.getId(), e.getMessage());
        }
    }
}
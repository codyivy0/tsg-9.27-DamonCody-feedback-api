package com.example.feedback_api.repositories;

import com.example.feedback_api.model.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for FeedbackEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, UUID> {

    /**
     * Find all feedback entries ordered by submission time (newest first)
     * Used for GET /feedback endpoint without filtering
     * 
     * @return list of all feedback entries ordered by submittedAt descending
     */
    List<FeedbackEntity> findAllByOrderBySubmittedAtDesc();

    /**
     * Find feedback entries for a specific member, ordered by submission time
     * (newest first)
     * Useful for displaying recent feedback first
     * 
     * @param memberId the member identifier to search for
     * @return list of feedback entries ordered by submittedAt descending
     */
    List<FeedbackEntity> findByMemberIdOrderBySubmittedAtDesc(String memberId);

    /**
     * Check if a member has already provided feedback for a specific provider
     * Useful for preventing duplicate feedback from the same member
     * 
     * @param memberId     the member identifier
     * @param providerName the provider name
     * @return true if feedback exists, false otherwise
     */
    boolean existsByMemberIdAndProviderName(String memberId, String providerName);
}
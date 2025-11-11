package com.example.feedback_api.repositories;

import com.example.feedback_api.model.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Find all feedback entries for a specific member
     * Maps to GET /feedback?memberId=<id> endpoint
     * 
     * @param memberId the member identifier to search for
     * @return list of feedback entries (may be empty)
     */
    List<FeedbackEntity> findByMemberId(String memberId);
    
    /**
     * Find feedback entries for a specific member, ordered by submission time (newest first)
     * Useful for displaying recent feedback first
     * 
     * @param memberId the member identifier to search for
     * @return list of feedback entries ordered by submittedAt descending
     */
    List<FeedbackEntity> findByMemberIdOrderBySubmittedAtDesc(String memberId);
    
    /**
     * Find feedback entries for a specific provider
     * Useful for provider analytics and reviews
     * 
     * @param providerName the provider name to search for
     * @return list of feedback entries for the provider
     */
    List<FeedbackEntity> findByProviderName(String providerName);
    
    /**
     * Find feedback entries with a specific rating
     * Useful for filtering by star rating
     * 
     * @param rating the rating value (1-5)
     * @return list of feedback entries with the specified rating
     */
    List<FeedbackEntity> findByRating(Integer rating);
    
    /**
     * Find feedback entries for a provider with minimum rating
     * Example: Find all 4+ star reviews for a provider
     * 
     * @param providerName the provider name
     * @param minRating minimum rating threshold
     * @return list of feedback entries matching criteria
     */
    @Query("SELECT f FROM FeedbackEntity f WHERE f.providerName = :providerName AND f.rating >= :minRating ORDER BY f.submittedAt DESC")
    List<FeedbackEntity> findByProviderNameAndRatingGreaterThanEqual(
            @Param("providerName") String providerName, 
            @Param("minRating") Integer minRating);
    
    /**
     * Count total feedback entries for a specific provider
     * Useful for provider statistics
     * 
     * @param providerName the provider name
     * @return count of feedback entries
     */
    long countByProviderName(String providerName);
    
    /**
     * Check if a member has already provided feedback for a specific provider
     * Useful for preventing duplicate feedback from the same member
     * 
     * @param memberId the member identifier
     * @param providerName the provider name
     * @return true if feedback exists, false otherwise
     */
    boolean existsByMemberIdAndProviderName(String memberId, String providerName);
}
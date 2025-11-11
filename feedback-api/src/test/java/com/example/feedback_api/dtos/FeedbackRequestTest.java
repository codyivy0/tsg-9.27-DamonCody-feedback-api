package com.example.feedback_api.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

/**
 * Unit tests for FeedbackRequest DTO validation
 * Tests all validation rules without needing a running application
 */
public class FeedbackRequestTest {
    
    private Validator validator;
    
    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    // happy path test
    @Test
    public void testValidFeedbackRequest() {
        // Given - Valid feedback request
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId("m-12345");
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(4);
        request.setComment("Great experience!");
        
        // When - Validate the request
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        
        // Then - No validation errors
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }
    
    @Test
    public void testMemberIdRequired() {
        // Given - Request missing memberId
        FeedbackRequest request = new FeedbackRequest();
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(4);
        
        // When - Validate
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        
        // Then - Should have violation for memberId
        assertEquals(1, violations.size());
        ConstraintViolation<FeedbackRequest> violation = violations.iterator().next();
        assertEquals("memberId", violation.getPropertyPath().toString());
        assertEquals("Member ID is required", violation.getMessage());
    }
    
    @Test
    public void testMemberIdTooLong() {
        // Given - MemberId longer than 36 characters
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId("m-" + "x".repeat(35)); // 37 characters total
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(4);
        
        // When - Validate
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        
        // Then - Should have size violation
        assertEquals(1, violations.size());
        ConstraintViolation<FeedbackRequest> violation = violations.iterator().next();
        assertEquals("Member ID must be 36 characters or less", violation.getMessage());
    }
    
    @Test
    public void testRatingOutOfRange() {
        // Given - Rating outside 1-5 range
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId("m-12345");
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(6); // Invalid rating
        
        // When - Validate
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        
        // Then - Should have rating violation
        assertEquals(1, violations.size());
        ConstraintViolation<FeedbackRequest> violation = violations.iterator().next();
        assertEquals("rating", violation.getPropertyPath().toString());
        assertEquals("Rating must be at most 5", violation.getMessage());
    }
    
    @Test
    public void testCommentTooLong() {
        // Given - Comment longer than 200 characters
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId("m-12345");
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(4);
        request.setComment("x".repeat(201)); // 201 characters
        
        // When - Validate
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        
        // Then - Should have comment length violation
        assertEquals(1, violations.size());
        ConstraintViolation<FeedbackRequest> violation = violations.iterator().next();
        assertEquals("Comment must be 200 characters or less", violation.getMessage());
    }
    
    @Test
    public void testOptionalCommentCanBeNull() {
        // Given - Request without comment (should be valid)
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId("m-12345");
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(4);
        // comment is null/not set
        
        // When - Validate
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        
        // Then - Should be valid (comment is optional)
        assertTrue(violations.isEmpty(), "Request without comment should be valid");
    }
    
    @Test
    public void testFieldWithoutAnnotationsAcceptsAnything() {
        // Given - Let's test the comment field which only has @Size (no @NotBlank)
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId("m-12345");
        request.setProviderName("Dr. Sarah Johnson");
        request.setRating(4);
        
        // Test 1: null comment (no annotation requires it)
        request.setComment(null);
        Set<ConstraintViolation<FeedbackRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Null comment should be valid (no @NotBlank)");
        
        // Test 2: empty string comment (no @NotBlank annotation)
        request.setComment("");
        violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Empty comment should be valid (no @NotBlank)");
        
        // Test 3: whitespace-only comment (no @NotBlank annotation)
        request.setComment("   ");
        violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Whitespace-only comment should be valid (no @NotBlank)");
        
        // Test 4: Only @Size annotation applies
        request.setComment("x".repeat(201)); // Over 200 characters
        violations = validator.validate(request);
        assertEquals(1, violations.size(), "Comment over 200 chars should fail @Size validation");
        assertEquals("Comment must be 200 characters or less", 
                    violations.iterator().next().getMessage());
    }
}
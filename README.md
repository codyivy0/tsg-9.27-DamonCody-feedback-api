# tsg-9.27-DamonCody-feedback-api

## Development Setup

### Starting Services
```bash
docker-compose up -d
```

### Service URLs
- **Frontend UI**: http://localhost:3000
- **Spring Boot API**: http://localhost:8080
- **Swagger UI (API Docs)**: http://localhost:8080/swagger-ui.html
- **Analytics Consumer**: http://localhost:8081
- **Kafka UI Dashboard**: http://localhost:8090
- **PostgreSQL**: localhost:5433

### Health Checks
Health checks are now available directly through **Swagger UI** at http://localhost:8080/swagger-ui.html:
- **API Health**: `GET /api/v1/health` - Main feedback API service status
- **Analytics Health**: `GET /api/v1/health/analytics` - Analytics consumer service status

*Note: Health endpoints provide detailed status information and are integrated into the API documentation.*

## Testing the Complete End-to-End Flow

### 🎯 GUI Testing Workflow (Recommended)

This workflow tests the complete flow: **API → Database → Kafka → Consumer** using visual dashboards and integrated health monitoring.

#### **Step 1: Open Testing Dashboards**
1. **Swagger UI**: http://localhost:8080/swagger-ui.html *(Primary testing interface)*
2. **Kafka UI**: http://localhost:8090 *(Message monitoring)*

#### **Step 2: Verify System Health via Swagger**
1. In Swagger UI, expand the **"Health"** section
2. Test `GET /api/v1/health` - Should return status "UP" for main API
3. Test `GET /api/v1/health/analytics` - Should return status "UP" for analytics consumer
4. ✅ **Expected**: Both endpoints return 200 OK with healthy status

#### **Step 3: Submit Feedback via Swagger UI**
1. Expand **"Feedback Operations"** section  
2. Find `POST /api/v1/feedback` endpoint
3. Click "Try it out"
4. Use this sample JSON:
```json
{
  "memberId": "test-gui-001",
  "providerName": "Dr. GUI Test",
  "rating": 5,
  "comment": "Testing via GUI - this is awesome!"
}
```
5. Click "Execute"
6. ✅ **Expected**: 201 Created response with generated ID and timestamp

*💡 **Tip**: After submitting feedback, test the `GET /api/v1/feedback` and `GET /api/v1/feedback/{id}` endpoints in Swagger to verify data persistence!*

#### **Step 4: Verify Kafka Message via Kafka UI**
1. Go to Kafka UI dashboard: http://localhost:8090
2. Click **"Topics"** in left navigation
3. Click **"feedback-submitted"** topic
4. Click **"Messages"** tab
5. ✅ **Expected**: New message at top with:
   - **Key**: Your feedback UUID
   - **Value**: JSON payload with all feedback data
   - **Timestamp**: Recent submission time

#### **Step 5: Check Consumer Processing**
```bash
docker logs feedback-analytics-consumer --tail 5
```
✅ **Expected**: Log entry like:
```
Received feedback (id=...) rating=5 provider='Dr. GUI Test' member='test-gui-001' comment='...' submittedAt='...'
```

#### **Step 6: Verify Database Persistence**
```bash
docker exec feedback-postgres psql -U postgres -d feedbackdb -c "SELECT * FROM feedback ORDER BY submitted_at DESC LIMIT 1;"
```
✅ **Expected**: Latest feedback record in database

### 🔄 Continuous Testing Loop

For ongoing development testing, use **Swagger UI** as your primary interface:

1. **Health Check** → Use Swagger `/api/v1/health` endpoints to verify services
2. **Submit** → Feedback via Swagger UI (vary member IDs, providers, ratings)  
3. **Verify** → 201 response in Swagger interface
4. **Check** → New message in Kafka UI (refresh Messages tab)
5. **Confirm** → Consumer log shows structured analytics
6. **Validate** → Database contains record (optional)

### 🧪 Test Scenarios

#### **Happy Path Tests**
- Valid feedback with all fields
- Different ratings (1-5)
- Various member IDs and providers
- With and without comments

#### **Validation Tests**
- Missing required fields → 400 Bad Request
- Invalid rating (0, 6, negative) → 400 Bad Request
- Comment over 200 characters → 400 Bad Request
- Duplicate member/provider combination → 400 Bad Request

#### **Sample Test Data**
```json
// Valid feedback
{
  "memberId": "m-12345",
  "providerName": "Dr. Smith",
  "rating": 4,
  "comment": "Great care and very professional."
}

// Minimal valid feedback
{
  "memberId": "m-67890",
  "providerName": "Dr. Johnson",
  "rating": 5
}

// Invalid rating (should fail)
{
  "memberId": "m-99999",
  "providerName": "Dr. Test",
  "rating": 6,
  "comment": "This should fail validation"
}
```

## 🧪 Unit Testing

### Running Tests

#### **Run All Tests**
```bash
cd feedback-api
./mvnw test
```

#### **Run Specific Test Class**
```bash
# Test only DTO validation
./mvnw test -Dtest=FeedbackRequestTest

# Test only service logic
./mvnw test -Dtest=FeedbackServiceTest

# Test only controller endpoints
./mvnw test -Dtest=FeedbackControllerTest
```

#### **Run Tests with Verbose Output**
```bash
./mvnw test -X
```

### Test Coverage Overview

**✅ Current Test Suite: 20 Unit Tests**

#### **Test Categories:**

##### **1. DTO Validation Tests** (`FeedbackRequestTest.java`) - 7 tests
Tests all Jakarta validation annotations on the request DTO:
- ✅ Valid feedback request validation
- ✅ Required field validation (`@NotBlank`, `@NotNull`)
- ✅ Field length constraints (`@Size`)
- ✅ Rating range validation (`@Min`, `@Max`)
- ✅ Optional comment field handling
- ✅ Edge cases (null, empty, whitespace values)

##### **2. Service Layer Tests** (`FeedbackServiceTest.java`) - 7 tests
Tests business logic and service operations with mocked dependencies:
- ✅ Feedback creation and database persistence
- ✅ Duplicate feedback prevention (business rule)
- ✅ Business validation (beyond DTO constraints)
- ✅ GET feedback with optional member ID filtering
- ✅ Kafka event publishing integration
- ✅ Error handling and exception scenarios
- ✅ Data mapping (DTO ↔ Entity transformations)

##### **3. Controller Layer Tests** (`FeedbackControllerTest.java`) - 6 tests
Tests HTTP endpoints using MockMvc (no real server):
- ✅ `POST /api/v1/feedback` → 201 Created response
- ✅ `POST /api/v1/feedback` → 400 validation error handling
- ✅ `GET /api/v1/feedback?memberId=<id>` → 200 filtered results
- ✅ `GET /api/v1/feedback` → 200 all feedback
- ✅ `GET /api/v1/health` → 200 health check
- ✅ JSON serialization/deserialization

### Test Results Summary

```bash
Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
```

**Breakdown:**
- **20 Unit Tests**: All passing ✅


### Test Architecture

#### **Testing Strategy:**
- **Unit Tests**: Fast, isolated, no external dependencies
- **Mocking**: Uses Mockito for repository and Kafka dependencies
- **Test Slices**: `@WebMvcTest` for controllers, isolated service tests
- **Validation Testing**: Bean Validation API with real validator

#### **Key Testing Patterns:**

##### **AAA Pattern** (Arrange, Act, Assert)
```java
@Test
void createFeedback_ValidRequest_ShouldReturn201() {
    // Arrange - Set up test data and mocks
    FeedbackRequest request = new FeedbackRequest();
    request.setMemberId("test-123");
    // ... more setup
    
    // Act - Execute the operation
    FeedbackResponse response = feedbackService.validateAndSave(request);
    
    // Assert - Verify the results
    assertEquals("test-123", response.getMemberId());
    verify(mockRepository).save(any());
}
```

##### **MockMvc for HTTP Testing**
```java
mockMvc.perform(post("/api/v1/feedback")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.memberId").value("test-123"));
```

##### **Mockito for Isolation**
```java
@Mock
private FeedbackRepository feedbackRepository;

@Mock 
private FeedbackEventPublisher eventPublisher;

when(feedbackRepository.save(any())).thenReturn(mockEntity);
```

#### **Edge Case Testing**
- Null values, empty strings, whitespace-only inputs
- Boundary values (rating 1, 5, 0, 6)
- Maximum field lengths (36 chars for memberId, 200 for comment)
- Business rule violations (duplicate feedback)

#### **Log Monitoring**
```bash
# Follow API logs
docker logs feedback-api --follow

# Follow Consumer logs  
docker logs feedback-analytics-consumer --follow

# Check database activity
docker logs feedback-postgres --follow
```

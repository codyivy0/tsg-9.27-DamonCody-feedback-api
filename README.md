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
- **Kafka Broker**: localhost:9092

### Health Check URLs
- **Frontend UI Health**: http://localhost:3000 (nginx status)
- **Main API Health**: http://localhost:8080/actuator/health
- **Analytics Consumer Health**: http://localhost:8081/actuator/health

## Testing the Complete End-to-End Flow

### 🎯 GUI Testing Workflow (Recommended)

This workflow tests the complete flow: **API → Database → Kafka → Consumer** using visual dashboards.

#### **Step 1: Open Testing Dashboards**
1. **Swagger UI**: http://localhost:8080/swagger-ui.html
2. **Kafka UI**: http://localhost:8090

#### **Step 2: Submit Feedback via Swagger UI**
1. Navigate to Swagger UI
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

#### **Step 3: Verify Kafka Message via Kafka UI**
1. Go to Kafka UI dashboard: http://localhost:8090
2. Click **"Topics"** in left navigation
3. Click **"feedback-submitted"** topic
4. Click **"Messages"** tab
5. ✅ **Expected**: New message at top with:
   - **Key**: Your feedback UUID
   - **Value**: JSON payload with all feedback data
   - **Timestamp**: Recent submission time

#### **Step 4: Check Consumer Processing**
```bash
docker logs feedback-analytics-consumer --tail 5
```
✅ **Expected**: Log entry like:
```
Received feedback (id=...) rating=5 provider='Dr. GUI Test' member='test-gui-001' comment='...' submittedAt='...'
```

#### **Step 5: Verify Database Persistence**
```bash
docker exec feedback-postgres psql -U postgres -d feedbackdb -c "SELECT * FROM feedback ORDER BY submitted_at DESC LIMIT 1;"
```
✅ **Expected**: Latest feedback record in database

### 🔄 Continuous Testing Loop

For ongoing development testing:

1. **Submit** → Feedback via Swagger UI (vary member IDs, providers, ratings)
2. **Verify** → 201 response in Swagger
3. **Check** → New message in Kafka UI (refresh Messages tab)
4. **Confirm** → Consumer log shows structured analytics
5. **Validate** → Database contains record (optional)

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

**✅ Current Test Suite: 20 Unit Tests + 1 Integration Test (disabled)**

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
Tests run: 21, Failures: 0, Errors: 0, Skipped: 1
```

**Breakdown:**
- **20 Unit Tests**: All passing ✅
- **1 Integration Test**: Intentionally disabled (requires Docker containers)

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

### Test Data Management

#### **Test Data Setup**
```java
@BeforeEach
void setUp() {
    validRequest = new FeedbackRequest();
    validRequest.setMemberId("member-123");
    validRequest.setProviderName("Dr. Smith");
    validRequest.setRating(4);
    validRequest.setComment("Great service!");
}
```

#### **Edge Case Testing**
- Null values, empty strings, whitespace-only inputs
- Boundary values (rating 1, 5, 0, 6)
- Maximum field lengths (36 chars for memberId, 200 for comment)
- Business rule violations (duplicate feedback)

### Testing Best Practices Used

1. **Fast Execution** - All unit tests complete in ~3 seconds
2. **Isolated Tests** - No shared state between tests
3. **Realistic Data** - Uses meaningful test values
4. **Clear Naming** - Test method names describe scenario and expectation
5. **Comprehensive Coverage** - Tests happy path, edge cases, and errors
6. **Proper Mocking** - Dependencies are mocked, not real implementations

### 📊 Monitoring

#### **Real-time Kafka Monitoring**
- **Kafka UI**: http://localhost:8090
- View message throughput, partition details, and consumer lag
- Monitor topic health and message formats

#### **Application Health**
- **API Health**: http://localhost:8080/actuator/health
- **Consumer Health**: http://localhost:8081/actuator/health
- **Database Connection**: Included in health endpoints

#### **Log Monitoring**
```bash
# Follow API logs
docker logs feedback-api --follow

# Follow Consumer logs  
docker logs feedback-analytics-consumer --follow

# Check database activity
docker logs feedback-postgres --follow
```

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

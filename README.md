# tsg-9.27-DamonCody-feedback-api

## Development Setup

### Starting Services
```bash
docker-compose up -d
```

### Service URLs
- **Frontend UI**: http://localhost:3000
- **Spring Boot API**: http://localhost:8080
- **Analytics Consumer**: http://localhost:8081
- **Kafka UI Dashboard**: http://localhost:8090
- **PostgreSQL**: localhost:5433
- **Kafka Broker**: localhost:9092

### Health Check URLs
- **Frontend UI Health**: http://localhost:3000 (nginx status)
- **Main API Health**: http://localhost:8080/actuator/health
- **Analytics Consumer Health**: http://localhost:8081/actuator/health

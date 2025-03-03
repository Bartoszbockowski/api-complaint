# Complaint API

## Overview
Complaint API is a RESTful service for handling product complaints. It allows users to create, update, and retrieve complaints while integrating with an external GeoLocation API to determine the country of the user based on their IP address.

## Running with Docker Compose
To start the application using Docker Compose:

```bash
  docker-compose up --build
```

To stop the application:

```bash
  docker-compose down
```

### Docker Configuration
- **PostgreSQL Container**:
    - Image: `postgres:latest`
    - Ports: `5432:5432`
    - Volume: `postgres-data:/var/lib/postgresql/data`
- **Spring Boot Application**:
    - Ports: `8080:8080`
    - Connects to PostgreSQL
    - Uses Liquibase for database migrations
  
## API Documentation
After starting the application via Docker, the API documentation is available at:

👉 [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

## Resilience4j Configuration
The application includes Resilience4j for fault tolerance with the external GeoLocation service:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      geoLocationService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
  retry:
    instances:
      geoLocationServiceRetry:
        max-attempts: 3
        wait-duration: 500ms
```


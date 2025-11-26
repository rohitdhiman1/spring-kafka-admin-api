# Spring Kafka Admin API

A modern Kafka administration platform with a RESTful API and React-based web dashboard. Manage Apache Kafka topics, consumer groups, and monitor cluster health through a beautiful, responsive UI or programmatic API.

## Features

### Backend API
- 🔌 RESTful API with HATEOAS support
- 📋 Topic management (create, list, describe, delete)
- 👥 Consumer group monitoring
- 🖥️ Cluster information and health checks
- 🔍 Under-replicated partition detection
- 📊 Spring Boot Actuator metrics

### Frontend Dashboard
- ⚡ Modern React 18 + TypeScript UI
- 🌓 Dark mode support
- 📱 Mobile-responsive design
- 🔄 Real-time auto-refresh
- 🎨 Tailwind CSS styling
- 📈 Interactive charts and metrics

## Prerequisites

- Java 17 or later
- Maven
- Docker & Docker Compose
- Node.js 18+ (for frontend development)

## Development & Testing Strategy

This project follows industry best practices to enable fast development cycles and reliable testing.

### Testing Approach

We separate tests into two categories for optimal development speed:

#### 1. Unit Tests (Fast Feedback Loop)
- **Purpose**: Test business logic in isolation with mocked dependencies
- **Speed**: Runs in seconds, no Docker required
- **Run with**: `mvn test -Dtest=*UnitTest`
- **Use when**: Making code changes, fast local development

#### 2. Integration Tests (Full System Verification)
- **Purpose**: Test actual Kafka interactions with real brokers
- **Speed**: ~30 seconds, uses Testcontainers (auto-manages Docker)
- **Run with**: `mvn verify` or `mvn test -Dtest=*IntegrationTest`
- **Use when**: Before commits, PRs, CI/CD pipelines

#### 3. Docker Compose Environment (Local Development)
- **Purpose**: Full environment for manual testing, debugging, and UI access
- **Includes**: 4 Kafka brokers, Zookeeper, Kafdrop UI, Spring application
- **Use when**: Interactive development, exploring Kafka via UI

### Recommended Workflow

```bash
# 1. Fast development cycle (unit tests only - runs in ~3 seconds)
# Make code changes, run unit tests with mocked dependencies
mvn test -Dtest="*UnitTest"

# 2. Integration verification (before commit - runs in ~30 seconds)
# Tests with real Kafka using Testcontainers (auto-manages Docker)
mvn test

# Or run full build with integration tests
mvn verify

# 3. Local environment (manual testing/debugging)
# Start full stack with Docker Compose
docker-compose up -d

# Access services:
# - API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Kafdrop (Kafka UI): http://localhost:9000

# Stop environment
docker-compose down
```

### Quick Commands Reference

| Command | Purpose | Speed | Docker Daemon Required |
|---------|---------|-------|------------------------|
| `mvn test -Dtest="*UnitTest"` | Run unit tests only | ~3 sec | ❌ **No** - Pure unit tests with mocks |
| `mvn test` | Run all tests | ~30 sec | ✅ **Yes** - Testcontainers auto-starts Kafka |
| `mvn package -DskipTests` | Build without tests | ~5 sec | ❌ No |
| `mvn verify` | Full build + all tests | ~30 sec | ✅ **Yes** - Testcontainers auto-starts Kafka |
| `docker-compose up -d` | Start local env | N/A | ✅ Yes |
| `docker-compose down` | Stop local env | N/A | ✅ Yes |

**Docker Requirements Explained:**

- **No Docker needed:** Unit tests (`*UnitTest`) use mocked dependencies - zero infrastructure required
- **Docker daemon must be running:** `mvn test` and `mvn verify` use Testcontainers which automatically manages Kafka containers
  - You DON'T need to run `docker-compose up` manually
  - Testcontainers handles starting/stopping containers automatically
  - Just ensure Docker Desktop (or Docker daemon) is running in the background
- **Manual Docker Compose:** Only needed for local development, manual testing, or accessing Kafdrop UI

### Why This Approach?

✅ **Fast feedback**: Unit tests give instant results  
✅ **No manual setup**: Testcontainers auto-manages Kafka for integration tests  
✅ **Full environment available**: Docker Compose for comprehensive testing  
✅ **CI/CD ready**: All tests run without pre-configuration  
✅ **Developer friendly**: Choose the right tool for each task

## Quick Start

### Option 1: Full Stack with Frontend (Recommended for Users)

```bash
# 1. Build backend
mvn clean package

# 2. Start all services
docker-compose up --build

# 3. In another terminal, start frontend
cd frontend
npm install
npm run dev

# Access services:
# - Frontend Dashboard: http://localhost:3000
# - Backend API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Kafdrop (Kafka UI): http://localhost:9000
```

### Option 2: Backend Only (For API Development)

```bash
# 1. Package the Spring Boot Application
mvn clean package

# 2. Build and Run All Services
docker-compose up --build

# Access services:
# - API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Kafdrop (Kafka UI): http://localhost:9000
```

### Option 3: Development Mode (Backend + Frontend)

```bash
# Terminal 1: Start Kafka infrastructure only
docker-compose up kafka1 kafka2 kafka3 kafka4 zookeeper kafdrop

# Terminal 2: Run backend locally
mvn spring-boot:run

# Terminal 3: Run frontend locally
cd frontend
npm run dev

# Access services:
# - Frontend: http://localhost:3000
# - Backend: http://localhost:8080
# - Kafdrop: http://localhost:9000
```

## Frontend Dashboard

The React-based dashboard provides:

- **Dashboard**: Real-time metrics, cluster health, under-replicated partitions
- **Topics**: Create, list, delete topics with detailed information
- **Consumer Groups**: Monitor consumer group status and state
- **Cluster**: View broker nodes and cluster information
- **Dark Mode**: Toggle between light and dark themes
- **Responsive**: Works on desktop, tablet, and mobile

See [frontend/README.md](frontend/README.md) for detailed frontend documentation.

## How to Stop and Tear Down the Environment

To cleanly stop all running containers, remove them, and tear down the network created by Docker Compose, run the following command:

```bash
docker-compose down
```

This command is the standard and safest way to tear down a Docker Compose environment. It performs the following actions:

-   **Stops** all the containers defined in your `docker-compose.yml` file.
-   **Removes** the stopped containers.
-   **Removes** the networks that were created for the services.

### Removing Volumes (Optional)

By default, `docker-compose down` does not remove the volumes created by your services (like the Kafka data volume). This is a safety feature to prevent data loss. If you want to remove the volumes as well for a complete cleanup, use the `-v` flag:

```bash
docker-compose down -v
```

This is useful when you want to start from a completely fresh state.

## API Documentation

This project uses `springdoc-openapi` to automatically generate interactive API documentation. Once the application is running, you can access the documentation in your browser:

-   **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
-   **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

The Swagger UI allows you to explore and test all the API endpoints directly from your browser.

## API Usage


## Health Check

This project exposes a health check endpoint using Spring Boot Actuator. You can use this endpoint to verify that the application is running and healthy.

### Health Check Endpoint

Returns the health status of the application and its dependencies (such as Kafka).

```bash
curl -X GET http://localhost:8080/actuator/health
```

**Sample Response:**

```json
{
    "status": "UP",
    "components": {
        "kafka": {
            "status": "UP",
            "details": {
                "clusterId": "...",
                "brokers": [ ... ]
            }
        },
        ...
    }
}
```

You can also view detailed health information in your browser at [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

---

Here are some examples of how to use the API with `curl`.

### Describe the Cluster

Provides information about the brokers in the Kafka cluster.

```bash
curl -X GET http://localhost:8080/api/cluster
```

### List all topics

```bash
curl -X GET http://localhost:8080/api/topics
```

### Create a new topic

```bash
curl -X POST http://localhost:8080/api/topics \
-H "Content-Type: application/json" \
-d '{"topicName": "my-new-topic"}'
```

### Describe a topic

```bash
curl -X GET http://localhost:8080/api/topics/my-new-topic
```

### Delete a topic

```bash
curl -X DELETE http://localhost:8080/api/topics/my-new-topic
```

### List all consumer groups

```bash
curl -X GET http://localhost:8080/api/consumer-groups
```

### Describe a consumer group

```bash
curl -X GET http://localhost:8080/api/consumer-groups/my-consumer-group
```

### Find Under-Replicated Partitions
Returns a map of topics to a list of under-replicated partition numbers. This is useful for monitoring the health of the cluster.

```bash
curl -X GET http://localhost:8080/api/topics/under-replicated
```

**Sample Response:**

```json
{
  "my-topic-1": [0, 2],
  "my-topic-2": [1]
}
```

## HATEOAS & Enhanced Topic Listing

The `/api/topics` endpoint now returns a HATEOAS-enabled response. Each topic includes meaningful key-value pairs and hypermedia links to guide clients on available actions.

**Sample Response:**

```json
{
  "_embedded": {
    "entityModelList": [
      {
        "name": "my-topic",
        "status": "available",
        "message": "Kafka topic resource",
        "_links": {
          "self": { "href": "http://localhost:8080/api/topics/my-topic" },
          "delete": { "href": "http://localhost:8080/api/topics/my-topic" }
        }
      },
      // ... more topics ...
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/api/topics" }
  }
}
```

- The `self` link points to the topic's details endpoint.
- The `delete` link points to the topic's delete endpoint.
- Each topic object includes `name`, `status`, and a descriptive `message`.

Clients should use these links to discover available actions and navigate the API in a RESTful manner.

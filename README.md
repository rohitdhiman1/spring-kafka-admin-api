# Spring Kafka Admin API

This project provides a RESTful API to manage Apache Kafka topics, consumer groups, and view cluster information. It's a Spring Boot application that uses the Kafka AdminClient to interact with a Kafka cluster. The API allows you to perform basic administrative tasks on Kafka resources, such as creating, listing, describing, and deleting them, as well as viewing information about the cluster nodes.

This provides a simple and convenient way to manage your Kafka resources programmatically without having to use the command-line tools that come with Kafka.

## Prerequisites

- Java 17 or later
- Maven
- Docker
- Docker Compose

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
# 1. Fast development cycle (unit tests only)
# Make code changes, run unit tests
mvn test

# 2. Integration verification (before commit)
# Tests with real Kafka using Testcontainers
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

| Command | Purpose | Speed | Docker Required |
|---------|---------|-------|-----------------|
| `mvn test` | Run all tests | Fast/Medium | No (uses Testcontainers) |
| `mvn test -DskipTests` | Build without tests | Fastest | No |
| `mvn verify` | Full build + all tests | Medium | No (uses Testcontainers) |
| `docker-compose up -d` | Start local env | N/A | Yes |
| `docker-compose down` | Stop local env | N/A | Yes |

### Why This Approach?

✅ **Fast feedback**: Unit tests give instant results  
✅ **No manual setup**: Testcontainers auto-manages Kafka for integration tests  
✅ **Full environment available**: Docker Compose for comprehensive testing  
✅ **CI/CD ready**: All tests run without pre-configuration  
✅ **Developer friendly**: Choose the right tool for each task

## How to run locally

This project is configured to run entirely within Docker containers, which simplifies setup and ensures a consistent environment.

1.  **Package the Spring Boot Application:**

    First, build the application's JAR file using Maven. This command cleans the project and packages it into a `.jar` file in the `target/` directory.

    ```bash
    mvn clean package
    ```

2.  **Build and Run All Services:**

    Use Docker Compose to build the Spring Boot application image and start all the services (Spring app, Kafka, Zookeeper, and Kafdrop).

    ```bash
    docker-compose up --build
    ```

    -   The `--build` flag tells Docker Compose to rebuild the `spring-app` image to include your latest code changes.
    -   Your application API will be available at `http://localhost:8080`.
    -   You can view the Kafka cluster UI (Kafdrop) at `http://localhost:9000`.

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

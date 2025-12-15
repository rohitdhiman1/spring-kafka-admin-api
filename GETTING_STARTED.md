# Getting Started with Kafka Admin Dashboard

This guide will walk you through setting up and using the Kafka Admin Dashboard.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Architecture Overview](#architecture-overview)
3. [Development Setup](#development-setup)
4. [Using the Dashboard](#using-the-dashboard)
5. [API Usage](#api-usage)
6. [Troubleshooting](#troubleshooting)

## Quick Start

The fastest way to get up and running:

```bash
# 1. Clone and navigate to the project
cd spring-kafka-admin-api

# 2. Build the backend
mvn clean package

# 3. Start all services (Kafka + Backend)
docker-compose up -d

# 4. Wait for services to initialize (~30 seconds)
sleep 30

# 5. Install frontend dependencies and start
cd frontend
npm install
npm run dev

# 6. Open your browser
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# Kafdrop UI: http://localhost:9000
```

## Architecture Overview

The Kafka Admin Dashboard consists of three main components:

### 1. Backend API (Spring Boot)
- **Port**: 8080
- **Technology**: Java 17, Spring Boot 3.5.9, Spring Kafka
- **Features**: REST API, HATEOAS, Actuator metrics
- **Location**: Root directory

### 2. Frontend Dashboard (React)
- **Port**: 3000
- **Technology**: React 18, TypeScript, Vite, Tailwind CSS
- **Features**: Real-time updates, dark mode, responsive design
- **Location**: `frontend/` directory

### 3. Kafka Cluster
- **Ports**: 9092-9095 (4 brokers)
- **Technology**: Apache Kafka with Zookeeper
- **UI**: Kafdrop on port 9000

```
┌─────────────────┐
│  Browser :3000  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ React Dashboard │ (Vite Dev Server)
└────────┬────────┘
         │ HTTP Proxy
         ▼
┌─────────────────┐
│ Spring Boot API │ :8080
└────────┬────────┘
         │ Admin Client
         ▼
┌─────────────────┐
│ Kafka Cluster   │ :9092-9095
│  4 Brokers      │
└─────────────────┘
```

## Development Setup

### Prerequisites

Ensure you have the following installed:

- **Java 17+**: `java -version`
- **Maven 3.6+**: `mvn -version`
- **Docker**: `docker --version`
- **Node.js 18+**: `node --version`
- **npm 8+**: `npm --version`

### Step-by-Step Setup

#### 1. Build Backend

```bash
# Clean and package the application
mvn clean package

# This creates: target/spring-kafka-admin-api-1.0-SNAPSHOT.jar
```

#### 2. Start Infrastructure

```bash
# Start Kafka cluster and backend
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f spring-app
```

**Services Started:**
- 4 Kafka brokers (kafka1-kafka4)
- 1 Zookeeper instance
- Spring Boot API
- Kafdrop UI

#### 3. Verify Backend

```bash
# Check health
curl http://localhost:8080/actuator/health

# List topics
curl http://localhost:8080/api/topics

# View Swagger UI
open http://localhost:8080/swagger-ui.html
```

#### 4. Setup Frontend

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend will be available at http://localhost:3000
```

#### 5. Access Services

Open your browser to:

- **Dashboard**: http://localhost:3000
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Kafdrop**: http://localhost:9000

## Using the Dashboard

### Dashboard Overview

The main dashboard shows:
- Total topics count
- Consumer groups count
- Cluster nodes count
- Under-replicated partitions (if any)
- Cluster information
- Recent topics

**Auto-refresh**: Data refreshes every 30 seconds automatically.

### Managing Topics

#### Create a Topic

1. Navigate to **Topics** page
2. Click **Create Topic** button
3. Fill in the form:
   - **Topic Name**: e.g., `my-new-topic`
   - **Partitions**: Number of partitions (default: 1)
   - **Replication Factor**: Replication factor (default: 1)
4. Click **Create**

#### View Topics

- Topics page shows all topics in a table
- Each topic displays its name and status
- Use the **Refresh** button to reload the list

#### Delete a Topic

1. Find the topic in the list
2. Click the trash icon
3. Confirm deletion by clicking **Yes**

### Monitoring Consumer Groups

The Consumer Groups page displays:
- Group ID
- Current state (Stable, Empty, etc.)

States:
- **Stable**: Active consumers, healthy
- **Empty**: No active consumers
- **Other**: Various transitional states

### Viewing Cluster Information

The Cluster page shows:
- Cluster health status
- Cluster ID
- Controller ID
- Broker nodes with:
  - Node ID
  - Host
  - Port
  - Rack information

### Dark Mode

Toggle between light and dark themes:
- Click the sun/moon icon in the top-right corner
- Preference is saved to localStorage

## API Usage

### Authentication

Currently, the API does not require authentication (suitable for internal/development use).

### API Endpoints

#### Topics

```bash
# List all topics
curl http://localhost:8080/api/topics

# Get specific topic
curl http://localhost:8080/api/topics/{topicName}

# Create topic
curl -X POST http://localhost:8080/api/topics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "my-topic",
    "partitions": 3,
    "replicationFactor": 2
  }'

# Delete topic
curl -X DELETE http://localhost:8080/api/topics/{topicName}

# Get under-replicated partitions
curl http://localhost:8080/api/topics/under-replicated
```

#### Consumer Groups

```bash
# List all consumer groups
curl http://localhost:8080/api/consumer-groups

# Get specific consumer group
curl http://localhost:8080/api/consumer-groups/{groupId}
```

#### Cluster

```bash
# Get cluster information
curl http://localhost:8080/api/cluster

# Health check
curl http://localhost:8080/actuator/health
```

### HATEOAS Links

The API uses HATEOAS for discoverability. Responses include `_links` with related resources:

```json
{
  "name": "my-topic",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/topics/my-topic"
    },
    "delete": {
      "href": "http://localhost:8080/api/topics/my-topic"
    }
  }
}
```

## Troubleshooting

### Backend Issues

#### Port 8080 Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change the port in application.properties
server.port=8081
```

#### Cannot Connect to Kafka

```bash
# Check if Kafka containers are running
docker-compose ps

# View Kafka logs
docker-compose logs kafka1

# Restart services
docker-compose restart
```

#### Maven Build Fails

```bash
# Clean Maven cache
mvn clean

# Build without tests
mvn package -DskipTests

# Update dependencies
mvn dependency:resolve
```

### Frontend Issues

#### Port 3000 Already in Use

```bash
# Find and kill process on port 3000
lsof -i :3000
kill -9 <PID>

# Or change port in package.json
vite --port 3001
```

#### API Calls Failing (CORS/Proxy Issues)

The frontend uses Vite's proxy configuration. Ensure:

1. Backend is running on port 8080
2. `vite.config.ts` has correct proxy settings
3. No firewall blocking localhost connections

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': 'http://localhost:8080',
    '/actuator': 'http://localhost:8080'
  }
}
```

#### Dependencies Not Installing

```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### Docker Issues

#### Containers Won't Start

```bash
# Check Docker is running
docker info

# Remove old containers
docker-compose down -v

# Rebuild images
docker-compose up --build
```

#### Disk Space Issues

```bash
# Clean up Docker
docker system prune -a

# Remove volumes
docker volume prune
```

### Testing Issues

#### Tests Failing with Kafka Connection Errors

```bash
# Run unit tests only (no Kafka needed)
mvn test -Dtest="*UnitTest"

# Ensure Docker is running for integration tests
docker info

# Run all tests
mvn test
```

## Development Workflow

### Backend Development

```bash
# 1. Make code changes
# 2. Run unit tests (fast feedback)
mvn test -Dtest="*UnitTest"

# 3. Run all tests including integration tests
mvn test

# 4. Package and restart
mvn clean package
docker-compose restart spring-app
```

### Frontend Development

```bash
# Frontend has hot reload - just save files
# Changes will reflect immediately

# Build for production
npm run build

# Preview production build
npm run preview
```

### Full Stack Development

For the best development experience:

```bash
# Terminal 1: Kafka infrastructure only
docker-compose up kafka1 kafka2 kafka3 kafka4 zookeeper kafdrop

# Terminal 2: Run backend with hot reload
mvn spring-boot:run

# Terminal 3: Run frontend with hot reload
cd frontend && npm run dev
```

This setup provides:
- ✅ Backend hot reload with Spring DevTools
- ✅ Frontend hot reload with Vite HMR
- ✅ No need to rebuild Docker images

## Next Steps

- Explore the [API Documentation](http://localhost:8080/swagger-ui.html)
- Check the [ROADMAP.md](ROADMAP.md) for planned features
- Read [frontend/README.md](frontend/README.md) for frontend details
- Review [README.md](README.md) for testing strategy

## Getting Help

- Check the logs: `docker-compose logs`
- View container status: `docker-compose ps`
- Inspect network: `docker network ls`
- Backend logs: `docker-compose logs spring-app`

## Clean Up

When you're done:

```bash
# Stop all services
docker-compose down

# Remove volumes (clean slate)
docker-compose down -v

# Stop frontend
# Press Ctrl+C in the terminal running npm run dev
```

Enjoy using the Kafka Admin Dashboard! 🚀

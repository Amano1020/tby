# High-Performance Order Management API

A scalable, high-concurrency order management system designed to handle up to 5000 QPS.

## Features
- **Zero Foreign Keys**: Application-level joins and aggregations for maximum write throughput.
- **Snowflake ID Generation**: Distributed, globally unique, high-performance ID assignment.
- **Asynchronous Processing**: Order persistence via Apache RocketMQ for peak-shaving.
- **Idempotency & Distributed Locks**: Industry-standard Redisson integration to prevent duplicate submissions or race conditions.
- **Multi-Level Caching**: Caffeine (L1) + Redis (L2) for lightning-fast product reads.
- **Design Patterns**: Chain of Responsibility implementation for extensible price calculation.
- **Security**: IDOR (Insecure Direct Object Reference) prevention on resource modification endpoints.
- **Unified API Responses**: Clean and consistent `{code, message, data}` payload wrap for all requests.

## Tech Stack
- Java 17, Spring Boot 3
- MySQL 8.0
- Redis 7.0 (Redisson Client)
- Apache RocketMQ 4.9.4
- Docker & Docker Compose

---

## 🚀 How to Run Locally

We have provided a `docker-compose.yml` to spin up the entire infrastructure. It also includes an `init.sql` script that will automatically hydrate the MySQL database with test mock data.

### 1. Start the Infrastructure (Database, Cache, MQ)
Ensure you have Docker installed and running. From the root directory of the project, run:
```bash
docker-compose down
docker-compose up -d
```
*Note: Wait a few seconds for MySQL and RocketMQ to fully initialize before starting the Spring Boot app.*

### 2. Start the Spring Boot Application
In another terminal, compile and run the Spring Boot application using the Maven wrapper:
```bash
./mvnw spring-boot:run
```

### 3. Test the Application
Once the application starts, it connects to port `8080`. You can test it via Postman or cURL. 
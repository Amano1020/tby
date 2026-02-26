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
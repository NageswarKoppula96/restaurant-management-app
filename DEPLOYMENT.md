# Restaurant Management Application - Deployment Guide

This guide explains how to deploy the Restaurant Management Application.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- (Optional) Docker (for containerized deployment)

## Building the Application

1. **Build the application** using Maven:
   ```bash
   mvn clean package -DskipTests
   ```
   This will create an executable JAR file at `target/restaurant-management-app.jar`

## Running the Application

### Development Mode

```bash
java -jar target/restaurant-management-app.jar
```

### Production Mode

```bash
java -jar target/restaurant-management-app.jar --spring.profiles.active=prod
```

### Custom Configuration

You can override any property using command line arguments:

```bash
java -jar target/restaurant-management-app.jar \
    --spring.profiles.active=prod \
    --server.port=8080 \
    --spring.datasource.url=jdbc:derby:directory:./derbydb/restaurantdb \
    --spring.datasource.username=app \
    --spring.datasource.password=your_password
```

## Environment Variables

You can also configure the application using environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SERVER_PORT=8080
export SPRING_DATASOURCE_URL=jdbc:derby:directory:./derbydb/restaurantdb
export SPRING_DATASOURCE_USERNAME=app
export SPRING_DATASOURCE_PASSWORD=your_password

java -jar target/restaurant-management-app.jar
```

## Docker Deployment

1. **Build the Docker image**:
   ```bash
   docker build -t restaurant-management-app .
   ```

2. **Run the container**:
   ```bash
   docker run -d -p 8080:8080 --name restaurant-app restaurant-management-app
   ```

## Database Configuration

By default, the application uses an embedded Derby database. For production, consider using a more robust database like PostgreSQL or MySQL.

### PostgreSQL Example

1. Add PostgreSQL dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. Configure `application-prod.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   ```

## Monitoring and Management

The application includes Spring Boot Actuator endpoints for monitoring:

- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`

## API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Logs

Logs are written to `logs/restaurant-app.log` by default in production mode.

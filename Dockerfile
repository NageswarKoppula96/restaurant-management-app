# Use the official Maven image as the build stage
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as the runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Install required packages for Derby
RUN apt-get update && apt-get install -y procps && rm -rf /var/lib/apt/lists/*

# Create directory for Derby database
RUN mkdir -p /app/derbydb

# Copy the JAR file from the build stage
COPY --from=build /app/target/restaurant-management-app.jar app.jar

# Copy the schema.sql file
COPY src/main/resources/schema.sql /app/schema.sql

# Set environment variables for Derby
ENV DERBY_HOME=/opt/derby
ENV CLASSPATH=$DERBY_HOME/lib/derby.jar:$DERBY_HOME/lib/derbytools.jar:.

# Create a non-root user and set permissions
RUN addgroup --system javauser && adduser --system --group javauser \
    && chown -R javauser:javauser /app \
    && chmod -R 755 /app

USER javauser

# Set working directory to the app directory
WORKDIR /app

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Djava.security.egd=file:/dev/./urandom"

# Expose the port the app runs on
EXPOSE 8080

# Run the application with Derby system home and port configuration
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dderby.system.home=/app/derbydb -jar app.jar --server.port=8080"]

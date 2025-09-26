# Use the official Maven image as the build stage
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as the runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/restaurant-management-app.jar app.jar

# Create a non-root user and switch to it
RUN addgroup --system javauser && adduser --system --group javauser
RUN chown -R javauser:javauser /app
USER javauser

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Djava.security.egd=file:/dev/./urandom"

# Expose the port the app runs on
EXPOSE $PORT

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=$PORT"]

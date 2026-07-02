# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/debugmate-ai-0.0.1-SNAPSHOT.jar app.jar

# Environment variables with sensible defaults
ENV DB_USERNAME=root
ENV DB_PASSWORD=root
ENV GEMINI_API_KEY=

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

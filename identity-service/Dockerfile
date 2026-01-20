# ---------- BUILD STAGE ----------
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copy only what Gradle needs first (better caching)
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy service sources
COPY identity-service identity-service

# Build only this module
RUN ./gradlew :identity-service:bootJar --no-daemon

# ---------- RUN STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/identity-service/build/libs/*.jar app.jar

# App port
EXPOSE 8082

# Optional: Java runtime tuning (safe defaults)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

# Spring reads env vars like MONGODB_URI, JWT_SECRET, INTERNAL_SHARED_SECRET
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
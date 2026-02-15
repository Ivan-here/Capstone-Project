# ---------- BUILD STAGE ----------
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copy everything from THIS service folder (build context)
COPY . .

# Ensure gradlew is executable (common on Windows)
RUN chmod +x ./gradlew

# Build the boot jar for this service
RUN ./gradlew clean bootJar --no-daemon -x test


# ---------- RUN STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Gateway port
EXPOSE 9000

# Optional: safe JVM tuning
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

# Activate docker profile by default (so it uses application-docker.properties)
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
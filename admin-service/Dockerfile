FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copy everything first
COPY . .

# Fix line endings and make gradlew executable
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew

# Build jar
RUN ./gradlew clean bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","app.jar"]
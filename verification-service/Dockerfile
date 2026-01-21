# 1. Start with a Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the built JAR file into the container
# Note: We assume you run 'gradlew bootJar' before building the docker image
COPY build/libs/*.jar app.jar

# 4. Expose the port
EXPOSE 8083

# 5. Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
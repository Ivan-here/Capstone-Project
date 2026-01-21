# 1. Start with a base image containing Java 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the built JAR file into the container
# This assumes the JAR is named 'profile-service-0.0.1-SNAPSHOT.jar'
# The wildcard *.jar handles slight version name changes automatically
COPY build/libs/*.jar app.jar

# 4. Expose the port the app runs on
EXPOSE 8081

# 5. Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
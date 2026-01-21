# ---------- BUILD STAGE ----------
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY profile-service profile-service

RUN ./gradlew :profile-service:bootJar --no-daemon

# ---------- RUN STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/profile-service/build/libs/*.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

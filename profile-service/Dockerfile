FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew :profile-service:bootJar --no-daemon -x test
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build --chown=app:app /app/profile-service/build/libs/app.jar app.jar
USER app
EXPOSE 8081
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

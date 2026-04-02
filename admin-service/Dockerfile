FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","app.jar"]

# Stage 1: Build
FROM maven:3.9.8-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Stage 2: Create Image
FROM amazoncorretto:21.0.4

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Stage 1: Build
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk
WORKDIR /
COPY --from=build /app/target/*.jar stackOverflow.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "stackOverflow.jar"]
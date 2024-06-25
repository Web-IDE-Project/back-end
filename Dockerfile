## spring-boot-app/Dockerfile
## Start with a base image containing Java runtime
#FROM openjdk:17-jdk-alpine
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the built jar file from the local file system to the container
#COPY build/libs/*.jar app.jar
#
## Expose the port the application runs on
#EXPOSE 8080
#
## Command to run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]


# Stage 1: Build the Spring Boot application
FROM gradle:7.3.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Stage 2: Package the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the build artifacts from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Copy the docker-build.sh script and set permissions
COPY ./docker-build.sh .
RUN chmod +x docker-build.sh


# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

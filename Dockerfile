# Step 1: Spring Boot 애플리케이션 빌드
#FROM openjdk:17-jdk-slim AS builder
#WORKDIR /app
#COPY . .
#RUN ./gradlew build -x test
#
## Step 2: Nginx 설정과 Spring Boot 애플리케이션 배포
#FROM nginx:alpine
#COPY --from=builder /app/build/libs/*.jar /app/app.jar
#
## Nginx 설정 파일을 컨테이너에 복사
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#
## Spring Boot 애플리케이션과 Nginx를 동시에 실행
#CMD ["sh", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]

## Step 1: Spring Boot 애플리케이션 빌드
#FROM openjdk:17-jdk-slim AS builder
#WORKDIR /app
#COPY . .
#RUN ./gradlew build -x test
#
## Step 2: Nginx 설정과 Spring Boot 애플리케이션 배포
#FROM nginx:alpine
#
## Nginx 설정 파일을 컨테이너에 복사
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#
## Spring Boot 애플리케이션 JAR 파일을 컨테이너에 복사
#COPY --from=builder /app/build/libs/*.jar /app/app.jar
#
## Spring Boot 애플리케이션과 Nginx를 동시에 실행
#CMD ["sh", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]

# Step 1: Spring Boot 애플리케이션 빌드
#FROM openjdk:17-jdk-slim AS builder
#WORKDIR /app
#COPY . .
#RUN ./gradlew build -x test
#
## Step 2: Spring Boot 애플리케이션 배포
#FROM openjdk:17-jdk-slim
#COPY --from=builder /app/build/libs/*.jar /app/app.jar
#CMD ["sh", "-c", "java -jar /app/app.jar"]
#
#
## Expose port
#EXPOSE 8080
#EXPOSE 80
#
## Step 3: Nginx 설정
#FROM nginx:alpine
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#CMD ["nginx", "-g", "daemon off;"]


## Step 1: Spring Boot 애플리케이션 빌드
#FROM openjdk:17-jdk-slim AS builder
#WORKDIR /app
#COPY . .
#RUN ./gradlew build -x test
#
## Step 2: Nginx 설정과 Spring Boot 애플리케이션 배포
#FROM nginx:alpine
#COPY --from=builder /app/build/libs/*.jar /app/app.jar
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#
## Expose port
#EXPOSE 8080
#EXPOSE 80
#
## Spring Boot 애플리케이션과 Nginx를 동시에 실행
#CMD ["sh", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]

## Step 1: Spring Boot 애플리케이션 빌드
#FROM openjdk:17-jdk-slim AS builder
#WORKDIR /app
#COPY . .
#RUN ./gradlew build -x test
#
## Step 2: Spring Boot 애플리케이션 배포
#FROM openjdk:17-jdk-slim
#WORKDIR /app
#COPY --from=builder /app/build/libs/*.jar /app/app.jar
#
#EXPOSE 8080
#EXPOSE 80
#
## Spring Boot 애플리케이션 실행
#CMD ["java", "-jar", "/app/app.jar"]

# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]


# Step 1: Spring Boot 애플리케이션 빌드
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Step 2: Nginx 설정과 Spring Boot 애플리케이션 배포
FROM nginx:alpine
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Nginx 설정 파일을 컨테이너에 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Spring Boot 애플리케이션과 Nginx를 동시에 실행
CMD ["sh", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]

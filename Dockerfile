# Step 1: Spring Boot 애플리케이션 빌드
# OpenJDK 17 슬림 이미지를 사용하여 빌드 단계 설정
FROM openjdk:17-jdk-slim AS builder
# 작업 디렉토리를 /app으로 설정
WORKDIR /app
# 현재 디렉토리의 모든 파일을 컨테이너의 /app 디렉토리로 복사
COPY . .
# Gradle을 사용하여 빌드, 테스트 단계는 생략
RUN ./gradlew build -x test

# Step 2: Nginx 설정과 Spring Boot 애플리케이션 배포
# Nginx Alpine 이미지를 사용하여 배포 단계 설정
FROM nginx:alpine
# 빌드 단계에서 생성된 JAR 파일을 Nginx 컨테이너로 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Nginx가 백엔드 애플리케이션을 프록시하도록 설정
RUN echo 'server {
    # Nginx가 80번 포트에서 HTTP 요청을 수신
    listen 80;
    # 서버 이름 설정
    server_name localhost;

    # /api/ 경로로 들어오는 요청을 프록시 설정
    location /api/ {
        # 로컬 호스트의 8080 포트로 프록시
        proxy_pass http://127.0.0.1:8080;
        # 원본 요청의 Host 헤더 설정
        proxy_set_header Host $host;
        # 원본 요청의 클라이언트 IP 설정
        proxy_set_header X-Real-IP $remote_addr;
        # 원본 요청의 X-Forwarded-For 헤더 설정
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        # 원본 요청의 프로토콜 설정
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}' > /etc/nginx/conf.d/default.conf

# Spring Boot 애플리케이션과 Nginx를 동시에 실행
CMD ["sh", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]

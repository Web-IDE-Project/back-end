#!/bin/bash

echo "Building Docker images for compilers..."

# 자바 컴파일 컨테이너 빌드
docker build -f src/main/resources/docker/Dockerfile-java -t java-compiler .

# C 컴파일 컨테이너 빌드
docker build -f src/main/resources/docker/Dockerfile-c -t c-compiler .

# CPP 컴파일 컨테이너 빌드
docker build -f src/main/resources/docker/Dockerfile-cpp -t cpp-compiler .

# 파이썬 컴파일 컨테이너 빌드
docker build -f src/main/resources/docker/Dockerfile-python -t python-compiler .

# 자바스크립트 컴파일 컨테이너 빌드
docker build -f src/main/resources/docker/Dockerfile-javascript -t js-compiler .

echo "Docker images built successfully."

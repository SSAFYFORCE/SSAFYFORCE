# Dockerfile (backend 디렉토리 안에 놓고, context: ./backend)
FROM openjdk:17-slim
WORKDIR /app

# 배포 환경에서 한국 시간대로 작동하도록 설정
ENV TZ=Asia/Seoul

# GitHub Actions가 빌드해둔 jar 파일을 복사
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

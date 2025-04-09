# Base image 설정 (JDK 17)
FROM openjdk:17-jdk-slim

# 작업 디렉터리 설정
WORKDIR /app

# 프로젝트에서 생성한 jar 파일을 컨테이너 내부로 복사
COPY build/libs/BANnerIt-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# jar 파일 실행
CMD ["java", "-jar", "/app/app.jar"]

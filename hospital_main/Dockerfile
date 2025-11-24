# 빌드 스테이지
FROM maven:3.9-amazoncorretto-21 AS builder

WORKDIR /app

# pom.xml 먼저 복사 (의존성 캐싱)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 소스 코드 복사
COPY src ./src

# db.properties 파일 수정 (파일이 존재하는 경우만)
RUN if [ -f "src/main/resources/db.properties" ]; then \
        echo "=== db.properties 수정 전 ===" && \
        cat src/main/resources/db.properties && \
        sed -i 's/localhost:3500/mariadb:3500/g' src/main/resources/db.properties && \
        echo "=== db.properties 수정 후 ===" && \
        cat src/main/resources/db.properties; \
    else \
        echo "db.properties 파일이 없습니다."; \
    fi

# Maven 빌드
RUN echo "=== Maven 빌드 시작 ===" && \
    mvn clean package -DskipTests -B

# 빌드 결과 확인
RUN echo "=== 빌드 결과 ===" && \
    ls -la target/ && \
    find target/ -name "*.war" -type f

# 실행 스테이지
FROM tomcat:10.1-jdk21-openjdk-slim

# 🚀 [추가] Healthcheck에 필요한 'curl' 설치 (Debian 기반)
# --no-install-recommends를 사용하여 이미지 크기를 최소화하고 불필요한 파일 정리
RUN apt-get update && \
    apt-get install -y curl --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# 기본 webapps 정리
RUN rm -rf /usr/local/tomcat/webapps/*

# WAR 파일 복사 (정확한 파일명 사용)
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Tomcat 포트를 8888로 변경
RUN sed -i 's/port="8080"/port="8888"/g' /usr/local/tomcat/conf/server.xml

EXPOSE 8888

# 🚀 [추가] 컨테이너 Healthcheck 정의
# Spring Boot가 완전히 시작될 때까지 60초의 초기 대기 시간(--start-period)을 줍니다.
# 30초마다 검사하고, 타임아웃은 10초로 설정합니다. 총 5회 실패 시 unhealthy로 간주합니다.
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:8888/actuator/health || exit 1

CMD ["catalina.sh", "run"]


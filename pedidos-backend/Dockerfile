FROM gradle:7.6-jdk17-alpine AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties* ./

RUN gradle dependencies --no-daemon

COPY src ./src

RUN gradle bootJar --no-daemon

FROM openjdk:17-jre-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown spring:spring app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="k8s"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/pedidos/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
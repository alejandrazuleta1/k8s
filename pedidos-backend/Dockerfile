FROM gradle:8.8-jdk17-alpine AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENV ENVIROMENT=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
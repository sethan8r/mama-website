FROM openjdk:21-ea-21-jdk-slim
WORKDIR /app
COPY build/libs/mama-0.0.1-mama-website.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
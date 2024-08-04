FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

COPY src ./src

RUN ./gradlew build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app.jar"]
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /build

COPY build/libs ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
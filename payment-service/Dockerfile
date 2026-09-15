# Build context must be the booking-platform/ repo root (needs ../proto),
# e.g.: docker build -f payment-service/Dockerfile -t payment-service .
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY proto /proto
COPY payment-service/pom.xml ./
COPY payment-service/src ./src
RUN mvn -q -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /src/target/payment-service-0.1.0.jar /app/app.jar
EXPOSE 18081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY proto ./proto
WORKDIR /build/order-service
COPY order-service/pom.xml ./
COPY order-service/src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/order-service/target/order-service-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

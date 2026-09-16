# Build context must be the observability-platform/ repo root (needs ../proto),
# e.g.: docker build -f config-api/Dockerfile -t config-api .
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY proto /proto
COPY config-api/pom.xml ./
COPY config-api/src ./src
RUN mvn -q -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /src/target/config-api.jar /app/app.jar
EXPOSE 28080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

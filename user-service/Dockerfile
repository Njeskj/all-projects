# Build context must be the matching-platform/ root (needs ../proto relative to user-service/pom.xml).
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY proto ./proto
COPY user-service ./user-service
WORKDIR /src/user-service
RUN mvn -q -o -DskipTests package || mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /src/user-service/target/user-service.jar /user-service.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/user-service.jar"]

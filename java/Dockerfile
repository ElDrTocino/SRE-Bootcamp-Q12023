# Build stage

FROM maven:3-openjdk-11 as build
COPY auth_api/src /app/src
COPY auth_api/pom.xml /app
RUN mvn -f /app/pom.xml clean package


# Package stage
FROM azul/zulu-openjdk-alpine:11
COPY --from=build /app/target/auth-api-1.0-SNAPSHOT-jar-with-dependencies.jar /app/auth-api-1.0-SNAPSHOT-jar-with-dependencies.jar
EXPOSE 8000
ENTRYPOINT ["java","-cp", "/app/auth-api-1.0-SNAPSHOT-jar-with-dependencies.jar","com.wizeline.App"]

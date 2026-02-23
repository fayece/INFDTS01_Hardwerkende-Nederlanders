# Build
FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /app
COPY hr-cms/pom.xml .
COPY hr-cms/src ./src
RUN mvn clean package -DskipTests

# Create image
FROM eclipse-temurin:25
WORKDIR /app
COPY --from=build /app/target/hr-cms-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 6031
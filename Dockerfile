FROM eclipse-temurin
WORKDIR /app
COPY hr-cms/target/hr-cms-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM eclipse-temurin:21-jdk
EXPOSE 8080
ADD target/book_cicd.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM openjdk:8
EXPOSE 8080
ADD target/book_cicd.jar book_cicd.jar
ENTRYPOINT ["java","-jar","/book_cicd.jar"]











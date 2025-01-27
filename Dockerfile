FROM eclipse-temurin:21-jdk-alpine
COPY /target/*.jar /opt/app/repasse.jar
LABEL maintainer="adevalter@gmail.com"
ENTRYPOINT ["java","-jar","/opt/app/repasse.jar"]
EXPOSE 9595
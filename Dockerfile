FROM maven:3.9.5-amazoncorretto-17/git
EXPOSE 8080
RUN git clone https://github.com/IvanMTD/passcode.git
RUN mvn clean package
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

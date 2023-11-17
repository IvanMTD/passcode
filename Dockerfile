FROM amazoncorretto:17.0.6
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
COPY ./resources ./resources
RUN ./mvnw clean install
ENTRYPOINT ["java","-jar","/app.jar"]
FROM amazoncorretto:17.0.6
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvn dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install
ENTRYPOINT ["java","-jar","/app.jar"]
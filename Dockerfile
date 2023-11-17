FROM amazoncorretto:17.0.6
COPY . .
RUN ./mvnw dependency:go-offline
RUN ./mvnw clean install
ENTRYPOINT ["java","-jar","/app.jar"]
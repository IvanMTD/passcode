FROM alpine/git as clone 
WORKDIR /app
RUN git clone https://github.com/IvanMTD/passcode.git

FROM maven
WORKDIR /app
COPY --from=clone /app/spring-petclinic /app 
RUN mvn clean package -DskipTests

FROM openjdk
WORKDIR /app
COPY --from=build /app/target/passcode-1.0.1-SNAPSHOT.jar /app
CMD ["java -jar passcode-1.0.1-SNAPSHOT.jar"]

FROM alpine/git as clone 
WORKDIR /app
RUN git clone https://github.com/IvanMTD/passcode.git

FROM maven:openjdk as build
WORKDIR /app
COPY --from=clone /app/passcode /app
RUN mvn clean package -DskipTests

FROM openjdk
EXPOSE 8080
EXPOSE 443
EXPOSE 5432
WORKDIR /app
COPY --from=build /app/target/passcode-1.0.1-SNAPSHOT.jar /app
CMD ["java -jar passcode-1.0.1-SNAPSHOT.jar"]

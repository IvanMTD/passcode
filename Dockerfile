FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/IvanMTD/passcode.git

FROM maven:3.9.5-amazoncorretto-17
WORKDIR /app
COPY --from=0 /app/passcode /app
RUN mvn install

FROM amazoncorretto:17.0.6
WORKDIR /app
COPY --from=1 /app/target/passcode-1.0.1-SNAPSHOT.jar /app
CMD ["java -jar passcode-1.0.1-SNAPSHOT.jar"]
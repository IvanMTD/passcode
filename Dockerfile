FROM alpine/git as clone 
WORKDIR /app
RUN git clone https://github.com/IvanMTD/passcode.git

FROM zhana0/maven-3.9.2-openjdk-17 as build
WORKDIR /app
COPY --from=clone /app/passcode /app
RUN mvn clean package -DskipTests

FROM 77tv/openjdk-17-jre
EXPOSE 8080
EXPOSE 443
EXPOSE 5432
WORKDIR /app
COPY --from=build /app/target/passcode-1.0.1-SNAPSHOT.jar /app
CMD ["java -jar passcode-1.0.1-SNAPSHOT.jar"]

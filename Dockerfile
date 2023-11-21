FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
EXPOSE 443
EXPOSE 5432
COPY passcode.jar passcode.jar
ENTRYPOINT ["java","-jar","/passcode.jar"]
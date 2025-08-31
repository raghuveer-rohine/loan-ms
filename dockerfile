FROM --platform=linux/amd64 amazoncorretto:17-alpine
VOLUME /tmp 
COPY target/Loan-Service-0.0.1-SNAPSHOT.jar loan_services.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/loan_services.jar"]

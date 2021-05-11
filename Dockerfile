FROM openjdk:14
VOLUME /tmp
ADD build/libs/issue-service-0.0.1-SNAPSHOT.jar issue-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","issue-service.jar"]

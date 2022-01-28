FROM maven:3.8.2-openjdk-11-slim as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests=True

FROM openjdk:11.0-jdk-slim-buster
COPY --from=build /home/app/target/DBServer-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV PG_HOST=kube-toolchain-demo-11-12-20211112141704554
ENV POSTGRES_PASSWORD=mysecretpassword
ENV POSTGRES_USER=santa
ENV POSTGRES_DB=santa_db
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
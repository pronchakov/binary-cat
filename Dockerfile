FROM maven:3.6.3-openjdk-15 as build
WORKDIR /root/binary-cat/build
COPY src src
COPY pom.xml pom.xml
RUN mvn clean package

FROM openjdk:15-jdk as run
WORKDIR /root/binary-cat/distr
COPY --from=build /root/binary-cat/build/target/dependency ./dependency
COPY --from=build /root/binary-cat/build/target/binary-cat.jar .
COPY deploy ./deploy
EXPOSE 8080
ENTRYPOINT java -jar binary-cat.jar
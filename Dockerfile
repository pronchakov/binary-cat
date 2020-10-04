FROM openjdk:15-jdk as run
WORKDIR /root/binary-cat
COPY target/dependency ./dependency
COPY target/binary-cat.jar .
COPY deploy ./deploy
EXPOSE 8080
ENTRYPOINT java -XshowSettings:vm -jar binary-cat.jar
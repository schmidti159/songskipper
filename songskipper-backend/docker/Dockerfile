FROM openjdk:17-alpine
ARG BUILD_VERSION
ADD songskipper-backend-${BUILD_VERSION}.jar /usr/songskipper/songskipper-backend.jar
WORKDIR /usr/songskipper
ENTRYPOINT exec java $JAVA_OPTS -jar songskipper-backend.jar
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n
RUN mkdir -p ./target/cache/
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
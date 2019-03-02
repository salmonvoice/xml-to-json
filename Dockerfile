FROM openjdk:8-jdk-alpine

EXPOSE 80

VOLUME /tmp
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-Dserver.port=80", "-cp","app:app/lib/*","software.engineering.task.Application"]
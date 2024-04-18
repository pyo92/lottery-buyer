FROM openjdk:17
ARG JAR_FILE=build/libs/lottery-purchaser.jar
COPY ${JAR_FILE} ./lottery-purchaser.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./lottery-purchaser.jar"]

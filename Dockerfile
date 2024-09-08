FROM maven:3-amazoncorretto-17 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM amazoncorretto:17-alpine

COPY --from=build build/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
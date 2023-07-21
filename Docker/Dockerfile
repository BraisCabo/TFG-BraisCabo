# Stage 1 - Build Angular app
FROM node:18-alpine AS builder

WORKDIR /project

COPY Frontend/ .

RUN npm install
RUN npm run build -- --configuration=production


# Stage 2 - Build Spring Boot app
FROM jelastic/maven:3.8.6-openjdk-20.ea-b24 as backend

# Create a new directory in the Docker image to hold the Maven settings.xml file
RUN mkdir /root/.m2

# Copy your local settings.xml to the Docker image
COPY Docker/settings.xml /root/.m2/

WORKDIR /project

COPY Backend/pom.xml /project/
COPY Backend/src /project/src

# Copy Angular build files to Spring Boot static resources
COPY --from=builder /project/dist/frontend /project/src/main/resources/static

RUN mvn package -DskipTests=true

# Stage 3 - Build Docker image
FROM openjdk:20

ENV JAVA_TOOL_OPTIONS="-Xss256K -XX:ReservedCodeCacheSize=64M -XX:MaxMetaspaceSize=100000K -Xmx64M"

WORKDIR /usr/src/app/

COPY --from=backend /project/target/*.jar /usr/src/app/

EXPOSE 8443

CMD ["java", "-jar", "Backend-0.0.1-SNAPSHOT.jar"]
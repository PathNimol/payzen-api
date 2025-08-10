# Stage 1: Build with Maven + JDK 21
FROM eclipse-temurin:21 AS build

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run with JRE 21
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

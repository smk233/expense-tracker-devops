# Stage 1 : Build the JAR using maven
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTest=true

# Stage 2 : Execute the JAR file from the above stage 
FROM eclipse-temurin:17-jre 

COPY --from=builder /app/target/*.jar /app/expense-tracker.jar

CMD ["java","-jar","expense-tracker.jar"]

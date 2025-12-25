# Stage 1: Build the application
# Use an official Maven image with JDK 11 as the base image
FROM maven:3.8.4-openjdk-11-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the application using Maven
# The -DskipTests flag is common for a production build process
WORKDIR /app/components/apimgt/org.wso2.carbon.apimgt.gateway
RUN mvn clean package -DskipTests
#
## Stage 2: Create a minimal runtime image
## Use a slim JRE 11 image for the final, lean runtime environment
#FROM openjdk:11-jre-slim
#
## Set the working directory for the runtime
#WORKDIR /app
#
## Copy the built JAR file from the 'build' stage to the current stage
#COPY --from=build /app/target/*.jar app.jar
#
## Expose any necessary ports (e.g., for a web application)
#EXPOSE 8080
#
## Define the command to run the application when the container starts
#ENTRYPOINT ["java", "-jar", "app.jar"]

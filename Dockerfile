# Stage 1: Build the Maven project and copy dependencies
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project files to the container
COPY pom.xml .
COPY src ./src

# Build the Maven project and copy dependencies
RUN mvn clean package dependency:copy-dependencies

# Stage 2: Create the final image
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Create the reports directory
RUN mkdir -p src/main/reports

# Copy the built JAR file and dependencies from the build stage
COPY --from=build /app/target/SHACL-Validator.jar .
COPY --from=build /app/target/dependency/*.jar ./lib/

# Copy the data and shape directories from the source code directory
COPY src/main/data /app/src/main/data
COPY src/main/shape /app/src/main/shape

# Specify the command to run your Java application
CMD ["java", "-cp", "SHACL-Validator.jar:./lib/*", "dem.shacl.ShaclDemo"]


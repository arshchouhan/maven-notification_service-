# Stage 1: Build with Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /workspace

# Allow override of MAVEN_OPTS at build-time
ARG MAVEN_OPTS="-Xmx1024m"
ENV MAVEN_OPTS=${MAVEN_OPTS}

# Copy pom.xml first to leverage layer caching
COPY pom.xml ./

# Fetch dependencies (cached layer)
RUN mvn -B -ntp dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre

# Allow selecting the produced JAR (default picks the first jar in target)
ARG JAR_FILE=target/*.jar

# JVM tuning - override at runtime with `JAVA_OPTS`
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+ExitOnOutOfMemoryError"

WORKDIR /app

# Copy the application jar from the build stage
COPY --from=build /workspace/target/*.jar /app/app.jar

# Create non-root app user and set permissions (run as root here)
RUN groupadd -r app && useradd -r -g app app && chown app:app /app/app.jar

USER app

# Expose the port your app listens on (adjust if different)
EXPOSE 8080

# Use shell form so `JAVA_OPTS` environment variable is expanded
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]


# 1단계: 빌드
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew gradle.* ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test --no-daemon


# 2단계: 런타임
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SERVER_PORT=8128 \
    JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -jar app.jar"]

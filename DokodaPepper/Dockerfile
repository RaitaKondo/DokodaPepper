# ステージ1：ビルド
FROM gradle:8.5.0-jdk21 AS builder
COPY . /home/gradle/project/
WORKDIR /home/gradle/project
RUN gradle bootJar --no-daemon

# ステージ2：実行
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

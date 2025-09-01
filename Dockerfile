# ===== build =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# cache de dependências
COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# código
COPY src ./src

# gera o fat-jar (ou bootJar) sem testes
RUN ./gradlew --no-daemon clean bootJar -x test

# ===== run =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*SNAPSHOT*.jar app.jar
# Porta vem do Render
ENV JAVA_OPTS="-Dserver.port=${PORT:-8080}"
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

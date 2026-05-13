# Etapa 1: compilar
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de dependencias primero (mejor cache de Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: imagen final liviana
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Puerto según el MS (cambiar en cada uno)
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
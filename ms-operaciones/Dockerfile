# Multi-stage build para optimizar el tamaño de la imagen
FROM maven:3.9.4-openjdk-17 AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Stage final con runtime optimizado
FROM openjdk:17-jre-slim

WORKDIR /app

# Crear usuario no root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Instalar dependencias necesarias para la aplicación
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar el JAR desde el stage de build
COPY --from=build /app/target/ms-operaciones-*.jar app.jar

# Cambiar permisos
RUN chown spring:spring /app/app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Cambiar a usuario no root
USER spring

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

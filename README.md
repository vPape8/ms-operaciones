# ms-reporte

Sistema de microservicios para gestión de operaciones portuarias, cálculos de tarifas y generación de boletas/reports.

## 🏗️ Arquitectura de Microservicios

**Microservicios Implementados:**
- **ms-user**: Gestión de usuarios y autenticación JWT
- **ms-puerto**: Gestión de puertos y tarifas
- **ms-buque**: Gestión de buques y tipos
- **ms-operaciones**: Cálculos portuarios y gestión de boletas
- **ms-reporte**: Generación de reportes PDF y envío de emails

**Frontend:**
- **Frontend**: Aplicación React en S3 (AWS)

**Infraestructura:**
- **BFF**: Backend for Frontend
- **Message Queue**: Sistema de colas para comunicación asíncrona
- **Base de Datos**: PostgreSQL en EC2

**Flujo de Comunicación:**
```
Frontend → BFF → ms-operaciones → ms-puerto (obtener tarifas)
ms-operaciones → ms-buque (obtener datos del buque)
ms-operaciones → ms-user (validar funcionario)
ms-reporte → Message Queue → Email Service
```

## 🚀 Características del Sistema

- **Cálculos Portuarios**: Motor de cálculo de tarifas por tipo de buque
- **Gestión de Boletas**: CRUD completo con validaciones
- **Generación de Reportes**: PDF individuales y reportes grupales
- **Envío de Emails**: Sistema de notificaciones automáticas
- **APIs REST**: Endpoints completos con HATEOAS
- **Autenticación**: JWT con gestión de roles
- **Docker**: Contenerización completa para despliegue

## 📋 Tipos de Buques Soportados

- **GENERAL**: Carga General
- **PESQUERO**: Embarcaciones pesqueras
- **MILITAR**: Buques militares
- **INVESTIGACIÓN**: Buques de investigación científica
- **CRUCERO**: Cruceros y buques de pasajeros

## 🛠️ Stack Tecnológico

- **Java 17**
- **Spring Boot 4.0.6**
- **Spring Data JPA**
- **PostgreSQL**
- **Spring Cloud OpenFeign**
- **Spring HATEOAS**
- **Lombok**
- **iText (PDF)**
- **Apache POI (Excel)**
- **Spring Mail**

## 📁 Estructura del Proyecto

```
ms-reporte/
├── ms-operaciones/          # Cálculos portuarios y boletas
│   ├── src/main/java/com/cordytech/ms_operaciones/
│   │   ├── client/         # Clientes Feign para APIs externas
│   │   ├── config/         # Configuración (CORS, JPA, Feign)
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # Entidad Boleta (solo local)
│   │   ├── operaciones/     # Lógica de cálculos
│   │   ├── repository/      # Repositorio JPA (solo Boleta)
│   │   └── service/        # Servicios de negocio
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── README.md
├── ms-user/               # Gestión de usuarios (existente)
├── ms-puerto/             # Gestión de puertos (por crear)
├── ms-buque/              # Gestión de buques (por crear)
└── ms-reporte/            # Reportes y emails (por crear)
```

## 🚀 Ejecución Rápida

### Desarrollo Local

```bash
# Iniciar ms-operaciones
cd ms-operaciones
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Iniciar otros microservicios (cuando existan)
cd ms-puerto && mvn spring-boot:run -Dspring-boot.run.profiles=dev
cd ms-buque && mvn spring-boot:run -Dspring-boot.run.profiles=dev
cd ms-user && mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Compose (Recomendado)

```bash
cd ms-operaciones
docker-compose up -d
```

## 📡 API Endpoints

### ms-operaciones
- `GET /api/boletas` - Listar todas las boletas
- `GET /api/boletas/{id}` - Obtener boleta por ID
- `POST /api/boletas/calcular` - Calcular y guardar boleta
- `POST /api/boletas/simular` - Simular cálculo sin guardar
- `DELETE /api/boletas/{id}` - Eliminar boleta

### ms-reporte (planificado)
- `POST /api/reportes/pdf` - Generar reporte PDF
- `POST /api/reportes/excel` - Generar reporte Excel
- `POST /api/reportes/email` - Enviar reporte por email

## 📊 Variables de Entorno

| Servicio | Variable | Default |
|----------|---------|----------|
| ms-operaciones | `SERVER_PORT` | `8080` |
| ms-operaciones | `MS_PUERTO_URL` | `http://localhost:8081` |
| ms-operaciones | `MS_BUQUE_URL` | `http://localhost:8082` |
| ms-operaciones | `MS_USER_URL` | `http://localhost:8083` |
| ms-reporte | `MQ_URL` | `http://localhost:5672` |
| ms-reporte | `LD_SERVICE_URL` | `http://localhost:9090` |

## 🧪 Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con cobertura
mvn clean test jacoco:report
```

## 📄 Licencia

Este proyecto está bajo licencia MIT License.

## 📞 Soporte

Para soporte técnico:
- Email: soporte@cordytech.com
- Issues: [GitHub Issues](https://github.com/cordytech/ms-reporte/issues)

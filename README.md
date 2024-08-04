# Product API

Esta API permite administrar los productos de un comercio.

## Requisitos

- Java 17
- Gradle
- Docker y Docker Compose (para ejecutar la aplicación con monitoreo)

## Configuración y Ejecución

1. Clonar el repositorio: git clone https://github.com/Sxel/bricks.git
2. Compilar el proyecto: ./gradlew build
3. Ejecutar la aplicación con Docker Compose: docker-compose up -d

Esto iniciará la aplicación Spring Boot, Prometheus y Grafana.

4. La API estará disponible en `http://localhost:8080`.

## Endpoints

- GET /product: Obtiene el listado de productos (con filtros y paginación)
- GET /product/{id}: Obtiene un producto específico
- POST /product: Crea un nuevo producto
- PUT /product/{id}: Actualiza un producto existente
- DELETE /product/{id}: Elimina un producto
- GET /category: Obtiene el listado de categorías

## Monitoreo con Grafana

Grafana está configurado para visualizar las métricas recopiladas por Prometheus de nuestra aplicación Spring Boot.

1. Acceder a Grafana:
   Abra un navegador y vaya a `http://localhost:3000`.

2. Iniciar sesión:
- Usuario: admin
- Contraseña: admin
  (Puede cambiar la contraseña después del primer inicio de sesión)

3. Configurar un dashboard:
- En el menú lateral, haga clic en "+ Create" y luego en "Dashboard".
- Haga clic en "Add new panel".
- En la sección "Query", seleccione "Prometheus" como fuente de datos.
- Puede comenzar a explorar métricas escribiendo consultas PromQL, por ejemplo:
    - `http_server_requests_seconds_count`: para ver el conteo de solicitudes HTTP
    - `category_requests_total`: para ver el total de solicitudes al servicio de categorías
    - `category_requests_limited_total`: para ver cuántas veces se ha alcanzado el límite de solicitudes

## Notas adicionales

- La aplicación utiliza una base de datos en memoria H2 para almacenamiento.
- Se implementa un límite de 10 solicitudes diarias al servicio externo de categorías.
- Se utiliza un circuit breaker para manejar fallos en el servicio de categorías.

## Pruebas

Para ejecutar las pruebas unitarias: ./gradlew test



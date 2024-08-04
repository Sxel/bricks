# Usa una imagen base de Java 17
FROM eclipse-temurin:17-jdk-focal

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copia los archivos de construcción
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copia el código fuente
COPY src ./src

# Construye la aplicación
RUN ./gradlew build -x test

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/app.jar"]
# Imagen base con JDK
FROM openjdk:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR generado por Gradle
COPY applications/app-service/build/libs/*.jar app.jar

# Exponer el puerto de la app
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java","-jar","app.jar"]
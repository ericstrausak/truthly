FROM node:20-slim AS frontend-build
WORKDIR /app
COPY frontend /app/
RUN npm install
RUN npm run build

FROM eclipse-temurin:21-jdk-slim AS backend-build
WORKDIR /app
COPY . .
COPY --from=frontend-build /app/dist /app/src/main/resources/static
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jdk-slim
WORKDIR /app
COPY --from=backend-build /app/target/Truthly-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
# Stage 1: Build Angular app
FROM node:18-alpine AS frontend-build

WORKDIR /app

# Copy only dependencies first to install
COPY frontend/package*.json ./

# Install đúng trên Linux
RUN npm install

# Copy source code
COPY frontend/ .

# Build Angular app
RUN npm run build --configuration=production

# Kiểm tra output (debug)
RUN ls -la /app/dist/frontend/browser

# Stage 2: Build Spring Boot
FROM maven:3.9.6-eclipse-temurin-17-alpine AS backend-build
WORKDIR /app
COPY backend/pom.xml /app/pom.xml
COPY backend/src /app/src
COPY backend/.mvn /app/.mvn
COPY backend/mvnw /app/mvnw
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 3: Final image (Nginx + Java)
FROM openjdk:17-jdk-alpine
RUN apk add --no-cache nginx

# Copy backend JAR
COPY --from=backend-build /app/target/backend-0.0.1-SNAPSHOT.jar /app/backend/app.jar

# Copy frontend build (Angular output)
COPY --from=frontend-build /app/dist/frontend/browser /usr/share/nginx/html

# Set permissions for Nginx
RUN chmod -R 755 /usr/share/nginx/html && \
    chown -R nginx:nginx /usr/share/nginx/html

# Copy nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy start script
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 80 8081
CMD ["/start.sh"]

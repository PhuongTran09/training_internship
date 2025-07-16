# ===========================
# Build custom provider JAR
# ===========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# ===========================
# Run Keycloak server
# ===========================
FROM quay.io/keycloak/keycloak:22.0.0

# Copy provider vào thư mục providers của Keycloak
COPY --from=build /build/target/keycloak-provider-0.0.1-SNAPSHOT.jar /opt/keycloak/providers/

# Bật custom provider SPI
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

# Có thể thêm SPI nếu provider của mày đăng ký theo file SPI
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev", "--spi-user-provider-database-enabled=true"]

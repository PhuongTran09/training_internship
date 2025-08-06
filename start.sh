#!/bin/sh

echo "Starting Spring Boot backend..."
java -jar /app/backend/app.jar &

echo "Starting nginx..."
nginx -g 'daemon off;'

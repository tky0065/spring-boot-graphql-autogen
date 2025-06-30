#!/bin/bash

echo "🧪 Test de compilation du module Spring Boot Starter"
echo "=================================================="

cd graphql-autogen-core
echo "📦 Installation du module Core sans tests..."
mvn clean install -DskipTests
echo ""

cd ../graphql-autogen-spring-boot-starter  
echo "📦 Compilation du module Spring Boot Starter..."
mvn clean compile
echo ""

echo "✅ Test terminé!"

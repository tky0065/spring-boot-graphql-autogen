#!/bin/bash

echo "🧪 Test de compilation du Core sans tests"
echo "========================================"

cd graphql-autogen-core

echo "🧹 Nettoyage complet..."
mvn clean

echo "📦 Compilation sans tests..."
mvn compile -DskipTests

echo ""
echo "📦 Installation sans tests..."
mvn install -DskipTests

echo ""
echo "✅ Test terminé!"

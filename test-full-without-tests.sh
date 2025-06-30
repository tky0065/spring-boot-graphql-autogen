#!/bin/bash

echo "🧪 Test de compilation complète sans tests"
echo "=========================================="

echo "🧹 Nettoyage complet..."
mvn clean

echo "📦 Compilation sans tests..."
mvn compile -DskipTests

echo ""
echo "📦 Installation sans tests..."
mvn install -DskipTests

echo ""
echo "✅ Test terminé!"

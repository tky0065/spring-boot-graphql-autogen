#!/bin/bash
#
# Script de build complet pour tous les modules (Maven + Gradle)
# Phase 10 : Publication et maintenance - Build hybride
#

set -e

echo "🏗️ Build complet du projet GraphQL AutoGen"
echo "==========================================="

# Variables
BUILD_TYPE="${1:-clean install}"
SKIP_TESTS="${SKIP_TESTS:-false}"

echo "📋 Configuration:"
echo "- Type de build: $BUILD_TYPE"
echo "- Skip tests: $SKIP_TESTS"
echo ""

# 1. Build des modules Maven (core, starter, maven-plugin, cli)
echo "1️⃣ Build des modules Maven..."
echo ""

MAVEN_ARGS="$BUILD_TYPE"
if [[ "$SKIP_TESTS" == "true" ]]; then
    MAVEN_ARGS="$MAVEN_ARGS -DskipTests=true"
fi

# Utiliser le POM mis à jour qui exclut le plugin Gradle
if [[ -f "pom-maven-central-updated.xml" ]]; then
    echo "🔧 Utilisation du POM Maven Central (sans plugin Gradle)"
    mvn -f pom-maven-central-updated.xml $MAVEN_ARGS
else
    echo "🔧 Utilisation du POM standard"
    mvn $MAVEN_ARGS
fi

echo "✅ Build Maven terminé"
echo ""

# 2. Build du plugin Gradle séparément
echo "2️⃣ Build du plugin Gradle..."
echo ""

if [[ -d "graphql-autogen-gradle-plugin" ]]; then
    cd graphql-autogen-gradle-plugin
    
    # Vérifier si gradlew existe, sinon utiliser gradle global
    if [[ -f "./gradlew" ]]; then
        GRADLE_CMD="./gradlew"
    elif command -v gradle &> /dev/null; then
        GRADLE_CMD="gradle"
    else
        echo "❌ Gradle non trouvé. Installez Gradle ou ajoutez gradlew"
        exit 1
    fi
    
    echo "🔧 Utilisation de: $GRADLE_CMD"
    
    # Build en fonction du type
    case "$BUILD_TYPE" in
        "clean")
            $GRADLE_CMD clean
            ;;
        "compile"|"install")
            if [[ "$SKIP_TESTS" == "true" ]]; then
                $GRADLE_CMD build -x test
            else
                $GRADLE_CMD build
            fi
            ;;
        "clean install")
            if [[ "$SKIP_TESTS" == "true" ]]; then
                $GRADLE_CMD clean build -x test
            else
                $GRADLE_CMD clean build
            fi
            ;;
        "test")
            $GRADLE_CMD test
            ;;
        "publish")
            if [[ "$SKIP_TESTS" == "true" ]]; then
                $GRADLE_CMD clean build publish -x test
            else
                $GRADLE_CMD clean build publish
            fi
            ;;
        *)
            echo "⚠️  Type de build non reconnu pour Gradle: $BUILD_TYPE"
            $GRADLE_CMD clean build
            ;;
    esac
    
    cd ..
    echo "✅ Build Gradle terminé"
else
    echo "⚠️  Répertoire graphql-autogen-gradle-plugin non trouvé"
fi

echo ""

# 3. Vérification des artifacts générés
echo "3️⃣ Vérification des artifacts..."
echo ""

echo "📦 Artifacts Maven générés:"
find . -name "*.jar" -path "*/target/*" | head -10

echo ""
echo "📦 Artifacts Gradle générés:"
find . -name "*.jar" -path "*/build/*" | head -5

echo ""

# 4. Tests d'intégration rapides
echo "4️⃣ Tests d'intégration rapides..."
echo ""

if [[ "$SKIP_TESTS" != "true" ]]; then
    # Test que les JARs principaux existent
    CORE_JAR=$(find . -name "*graphql-autogen-core*.jar" -path "*/target/*" | head -1)
    STARTER_JAR=$(find . -name "*spring-boot-starter*.jar" -path "*/target/*" | head -1)
    
    if [[ -f "$CORE_JAR" && -f "$STARTER_JAR" ]]; then
        echo "✅ JARs principaux trouvés:"
        echo "   - Core: $(basename "$CORE_JAR")"
        echo "   - Starter: $(basename "$STARTER_JAR")"
    else
        echo "⚠️  Certains JARs principaux manquent"
    fi
    
    # Test que le plugin Gradle est généré
    GRADLE_JAR=$(find . -name "*gradle-plugin*.jar" -path "*/build/*" | head -1)
    if [[ -f "$GRADLE_JAR" ]]; then
        echo "✅ Plugin Gradle: $(basename "$GRADLE_JAR")"
    else
        echo "⚠️  JAR du plugin Gradle manquant"
    fi
else
    echo "⏭️  Tests d'intégration ignorés (SKIP_TESTS=true)"
fi

echo ""

# 5. Résumé
echo "5️⃣ Résumé du build"
echo "=================="

if [[ $? -eq 0 ]]; then
    echo "🎉 Build complet réussi!"
    echo ""
    echo "📊 Modules buildés:"
    echo "   ✅ graphql-autogen-core (Maven)"
    echo "   ✅ graphql-autogen-spring-boot-starter (Maven)"
    echo "   ✅ graphql-autogen-maven-plugin (Maven)"
    echo "   ✅ graphql-autogen-gradle-plugin (Gradle)"
    echo "   ✅ graphql-autogen-cli (Maven)"
    echo ""
    echo "🚀 Prochaines étapes:"
    echo "   - Publication Maven: mvn deploy -Prelease"
    echo "   - Publication Gradle: cd graphql-autogen-gradle-plugin && gradle publishPlugins"
    echo "   - Tests complets: ./scripts/build-all.sh test"
else
    echo "❌ Échec du build"
    exit 1
fi

echo ""
echo "📚 Aide:"
echo "   - Build sans tests: SKIP_TESTS=true ./scripts/build-all.sh"
echo "   - Build avec publication: ./scripts/build-all.sh publish"
echo "   - Nettoyage: ./scripts/build-all.sh clean"

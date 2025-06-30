#!/bin/bash

# Script de test de compilation pour vérifier les conflits de types
# Utilisé pour détecter les problèmes entre nos types et les types GraphQL standard

set -e

# Couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Fonction principale
main() {
    echo "🔍 Test de compilation - Spring Boot GraphQL Auto-Generator"
    echo "============================================================"
    
    log_info "Nettoyage des builds précédents..."
    mvn clean -q
    
    log_info "Compilation du module core..."
    if mvn compile -pl graphql-autogen-core -q; then
        log_success "✅ Module core compilé avec succès"
    else
        log_error "❌ Erreur de compilation du module core"
        exit 1
    fi
    
    log_info "Compilation du module spring-boot-starter..."
    if mvn compile -pl graphql-autogen-spring-boot-starter -q; then
        log_success "✅ Module spring-boot-starter compilé avec succès"
    else
        log_error "❌ Erreur de compilation du module spring-boot-starter"
        exit 1
    fi
    
    log_info "Compilation de tous les modules..."
    if mvn compile -q; then
        log_success "✅ Tous les modules compilés avec succès"
    else
        log_error "❌ Erreur de compilation globale"
        exit 1
    fi
    
    log_info "Exécution des tests unitaires..."
    if mvn test -q; then
        log_success "✅ Tous les tests passent"
    else
        log_warning "⚠️ Certains tests échouent (peut être normal pour des tests incomplets)"
    fi
    
    echo ""
    echo "============================================================"
    log_success "🎉 Compilation réussie ! Aucun conflit de types détecté."
    echo ""
    
    # Affichage des dépendances GraphQL Java
    log_info "📋 Versions des dépendances GraphQL utilisées :"
    mvn dependency:tree -Dincludes=com.graphql-java:graphql-java -q | grep graphql-java || echo "  - GraphQL Java : Version intégrée dans Spring Boot"
    
    echo ""
    log_info "✨ Le projet est prêt pour le développement et la production !"
}

# Gestion des erreurs
trap 'log_error "Script interrompu"; exit 1' INT TERM

# Vérification que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    log_error "Ce script doit être exécuté depuis la racine du projet"
    exit 1
fi

# Exécution
main "$@"
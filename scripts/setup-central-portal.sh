#!/bin/bash
#
# Script de configuration pour le nouveau Central Portal de Sonatype
# Phase 10 : Publication et maintenance - Configuration Central Portal 2025
#

set -e

echo "🌐 Configuration Central Portal pour Maven Central"
echo "================================================="

# Variables
CENTRAL_PORTAL_URL="${CENTRAL_PORTAL_URL:-https://central.sonatype.com}"
PROJECT_NAMESPACE="${PROJECT_NAMESPACE:-io.github.tky0065}"

echo "📋 Informations du projet:"
echo "- Namespace: $PROJECT_NAMESPACE"
echo "- Central Portal: $CENTRAL_PORTAL_URL"
echo ""

# Vérification des prérequis
echo "1️⃣ Vérification des prérequis..."

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé"
    exit 1
fi

if [[ ! -f "pom.xml" ]]; then
    echo "❌ Fichier pom.xml non trouvé"
    exit 1
fi

echo "✅ Prérequis validés"

# Configuration du compte
echo ""
echo "2️⃣ Configuration du compte Central Portal..."
echo ""
echo "📝 Créez un compte sur: $CENTRAL_PORTAL_URL"
echo ""

read -p "Avez-vous un compte Central Portal? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "⚠️  Créez un compte d'abord: $CENTRAL_PORTAL_URL"
    exit 0
fi

# Génération du User Token
echo ""
echo "3️⃣ Génération du User Token..."
echo ""
echo "📝 Instructions:"
echo "1. Se connecter sur: $CENTRAL_PORTAL_URL"
echo "2. Account > Generate User Token"
echo "3. Copier username et password"
echo ""

read -p "Username Token: " CENTRAL_TOKEN_USERNAME
read  -p "Password Token: " CENTRAL_TOKEN_PASSWORD
echo

if [[ -z "$CENTRAL_TOKEN_USERNAME" || -z "$CENTRAL_TOKEN_PASSWORD" ]]; then
    echo "❌ Credentials requis"
    exit 1
fi

# Test de connexion
echo ""
echo "4️⃣ Test de connexion..."

# Créer un fichier temporaire pour stocker la réponse
RESPONSE_FILE=$(mktemp)

# Utiliser -v pour un mode verbose et sauvegarder la sortie dans un fichier
echo "🔍 Tentative de connexion à l'API Central Portal..."
HTTP_CODE=$(curl -v -s -w "%{http_code}" -u "$CENTRAL_TOKEN_USERNAME:$CENTRAL_TOKEN_PASSWORD" \
    "$CENTRAL_PORTAL_URL/api/v1/publisher/status" -o "$RESPONSE_FILE" 2>&1)

# Afficher le code de réponse
echo "📊 Code de réponse HTTP: $HTTP_CODE"

# En cas d'erreur, afficher des informations supplémentaires
if [[ "$HTTP_CODE" == "200" ]]; then
    echo "✅ Authentification réussie"
else
    echo "❌ Échec authentification (HTTP $HTTP_CODE)"
    echo "🔍 Détails de l'erreur:"
    cat "$RESPONSE_FILE"
    echo ""
    echo "⚠️ Solutions possibles:"
    echo "1. Vérifiez que vos identifiants sont corrects"
    echo "2. Essayez de regénérer votre token sur $CENTRAL_PORTAL_URL"
    echo "3. Vérifiez l'état des services Sonatype: https://status.sonatype.com/"
    echo "4. Essayez une API alternative pour tester:"

    # Tentative avec une API alternative
    echo "🔄 Tentative avec une API alternative..."
    ALT_HTTP_CODE=$(curl -s -w "%{http_code}" -u "$CENTRAL_TOKEN_USERNAME:$CENTRAL_TOKEN_PASSWORD" \
        "$CENTRAL_PORTAL_URL/api/v1/components" -o /dev/null)
    echo "📊 Code de réponse HTTP alternatif: $ALT_HTTP_CODE"

    if [[ "$ALT_HTTP_CODE" == "200" ]]; then
        echo "✅ Authentification réussie avec l'API alternative"
        echo "⚠️ Le problème semble être spécifique à l'API de statut, mais vos identifiants fonctionnent"
        # On continue malgré l'erreur sur la première API
        HTTP_CODE=200
    else
        rm "$RESPONSE_FILE"
        exit 1
    fi
fi

# Nettoyage
rm "$RESPONSE_FILE"

# Configuration des variables
echo ""
echo "5️⃣ Configuration des variables..."

cat > .env.central << EOF
CENTRAL_TOKEN_USERNAME=$CENTRAL_TOKEN_USERNAME
CENTRAL_TOKEN_PASSWORD=$CENTRAL_TOKEN_PASSWORD
PROJECT_NAMESPACE=$PROJECT_NAMESPACE
EOF

chmod 600 .env.central
echo "✅ Variables sauvegardées dans .env.central"

# GitHub Secrets
echo ""
echo "6️⃣ GitHub Secrets:"
echo ""
echo "gh secret set CENTRAL_TOKEN_USERNAME --body \"$CENTRAL_TOKEN_USERNAME\""
echo "gh secret set CENTRAL_TOKEN_PASSWORD --body \"$CENTRAL_TOKEN_PASSWORD\""

# Test final
echo ""
echo "7️⃣ Test final..."
source .env.central

if mvn clean verify -Prelease -DskipTests=true -q; then
    echo "✅ Build de release réussi"
else
    echo "❌ Échec du build"
    exit 1
fi

echo ""
echo "🎉 Configuration terminée!"
echo ""
echo "📋 Commandes utiles:"
echo "- Test: mvn clean deploy -Prelease -Dcentral.autoPublish=false"
echo "- Release: mvn clean deploy -Prelease -Dcentral.autoPublish=true"
echo "- Dashboard: $CENTRAL_PORTAL_URL"

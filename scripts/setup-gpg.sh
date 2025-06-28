#!/bin/bash
#
# Script de configuration GPG pour Maven Central
# Phase 10 : Publication et maintenance - Configuration Maven Central
#

set -e

echo "🔐 Configuration GPG pour Maven Central"
echo "========================================"

# Variables
GPG_KEY_NAME="${GPG_KEY_NAME:-GraphQL-AutoGen-Signing-Key}"
GPG_USER_NAME="${GPG_USER_NAME:-EnokDev GraphQL AutoGen Team}"
GPG_USER_EMAIL="${GPG_USER_EMAIL:-graphql-autogen@enokdev.com}"
GPG_PASSPHRASE="${GPG_PASSPHRASE:-$(openssl rand -base64 32)}"

# Vérifier si GPG est installé
if ! command -v gpg &> /dev/null; then
    echo "❌ GPG n'est pas installé. Installation..."
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        if command -v brew &> /dev/null; then
            brew install gnupg
        else
            echo "❌ Homebrew non trouvé. Installez GPG manuellement."
            exit 1
        fi
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        if command -v apt-get &> /dev/null; then
            sudo apt-get update && sudo apt-get install -y gnupg
        elif command -v yum &> /dev/null; then
            sudo yum install -y gnupg
        else
            echo "❌ Gestionnaire de paquets non supporté. Installez GPG manuellement."
            exit 1
        fi
    else
        echo "❌ OS non supporté. Installez GPG manuellement."
        exit 1
    fi
fi

echo "✅ GPG installé : $(gpg --version | head -n1)"

# Créer le fichier de configuration GPG
echo "📝 Création de la configuration GPG..."

mkdir -p ~/.gnupg
chmod 700 ~/.gnupg

# Configuration GPG
cat > ~/.gnupg/gpg.conf << EOF
# Configuration GPG pour Maven Central
personal-cipher-preferences AES256 AES192 AES CAST5
personal-digest-preferences SHA512 SHA384 SHA256 SHA224
default-preference-list SHA512 SHA384 SHA256 SHA224 AES256 AES192 AES CAST5 ZLIB BZIP2 ZIP Uncompressed
cert-digest-algo SHA512
s2k-digest-algo SHA512
s2k-cipher-algo AES256
charset utf-8
fixed-list-mode
no-comments
no-emit-version
keyid-format 0xlong
list-options show-uid-validity
verify-options show-uid-validity
with-fingerprint
use-agent
EOF

# Configuration gpg-agent
cat > ~/.gnupg/gpg-agent.conf << EOF
# Configuration GPG Agent
pinentry-program $(which pinentry-tty 2>/dev/null || which pinentry-curses 2>/dev/null || echo /usr/bin/pinentry-tty)
default-cache-ttl 86400
max-cache-ttl 86400
allow-loopback-pinentry
EOF

chmod 600 ~/.gnupg/*.conf

# Redémarrer gpg-agent
gpgconf --kill gpg-agent
gpg-agent --daemon --allow-loopback-pinentry

# Vérifier si une clé existe déjà
if gpg --list-secret-keys --keyid-format LONG | grep -q "$GPG_USER_EMAIL"; then
    echo "⚠️  Une clé GPG existe déjà pour $GPG_USER_EMAIL"
    read -p "Voulez-vous la supprimer et en créer une nouvelle? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        # Supprimer les clés existantes
        KEY_ID=$(gpg --list-secret-keys --keyid-format LONG | grep -A1 "$GPG_USER_EMAIL" | grep sec | awk '{print $2}' | cut -d'/' -f2)
        if [ ! -z "$KEY_ID" ]; then
            echo "🗑️  Suppression de la clé existante $KEY_ID..."
            gpg --batch --yes --delete-secret-keys $KEY_ID
            gpg --batch --yes --delete-keys $KEY_ID
        fi
    else
        echo "🔑 Utilisation de la clé existante"
        KEY_ID=$(gpg --list-secret-keys --keyid-format LONG | grep -A1 "$GPG_USER_EMAIL" | grep sec | awk '{print $2}' | cut -d'/' -f2)
        GPG_PASSPHRASE_INPUT=""
        read -s -p "Entrez la passphrase de la clé existante: " GPG_PASSPHRASE_INPUT
        echo
        GPG_PASSPHRASE="$GPG_PASSPHRASE_INPUT"
    fi
else
    # Générer une nouvelle clé GPG
    echo "🔑 Génération d'une nouvelle clé GPG..."
    
    # Créer le fichier de configuration pour la génération de clé
    cat > /tmp/gpg-key-config << EOF
%echo Generating a basic OpenPGP key
Key-Type: RSA
Key-Length: 4096
Subkey-Type: RSA
Subkey-Length: 4096
Name-Real: $GPG_USER_NAME
Name-Email: $GPG_USER_EMAIL
Expire-Date: 2y
Passphrase: $GPG_PASSPHRASE
%pubring ~/.gnupg/pubring.kbx
%secring ~/.gnupg/secring.gpg
# Do a commit here, so that we can later print "done" :-)
%commit
%echo done
EOF

    # Générer la clé
    gpg --batch --generate-key /tmp/gpg-key-config
    rm -f /tmp/gpg-key-config
    
    # Obtenir l'ID de la clé générée
    KEY_ID=$(gpg --list-secret-keys --keyid-format LONG | grep -A1 "$GPG_USER_EMAIL" | grep sec | awk '{print $2}' | cut -d'/' -f2)
fi

if [ -z "$KEY_ID" ]; then
    echo "❌ Erreur: Impossible de trouver l'ID de la clé GPG"
    exit 1
fi

echo "✅ Clé GPG générée avec l'ID: $KEY_ID"

# Exporter la clé publique
echo "📤 Export de la clé publique..."
gpg --armor --export $KEY_ID > ~/.gnupg/public-key.asc
echo "✅ Clé publique exportée vers ~/.gnupg/public-key.asc"

# Exporter la clé privée (pour GitHub Secrets)
echo "📤 Export de la clé privée..."
gpg --armor --export-secret-keys $KEY_ID > ~/.gnupg/private-key.asc
echo "✅ Clé privée exportée vers ~/.gnupg/private-key.asc"

# Afficher les informations de la clé
echo ""
echo "🔐 Informations de la clé GPG:"
echo "=============================="
echo "ID de la clé: $KEY_ID"
echo "Nom: $GPG_USER_NAME"
echo "Email: $GPG_USER_EMAIL"
echo "Passphrase: $GPG_PASSPHRASE"
echo ""

# Afficher la clé publique pour upload vers serveurs de clés
echo "📋 Clé publique (à uploader vers les serveurs de clés):"
echo "======================================================="
cat ~/.gnupg/public-key.asc
echo ""

# Instructions pour upload vers serveurs de clés
echo "📤 Upload vers les serveurs de clés:"
echo "===================================="
echo "1. Upload automatique vers les serveurs de clés principaux:"
echo "   gpg --keyserver hkps://keys.openpgp.org --send-keys $KEY_ID"
echo "   gpg --keyserver hkps://keyserver.ubuntu.com --send-keys $KEY_ID"
echo ""

# Tentative d'upload automatique
echo "🚀 Upload automatique vers les serveurs de clés..."
if gpg --keyserver hkps://keys.openpgp.org --send-keys $KEY_ID; then
    echo "✅ Clé uploadée vers keys.openpgp.org"
else
    echo "⚠️  Échec upload vers keys.openpgp.org (normal si pas d'accès internet)"
fi

if gpg --keyserver hkps://keyserver.ubuntu.com --send-keys $KEY_ID; then
    echo "✅ Clé uploadée vers keyserver.ubuntu.com"
else
    echo "⚠️  Échec upload vers keyserver.ubuntu.com (normal si pas d'accès internet)"
fi

# Instructions pour GitHub Secrets
echo ""
echo "🔧 Configuration GitHub Secrets:"
echo "================================="
echo "Ajoutez ces secrets dans votre repository GitHub:"
echo ""
echo "GPG_PRIVATE_KEY:"
echo "$(cat ~/.gnupg/private-key.asc | base64 -w 0)"
echo ""
echo "GPG_PASSPHRASE:"
echo "$GPG_PASSPHRASE"
echo ""
echo "GPG_KEY_ID:"
echo "$KEY_ID"
echo ""

# Instructions pour variables d'environnement
echo "🌍 Variables d'environnement pour Maven:"
echo "========================================"
echo "export GPG_KEY_ID=$KEY_ID"
echo "export GPG_PASSPHRASE=\"$GPG_PASSPHRASE\""
echo ""
echo "Ajoutez ces variables à votre ~/.bashrc ou ~/.zshrc"

# Sauvegarder les informations dans un fichier
cat > ~/.gnupg/key-info.txt << EOF
# Informations clé GPG pour Maven Central
# Généré le: $(date)

GPG_KEY_ID=$KEY_ID
GPG_USER_NAME=$GPG_USER_NAME
GPG_USER_EMAIL=$GPG_USER_EMAIL
GPG_PASSPHRASE=$GPG_PASSPHRASE

# Commandes utiles:
# Lister les clés: gpg --list-secret-keys --keyid-format LONG
# Exporter clé publique: gpg --armor --export $KEY_ID
# Exporter clé privée: gpg --armor --export-secret-keys $KEY_ID
# Upload vers serveur: gpg --keyserver hkps://keys.openpgp.org --send-keys $KEY_ID
EOF

chmod 600 ~/.gnupg/key-info.txt
echo "💾 Informations sauvegardées dans ~/.gnupg/key-info.txt"

# Test de signature
echo ""
echo "🧪 Test de signature GPG..."
echo "test" | gpg --batch --yes --passphrase "$GPG_PASSPHRASE" --pinentry-mode loopback --armor --detach-sign --default-key $KEY_ID > /tmp/test-signature.asc
if [ $? -eq 0 ]; then
    echo "✅ Test de signature réussi"
    rm -f /tmp/test-signature.asc
else
    echo "❌ Échec du test de signature"
    exit 1
fi

echo ""
echo "🎉 Configuration GPG terminée avec succès!"
echo "=========================================="
echo ""
echo "📝 Prochaines étapes:"
echo "1. Vérifiez que la clé est uploadée vers les serveurs de clés"
echo "2. Configurez les GitHub Secrets avec les valeurs affichées"
echo "3. Configurez les variables d'environnement Maven"
echo "4. Testez avec: mvn clean deploy -Prelease"
echo ""

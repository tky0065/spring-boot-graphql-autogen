#!/bin/bash

# Script de construction des distributions multi-plateforme
# Pour la publication Homebrew et Chocolatey

set -e

# Configuration
VERSION="1.0.0"
CLI_MODULE="graphql-autogen-cli"
DIST_DIR="dist"
RELEASE_DIR="releases/v${VERSION}"

# Couleurs pour les logs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Construction du projet
build_project() {
    log_info "Construction du projet..."
    mvn clean package -DskipTests -q
    log_success "Projet construit avec succès"
}

# Création des distributions
create_distributions() {
    log_info "Création des distributions..."
    
    mkdir -p "$DIST_DIR" "$RELEASE_DIR"
    CLI_JAR="${CLI_MODULE}/target/${CLI_MODULE}-${VERSION}.jar"
    
    # macOS ARM64
    mkdir -p "$DIST_DIR/darwin-arm64"
    cp "$CLI_JAR" "$DIST_DIR/darwin-arm64/"
    create_unix_launcher "$DIST_DIR/darwin-arm64"
    tar -czf "$RELEASE_DIR/${CLI_MODULE}-${VERSION}-darwin-arm64.tar.gz" -C "$DIST_DIR/darwin-arm64" .
    
    # macOS x64
    mkdir -p "$DIST_DIR/darwin-x64"
    cp "$CLI_JAR" "$DIST_DIR/darwin-x64/"
    create_unix_launcher "$DIST_DIR/darwin-x64"
    tar -czf "$RELEASE_DIR/${CLI_MODULE}-${VERSION}-darwin-x64.tar.gz" -C "$DIST_DIR/darwin-x64" .
    
    # Linux x64
    mkdir -p "$DIST_DIR/linux-x64"
    cp "$CLI_JAR" "$DIST_DIR/linux-x64/"
    create_unix_launcher "$DIST_DIR/linux-x64"
    tar -czf "$RELEASE_DIR/${CLI_MODULE}-${VERSION}-linux-x64.tar.gz" -C "$DIST_DIR/linux-x64" .
    
    # Windows x64
    mkdir -p "$DIST_DIR/windows-x64"
    cp "$CLI_JAR" "$DIST_DIR/windows-x64/"
    create_windows_launcher "$DIST_DIR/windows-x64"
    (cd "$DIST_DIR/windows-x64" && zip -r "../../$RELEASE_DIR/${CLI_MODULE}-${VERSION}-windows-x64.zip" .)
    
    log_success "Distributions créées"
}

# Création du launcher Unix
create_unix_launcher() {
    local dist_dir="$1"
    cat > "$dist_dir/graphql-autogen" << 'EOF'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVA_CMD="${JAVA_HOME:-/usr}/bin/java"
[ ! -x "$JAVA_CMD" ] && JAVA_CMD="java"
exec "$JAVA_CMD" -jar "$SCRIPT_DIR/graphql-autogen-cli-1.0.0.jar" "$@"
EOF
    chmod +x "$dist_dir/graphql-autogen"
}

# Création du launcher Windows
create_windows_launcher() {
    local dist_dir="$1"
    cat > "$dist_dir/graphql-autogen.bat" << 'EOF'
@echo off
set SCRIPT_DIR=%~dp0
if defined JAVA_HOME (set JAVA_CMD=%JAVA_HOME%\bin\java.exe) else (set JAVA_CMD=java.exe)
"%JAVA_CMD%" -jar "%SCRIPT_DIR%graphql-autogen-cli-1.0.0.jar" %*
EOF
}

# Fonction principale
main() {
    echo "🚀 Construction des distributions GraphQL Auto-Generator CLI v$VERSION"
    build_project
    create_distributions
    log_success "🎉 Distributions créées dans $RELEASE_DIR"
}

main "$@"
# 📦 Distribution GraphQL Auto-Generator CLI

Ce dossier contient les fichiers nécessaires pour distribuer GraphQL Auto-Generator CLI via Homebrew et Chocolatey.

## 🍺 Distribution Homebrew

### Configuration
- **Formule** : `homebrew-formula/graphql-autogen.rb`
- **Platforms** : macOS (ARM64 et x64), Linux (x64)
- **Dépendances** : OpenJDK 21

### Installation
```bash
# Via Homebrew (après publication)
brew install graphql-autogen

# Via formule locale pour test
brew install ./homebrew-formula/graphql-autogen.rb
```

## 🍫 Distribution Chocolatey

### Configuration
- **Package** : `chocolatey-package/graphql-autogen.nuspec`
- **Scripts** : PowerShell pour installation/désinstallation
- **Platform** : Windows (x64)
- **Dépendances** : OpenJDK 21

### Installation
```powershell
# Via Chocolatey (après publication)
choco install graphql-autogen

# Via package local pour test
choco install graphql-autogen -s .
```

## 🔧 Script de build

### Usage
```bash
# Construire toutes les distributions
./scripts/build-distributions.sh

# Les artifacts sont créés dans releases/v1.0.0/
```

### Artifacts générés
- `graphql-autogen-cli-1.0.0-darwin-arm64.tar.gz` (macOS Apple Silicon)
- `graphql-autogen-cli-1.0.0-darwin-x64.tar.gz` (macOS Intel)
- `graphql-autogen-cli-1.0.0-linux-x64.tar.gz` (Linux)
- `graphql-autogen-cli-1.0.0-windows-x64.zip` (Windows)

## 🧪 Tests locaux

### Test Homebrew
```bash
# Build local
./scripts/build-distributions.sh

# Installation locale
brew install --build-from-source ./homebrew-formula/graphql-autogen.rb

# Test
graphql-autogen --version
graphql-autogen --help
```

### Test Chocolatey
```powershell
# Build local
.\scripts\build-distributions.sh

# Création du package
cd chocolatey-package
choco pack

# Installation locale
choco install graphql-autogen -s . -f

# Test
graphql-autogen --version
graphql-autogen --help
```

## 📋 Checklist de release

### Pré-release
- [ ] Tests du CLI sur toutes les plateformes
- [ ] Build des distributions localement
- [ ] Test des installations locales

### Release GitHub
- [ ] Créer tag git `v1.0.0`
- [ ] Créer release GitHub
- [ ] Upload des artifacts de distribution

### Publication Homebrew
- [ ] Mettre à jour checksums dans la formule
- [ ] Tester installation locale
- [ ] Créer PR vers homebrew-core

### Publication Chocolatey
- [ ] Mettre à jour checksums dans le nuspec
- [ ] Tester installation locale
- [ ] Publier le package sur chocolatey.org

## 📊 Métriques de distribution

### Objectifs
- **Homebrew** : 1000+ installations premier mois
- **Chocolatey** : 500+ installations premier mois
- **GitHub Releases** : 2000+ téléchargements

---

**🎯 Objectif** : Rendre GraphQL Auto-Generator CLI accessible à tous les développeurs via leurs gestionnaires de packages préférés !
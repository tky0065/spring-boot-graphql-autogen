# 📦 Guide de publication Maven Central

## 🎯 Vue d'ensemble

Ce guide détaille le processus complet de publication du Spring Boot GraphQL Auto-Generator sur Maven Central via Sonatype OSSRH.

---

## 📋 Prérequis

### 1. Compte Sonatype OSSRH

1. **Créer un compte** sur [issues.sonatype.org](https://issues.sonatype.org)
2. **Créer un ticket JIRA** pour demander l'accès au groupId `com.enokdev.graphql`
3. **Attendre l'approbation** (généralement 1-2 jours ouvrés)

### 2. Domaine vérifié

- **Domaine :** `enokdev.com`
- **Vérification :** DNS TXT record ou GitHub Pages
- **GroupId autorisé :** `com.enokdev.graphql`

### 3. Clé GPG configurée

Utilisez le script fourni pour générer et configurer une clé GPG :

```bash
chmod +x scripts/setup-gpg.sh
./scripts/setup-gpg.sh
```

---

## 🔧 Configuration

### 1. Variables d'environnement

Configurez ces variables d'environnement :

```bash
# Credentials Sonatype OSSRH
export SONATYPE_USERNAME="votre-username"
export SONATYPE_PASSWORD="votre-password"

# Configuration GPG
export GPG_KEY_ID="0x1234567890ABCDEF"
export GPG_PASSPHRASE="votre-passphrase-gpg"

# Optionnel: pour GitHub Actions
export GITHUB_TOKEN="ghp_xxxxxxxxxxxx"
```

### 2. Settings Maven

Copiez la configuration Maven :

```bash
cp maven-central-settings.xml ~/.m2/settings.xml
```

Ou utilisez le flag `-s` :

```bash
mvn deploy -s maven-central-settings.xml -Prelease
```

### 3. POM de publication

Le fichier `pom-maven-central.xml` contient toute la configuration nécessaire pour Maven Central.

---

## 🚀 Processus de publication

### 1. Préparation de la release

```bash
# 1. Vérifier l'état du projet
mvn clean verify -Prelease

# 2. Mettre à jour la version
mvn versions:set -DnewVersion=1.0.0
mvn versions:commit

# 3. Vérifier les tests et la compilation
mvn clean test -Pci

# 4. Générer la documentation
mvn javadoc:aggregate

# 5. Vérifier la signature GPG
mvn clean package -Prelease -DskipTests
```

### 2. Publication des snapshots

Pour publier une version snapshot :

```bash
# Version snapshot
mvn versions:set -DnewVersion=1.0.1-SNAPSHOT
mvn clean deploy -Psnapshot

# Vérification
curl -I https://s01.oss.sonatype.org/content/repositories/snapshots/com/enokdev/graphql/spring-boot-graphql-autogen/1.0.1-SNAPSHOT/
```

### 3. Publication de la release

```bash
# 1. Release complète
mvn clean deploy -Prelease

# 2. Vérification du staging repository
# Se connecter à https://s01.oss.sonatype.org/
# Vérifier le repository staging

# 3. Fermeture et release du staging
# Via interface web Sonatype ou Maven plugin
mvn nexus-staging:close -Prelease
mvn nexus-staging:release -Prelease
```

### 4. Release automatisée avec Maven Release Plugin

```bash
# Configuration Git
git config user.name "GraphQL AutoGen Release Bot"
git config user.email "release@enokdev.com"

# Préparation de la release
mvn release:prepare -Prelease \
    -DtagNameFormat=v@{project.version} \
    -DreleaseVersion=1.0.0 \
    -DdevelopmentVersion=1.0.1-SNAPSHOT

# Exécution de la release
mvn release:perform -Prelease

# En cas de problème, rollback
mvn release:rollback
```

---

## 🔍 Vérification et validation

### 1. Vérification des artifacts

Après publication, vérifiez que tous les artifacts sont présents :

```bash
# URL de base Maven Central
BASE_URL="https://repo1.maven.org/maven2/com/enokdev/graphql"

# Vérifier les artifacts principaux
curl -I "$BASE_URL/spring-boot-graphql-autogen/1.0.0/spring-boot-graphql-autogen-1.0.0.pom"
curl -I "$BASE_URL/graphql-autogen-core/1.0.0/graphql-autogen-core-1.0.0.jar"
curl -I "$BASE_URL/graphql-autogen-spring-boot-starter/1.0.0/graphql-autogen-spring-boot-starter-1.0.0.jar"

# Vérifier les sources et javadoc
curl -I "$BASE_URL/graphql-autogen-core/1.0.0/graphql-autogen-core-1.0.0-sources.jar"
curl -I "$BASE_URL/graphql-autogen-core/1.0.0/graphql-autogen-core-1.0.0-javadoc.jar"

# Vérifier les signatures GPG
curl -I "$BASE_URL/graphql-autogen-core/1.0.0/graphql-autogen-core-1.0.0.jar.asc"
```

### 2. Test d'utilisation

Créez un projet de test pour vérifier l'utilisation :

```xml
<dependency>
    <groupId>com.enokdev.graphql</groupId>
    <artifactId>graphql-autogen-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3. Vérification sur Maven Central Search

- **URL :** https://search.maven.org/search?q=g:com.enokdev.graphql
- **Délai :** 2-4 heures après release
- **Synchronisation :** Automatique vers tous les mirrors

---

## 🛠️ Commandes utiles

### Gestion des versions

```bash
# Afficher la version actuelle
mvn help:evaluate -Dexpression=project.version -q -DforceStdout

# Mettre à jour toutes les versions des modules
mvn versions:set -DnewVersion=1.0.0
mvn versions:commit

# Revenir à la version précédente
mvn versions:revert

# Mettre à jour les dépendances
mvn versions:display-dependency-updates
mvn versions:use-latest-releases
```

### Gestion GPG

```bash
# Lister les clés
gpg --list-secret-keys --keyid-format LONG

# Exporter la clé publique
gpg --armor --export $GPG_KEY_ID

# Tester la signature
echo "test" | gpg --armor --detach-sign --default-key $GPG_KEY_ID

# Upload vers serveurs de clés
gpg --keyserver hkps://keys.openpgp.org --send-keys $GPG_KEY_ID
gpg --keyserver hkps://keyserver.ubuntu.com --send-keys $GPG_KEY_ID
```

### Debugging

```bash
# Debug Maven avec logs détaillés
mvn clean deploy -Prelease -X -e

# Vérifier la configuration effective
mvn help:effective-pom -Prelease

# Vérifier les settings
mvn help:effective-settings

# Test GPG avec Maven
mvn clean package gpg:sign -Prelease -DskipTests
```

---

## 🚨 Résolution de problèmes

### Erreur GPG "No such file or directory"

```bash
# Vérifier l'installation GPG
which gpg
gpg --version

# Configurer le path si nécessaire
export GPG_EXECUTABLE=$(which gpg)

# Redémarrer gpg-agent
gpgconf --kill gpg-agent
gpg-agent --daemon
```

### Erreur "401 Unauthorized" Sonatype

```bash
# Vérifier les credentials
echo $SONATYPE_USERNAME
echo $SONATYPE_PASSWORD

# Tester l'authentification
curl -u "$SONATYPE_USERNAME:$SONATYPE_PASSWORD" \
     "https://s01.oss.sonatype.org/service/local/staging/profiles"
```

### Erreur "No valid OpenPGP data found"

```bash
# Vérifier la clé GPG
gpg --list-secret-keys --keyid-format LONG

# Vérifier la configuration
cat ~/.gnupg/gpg.conf
cat ~/.gnupg/gpg-agent.conf

# Test de signature manuelle
echo "test" | gpg --batch --yes --passphrase "$GPG_PASSPHRASE" \
    --pinentry-mode loopback --armor --detach-sign --default-key $GPG_KEY_ID
```

### Repository staging bloqué

```bash
# Lister les repositories staging
mvn nexus-staging:rc-list -Prelease

# Fermer le repository manuellement
mvn nexus-staging:close -Prelease -DstagingRepositoryId=comenokdevgraphql-1234

# Abandonner le repository staging
mvn nexus-staging:drop -Prelease -DstagingRepositoryId=comenokdevgraphql-1234
```

---

## 📊 Checklist de publication

### Avant publication ✅

- [ ] **Tests passent** : `mvn clean test`
- [ ] **Couverture > 85%** : `mvn jacoco:report`
- [ ] **Documentation à jour** : README, CHANGELOG
- [ ] **Version finalisée** : Pas de SNAPSHOT
- [ ] **GPG configuré** : Clé valide et uploadée
- [ ] **Credentials Sonatype** : Testés et valides

### Pendant publication ✅

- [ ] **Build réussit** : `mvn clean deploy -Prelease`
- [ ] **Artifacts signés** : Fichiers .asc présents
- [ ] **Sources incluses** : JAR sources généré
- [ ] **Javadoc incluse** : JAR javadoc généré
- [ ] **Staging fermé** : Repository validé

### Après publication ✅

- [ ] **Artifacts disponibles** : URLs Maven Central accessibles
- [ ] **Search Maven Central** : Artifacts trouvables
- [ ] **Test d'intégration** : Nouveau projet utilise la lib
- [ ] **Documentation mise à jour** : Versions dans README
- [ ] **Annonce** : Blog, réseaux sociaux, GitHub Release

---

## 🎯 Métriques de succès

### Publication

- **Temps de publication** : < 30 minutes (staging → Central)
- **Synchronisation** : < 4 heures (disponible sur search.maven.org)
- **Taille artifacts** : 
  - Core JAR : ~500KB
  - Starter JAR : ~50KB
  - Sources : ~300KB
  - Javadoc : ~800KB

### Adoption

- **Téléchargements** : Tracking via [mvnrepository.com](https://mvnrepository.com)
- **Dépendants** : Monitoring GitHub, GitLab
- **Issues** : Suivi des problèmes d'intégration

---

## 📚 Ressources

### Documentation officielle

- [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)

### Outils utiles

- [Sonatype Nexus Repository Manager](https://s01.oss.sonatype.org/)
- [Maven Central Search](https://search.maven.org/)
- [GPG Keyserver](https://keys.openpgp.org/)
- [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/)

---

**📅 Dernière mise à jour :** Juin 2025  
**🎯 Version cible :** 1.0.0  
**📦 Statut :** Prêt pour publication Maven Central

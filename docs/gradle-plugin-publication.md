# 📦 Publication du Plugin Gradle

## 🎯 Vue d'ensemble

Le plugin Gradle suit un processus de publication différent des modules Maven car :
1. Les dépendances Gradle ne sont pas sur Maven Central
2. Le Gradle Plugin Portal est la destination recommandée
3. Le build Gradle est plus adapté que Maven pour les plugins Gradle

---

## 🔧 Architecture hybride

### Modules Maven (Maven Central)
- `graphql-autogen-core`
- `graphql-auto
- gen-spring-boot-starter` 
- `graphql-autogen-maven-plugin`
- `graphql-autogen-cli`

### Module Gradle (Gradle Plugin Portal)
- `graphql-autogen-gradle-plugin`

---

## 🚀 Publication du plugin Gradle

### 1. Prérequis

#### Créer un compte Gradle Plugin Portal
```bash
# 1. Aller sur https://plugins.gradle.org/
# 2. Se connecter avec GitHub
# 3. Générer API Key
# 4. Configurer ~/.gradle/gradle.properties
```

#### Configuration des credentials
```bash
# ~/.gradle/gradle.properties
gradle.publish.key=your-api-key
gradle.publish.secret=your-api-secret

# Optionnel: signature GPG
signing.keyId=your-gpg-key-id
signing.password=your-gpg-passphrase
signing.secretKeyRingFile=/path/to/secring.gpg
```

### 2. Build et publication

#### Build local
```bash
cd graphql-autogen-gradle-plugin

# Build sans publication
./gradlew clean build

# Tests
./gradlew test

# Vérification du plugin
./gradlew validatePlugins
```

#### Publication vers Gradle Plugin Portal
```bash
# Publication complète
./gradlew publishPlugins

# Publication avec validation
./gradlew clean build publishPlugins
```

### 3. Vérification

#### Gradle Plugin Portal
- URL: https://plugins.gradle.org/plugin/com.enokdev.graphql.autogen
- Recherche: "graphql autogen"

#### Test d'utilisation
```gradle
// build.gradle
plugins {
    id 'com.enokdev.graphql.autogen' version '1.0.0'
}

graphqlAutogen {
    basePackages = ['com.example.entities']
    outputDir = file('src/main/resources/graphql')
}
```

---

## 🔄 Processus de release hybride

### 1. Release des modules Maven d'abord

```bash
# 1. Build et publication Maven Central
mvn clean deploy -Prelease

# 2. Attendre que core soit disponible sur Maven Central
# (nécessaire pour les dépendances du plugin Gradle)
```

### 2. Release du plugin Gradle ensuite

```bash
# 1. Mettre à jour la version de core dans build.gradle
cd graphql-autogen-gradle-plugin

# 2. Publier le plugin
./gradlew publishPlugins
```

### 3. Automatisation avec GitHub Actions

#### Workflow séparé pour Gradle
```yaml
# .github/workflows/publish-gradle-plugin.yml
name: 📦 Publish Gradle Plugin

on:
  workflow_run:
    workflows: ["🚀 Release to Maven Central"]
    types:
      - completed

jobs:
  publish-gradle-plugin:
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          
      - name: Setup Gradle
        uses: gradle/gradle-build-action@v2
        
      - name: Publish to Gradle Plugin Portal
        env:
          GRADLE_PUBLISH_KEY: ${{ secrets.GRADLE_PUBLISH_KEY }}
          GRADLE_PUBLISH_SECRET: ${{ secrets.GRADLE_PUBLISH_SECRET }}
        run: |
          cd graphql-autogen-gradle-plugin
          ./gradlew publishPlugins
```

---

## 🧪 Tests du plugin Gradle

### Tests unitaires
```bash
cd graphql-autogen-gradle-plugin
./gradlew test
```

### Tests d'intégration avec TestKit
```java
@Test
void testPluginApplication() {
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withArguments("generateGraphQLSchema")
        .withPluginClasspath()
        .build();
        
    assertThat(result.task(":generateGraphQLSchema").getOutcome())
        .isEqualTo(TaskOutcome.SUCCESS);
}
```

### Test fonctionnel complet
```bash
# Créer un projet de test
mkdir test-gradle-plugin
cd test-gradle-plugin

cat > build.gradle << EOF
plugins {
    id 'java'
    id 'com.enokdev.graphql.autogen' version '1.0.0'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa:3.3.1'
}

graphqlAutogen {
    basePackages = ['com.example']
    outputFile = file('schema.graphqls')
}
EOF

# Créer une entité de test
mkdir -p src/main/java/com/example
cat > src/main/java/com/example/Book.java << EOF
package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Book {
    @Id
    private Long id;
    private String title;
    
    // getters/setters
}
EOF

# Exécuter la génération
./gradlew generateGraphQLSchema

# Vérifier le résultat
cat schema.graphqls
```

---

## 🔒 Sécurité

### Signature des artifacts
```gradle
// build.gradle
signing {
    required { gradle.taskGraph.hasTask('publish') }
    sign publishing.publications.maven
}
```

### Variables d'environnement sensibles
```bash
# GitHub Secrets requis
GRADLE_PUBLISH_KEY
GRADLE_PUBLISH_SECRET
GPG_PRIVATE_KEY
GPG_PASSPHRASE
```

---

## 📊 Avantages de l'approche hybride

### ✅ **Optimisations par écosystème**
- **Maven** : Parfait pour les librairies Java standard
- **Gradle** : Optimal pour les plugins Gradle avec TestKit

### ✅ **Distribution native**
- **Maven Central** : Maximum de compatibilité
- **Gradle Plugin Portal** : Découvrabilité et facilité d'usage

### ✅ **Outils appropriés**
- **Maven** : Gestion des dépendances complexes
- **Gradle** : API native pour plugins

---

## 🚨 Troubleshooting

### Erreur "gradle-core not found"
```bash
# Utiliser gradle-api au lieu de gradle-core
implementation 'org.gradle:gradle-api:8.8'
```

### Erreur de publication
```bash
# Vérifier les credentials
./gradlew publishPlugins --info

# Debug la configuration
./gradlew properties | grep publish
```

### Tests TestKit échouent
```bash
# Vérifier le classpath du plugin
./gradlew pluginUnderTestMetadata

# Debug avec logs
./gradlew test --info
```

---

**📚 Ressources :**
- [Gradle Plugin Development](https://docs.gradle.org/current/userguide/custom_plugins.html)
- [Gradle Plugin Portal](https://plugins.gradle.org/)
- [TestKit Guide](https://docs.gradle.org/current/userguide/test_kit.html)

# 🔨 Guide des plugins Maven et Gradle

<div align="center">

**Générez vos schémas GraphQL au build-time avec les plugins officiels**

*Intégration native avec Maven et Gradle*

</div>

---

## 🎯 Vue d'ensemble

Les plugins Maven et Gradle de GraphQL AutoGen permettent de générer vos schémas GraphQL pendant la phase de build, avant même le démarrage de l'application.

### ✅ Avantages des plugins
- 🚀 **Génération build-time** : Schéma disponible avant l'exécution
- 📦 **Intégration CI/CD** : Génération automatique dans vos pipelines
- 🔍 **Validation précoce** : Erreurs détectées dès la compilation
- 📁 **Artefacts versionnés** : Schémas inclus dans vos JARs
- ⚡ **Performance runtime** : Pas de génération au démarrage

---

## 🔨 Plugin Maven

### Installation basique

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.enokdev</groupId>
            <artifactId>graphql-autogen-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-schema</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <basePackages>
                    <basePackage>com.example.model</basePackage>
                </basePackages>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Configuration complète

```xml
<plugin>
    <groupId>com.enokdev</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <configuration>
        <!-- Packages à scanner -->
        <basePackages>
            <basePackage>com.example.model</basePackage>
            <basePackage>com.example.dto</basePackage>
        </basePackages>
        
        <!-- Sortie -->
        <outputDirectory>${project.build.directory}/generated-sources/graphql</outputDirectory>
        <schemaFileName>schema.graphqls</schemaFileName>
        
        <!-- Options -->
        <generateInputs>true</generateInputs>
        <generateDataLoaders>true</generateDataLoaders>
        <namingStrategy>CAMEL_CASE</namingStrategy>
        
        <!-- Mappings personnalisés -->
        <typeMappings>
            <typeMapping>java.time.LocalDateTime=DateTime</typeMapping>
            <typeMapping>java.math.BigDecimal=Decimal</typeMapping>
        </typeMappings>
    </configuration>
</plugin>
```

---

## 🐘 Plugin Gradle

### Installation basique

```gradle
plugins {
    id 'com.enokdev.graphql-autogen' version '1.0.0-SNAPSHOT'
}

graphqlAutogen {
    basePackages = ['com.example.model']
}
```

### Configuration complète

```gradle
graphqlAutogen {
    basePackages = ['com.example.model', 'com.example.dto']
    outputDirectory = file('src/main/resources/graphql')
    schemaFileName = 'schema.graphqls'
    
    generateInputs = true
    generateDataLoaders = true
    namingStrategy = 'CAMEL_CASE'
    
    typeMappings = [
        'java.time.LocalDateTime': 'DateTime',
        'java.math.BigDecimal': 'Decimal'
    ]
}
```

---

## 🚀 Utilisation

### Commandes Maven

```bash
# Génération du schéma
mvn graphql-autogen:generate-schema

# Avec compilation
mvn clean compile

# Validation du schéma
mvn graphql-autogen:validate-schema
```

### Commandes Gradle

```bash
# Génération du schéma
./gradlew generateGraphQLSchema

# Validation
./gradlew validateGraphQLSchema

# Build complet
./gradlew build
```

---

## 🔄 Intégration CI/CD

### GitHub Actions

```yaml
- name: Generate GraphQL Schema
  run: mvn graphql-autogen:generate-schema

- name: Build Application  
  run: mvn clean package
```

### Avantages build-time

| Aspect | Build-time | Runtime |
|--------|------------|---------|
| **Performance** | ⚡ Aucun overhead | ⚠️ Génération au démarrage |
| **Validation** | ✅ Échec de build | ❌ Échec au runtime |
| **CI/CD** | ✅ Intégration native | ⚠️ Tests nécessaires |

---

**🎉 Vos schémas sont maintenant générés automatiquement à chaque build !**

[Configuration avancée →](./plugins-advanced.md)

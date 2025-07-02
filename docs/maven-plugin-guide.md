# Guide du Plugin Maven GraphQL AutoGen

🔧 **Automatisez la génération de schémas GraphQL dans vos builds Maven !**

Le plugin Maven GraphQL AutoGen vous permet d'intégrer la génération de schémas GraphQL directement dans votre processus de build Maven, garantissant que vos schémas sont toujours à jour avec votre code source.

---

## 📦 Installation

Ajoutez le plugin à votre fichier `pom.xml` :

```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.github.tky0065</groupId>
            <artifactId>graphql-autogen-maven-plugin</artifactId>
            <version>1.0.1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <!-- Configuration optionnelle -->
                <basePackages>
                    <basePackage>com.example.model</basePackage>
                    <basePackage>com.example.controller</basePackage>
                </basePackages>
                <outputDirectory>${project.build.directory}/generated-sources/graphql</outputDirectory>
                <schemaFileName>schema.graphqls</schemaFileName>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 🎯 Goals disponibles

### `graphql-autogen:generate`

Génère les schémas GraphQL à partir de vos classes annotées.

#### Utilisation

```bash
# Génération manuelle
mvn graphql-autogen:generate

# Intégration dans le build
mvn compile
```

#### Paramètres de configuration

| Paramètre | Type | Description | Défaut |
|-----------|------|-------------|---------|
| `basePackages` | `List<String>` | Packages à scanner pour les annotations | `[]` (scan tout le classpath) |
| `outputDirectory` | `String` | Répertoire de sortie pour les schémas générés | `${project.build.directory}/generated-sources/graphql` |
| `schemaFileName` | `String` | Nom du fichier de schéma généré | `schema.graphqls` |
| `includeIntrospection` | `boolean` | Inclure les types d'introspection | `true` |
| `skipGeneration` | `boolean` | Ignorer la génération | `false` |
| `encoding` | `String` | Encodage des fichiers générés | `UTF-8` |

---

## ⚙️ Configuration avancée

### Configuration complète

```xml
<plugin>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.1</version>
    <executions>
        <execution>
            <id>generate-graphql-schema</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- Packages à scanner -->
        <basePackages>
            <basePackage>com.example.entities</basePackage>
            <basePackage>com.example.controllers</basePackage>
            <basePackage>com.example.services</basePackage>
        </basePackages>
        
        <!-- Configuration de sortie -->
        <outputDirectory>${project.build.directory}/graphql-schemas</outputDirectory>
        <schemaFileName>api-schema.graphqls</schemaFileName>
        <encoding>UTF-8</encoding>
        
        <!-- Options de génération -->
        <includeIntrospection>true</includeIntrospection>
        <skipGeneration>false</skipGeneration>
        
        <!-- Mapping de types personnalisés -->
        <typeMapping>
            <LocalDateTime>DateTime</LocalDateTime>
            <BigDecimal>Decimal</BigDecimal>
            <UUID>ID</UUID>
        </typeMapping>
        
        <!-- Configuration de nommage -->
        <namingStrategy>CAMEL_CASE</namingStrategy>
        <generateInputTypes>true</generateInputTypes>
    </configuration>
</plugin>
```

### Configuration avec propriétés

Vous pouvez également utiliser des propriétés Maven :

```xml
<properties>
    <graphql.basePackages>com.example.model,com.example.controller</graphql.basePackages>
    <graphql.outputDir>${project.build.directory}/graphql</graphql.outputDir>
    <graphql.schemaFile>schema.graphqls</graphql.schemaFile>
</properties>

<plugin>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.1</version>
    <configuration>
        <basePackages>${graphql.basePackages}</basePackages>
        <outputDirectory>${graphql.outputDir}</outputDirectory>
        <schemaFileName>${graphql.schemaFile}</schemaFileName>
    </configuration>
</plugin>
```

---

## 🔄 Intégration dans le cycle de vie Maven

### Exécution automatique

Pour exécuter la génération automatiquement lors du build :

```xml
<execution>
    <id>generate-schema</id>
    <phase>generate-sources</phase>
    <goals>
        <goal>generate</goal>
    </goals>
</execution>
```

### Phases recommandées

- **`generate-sources`** : Pour générer avant la compilation
- **`compile`** : Pour générer après la compilation
- **`prepare-package`** : Pour générer avant l'empaquetage

### Liaison avec d'autres plugins

```xml
<!-- Plugin Maven Resources pour copier les schémas -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-graphql-schemas</id>
            <phase>process-resources</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.outputDirectory}/graphql</outputDirectory>
                <resources>
                    <resource>
                        <directory>${project.build.directory}/generated-sources/graphql</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 🎨 Exemples d'utilisation

### Projet simple

```xml
<plugin>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Projet multi-modules

```xml
<!-- Dans le POM parent -->
<pluginManagement>
    <plugins>
        <plugin>
            <groupId>io.github.tky0065</groupId>
            <artifactId>graphql-autogen-maven-plugin</artifactId>
            <version>1.0.1</version>
            <configuration>
                <outputDirectory>${project.build.directory}/graphql</outputDirectory>
                <includeIntrospection>true</includeIntrospection>
            </configuration>
        </plugin>
    </plugins>
</pluginManagement>

<!-- Dans chaque module -->
<plugin>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <basePackages>
            <basePackage>com.example.${project.artifactId}</basePackage>
        </basePackages>
    </configuration>
</plugin>
```

### CI/CD avec validation

```xml
<plugin>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.1</version>
    <executions>
        <execution>
            <id>generate-schema</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
        <execution>
            <id>validate-schema</id>
            <phase>test</phase>
            <goals>
                <goal>validate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 🔍 Dépannage

### Problèmes courants

#### 1. Plugin non trouvé

**Erreur :**
```
Plugin io.github.tky0065:graphql-autogen-maven-plugin not found
```

**Solution :**
Vérifiez que vous avez ajouté le bon groupId et artifactId, et que la version existe dans Maven Central.

#### 2. Classes non trouvées

**Erreur :**
```
No GraphQL types found in specified packages
```

**Solution :**
- Vérifiez que vos classes sont bien annotées avec `@GraphQLType`
- Assurez-vous que les packages spécifiés sont corrects
- Vérifiez que les classes sont dans le classpath

#### 3. Répertoire de sortie

**Erreur :**
```
Cannot write to output directory
```

**Solution :**
- Vérifiez les permissions du répertoire
- Assurez-vous que le répertoire parent existe
- Utilisez un chemin absolu si nécessaire

### Debug

Activez le debug pour obtenir plus d'informations :

```bash
mvn graphql-autogen:generate -X
```

---

## 📝 Exemple complet

Voici un exemple complet d'un projet utilisant le plugin Maven :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-graphql-api</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <spring-boot.version>3.3.1</spring-boot.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.tky0065</groupId>
            <artifactId>graphql-autogen-spring-boot-starter</artifactId>
            <version>1.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- GraphQL AutoGen Plugin -->
            <plugin>
                <groupId>io.github.tky0065</groupId>
                <artifactId>graphql-autogen-maven-plugin</artifactId>
                <version>1.0.1</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <basePackages>
                        <basePackage>com.example.entities</basePackage>
                        <basePackage>com.example.controllers</basePackage>
                    </basePackages>
                    <outputDirectory>${project.build.directory}/generated-graphql</outputDirectory>
                    <schemaFileName>api.graphqls</schemaFileName>
                </configuration>
            </plugin>

            <!-- Spring Boot Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

Structure de fichiers du projet :

```
my-graphql-api/
├── src/
│   └── main/
│       └── java/
│           └── com/example/
│               ├── entities/
│               │   ├── User.java        # @GraphQLType
│               │   └── Product.java     # @GraphQLType
│               └── controllers/
│                   └── ApiController.java # @GraphQLController
├── target/
│   └── generated-graphql/
│       └── api.graphqls                 # Schéma généré
└── pom.xml
```

---

## 🚀 Bonnes pratiques

### 1. Organisation des packages

```xml
<basePackages>
    <basePackage>com.example.entities</basePackage>     <!-- Types GraphQL -->
    <basePackage>com.example.dto</basePackage>          <!-- Types Input -->
    <basePackage>com.example.controllers</basePackage>  <!-- Resolvers -->
</basePackages>
```

### 2. Nommage des fichiers

```xml
<schemaFileName>${project.artifactId}-schema.graphqls</schemaFileName>
```

### 3. Intégration CI/CD

```xml
<!-- Générer et valider dans le build -->
<execution>
    <id>generate</id>
    <phase>generate-sources</phase>
    <goals>
        <goal>generate</goal>
    </goals>
</execution>
<execution>
    <id>validate</id>
    <phase>test</phase>
    <goals>
        <goal>validate</goal>
    </goals>
</execution>
```

### 4. Profils Maven

```xml
<profiles>
    <profile>
        <id>graphql-dev</id>
        <properties>
            <graphql.includeIntrospection>true</graphql.includeIntrospection>
        </properties>
    </profile>
    <profile>
        <id>graphql-prod</id>
        <properties>
            <graphql.includeIntrospection>false</graphql.includeIntrospection>
        </properties>
    </profile>
</profiles>
```

---

## 📚 Ressources

- [Guide de démarrage rapide](quick-start-guide.md)
- [Référence des annotations](annotations-reference.md)
- [Configuration avancée](advanced-configuration.md)
- [Exemples GitHub](https://github.com/tky0065/spring-boot-graphql-autogen/tree/main/graphql-autogen-examples)

---

**💡 Conseil :** Utilisez le plugin Maven pour automatiser la génération de schémas dans vos pipelines CI/CD et garantir la cohérence entre votre code et vos schémas GraphQL !

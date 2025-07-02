# Spring Boot GraphQL Auto-Generator

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.1+-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/GraphQL-Java%2022.1-blue.svg" alt="GraphQL Java">
  <img src="https://img.shields.io/badge/JDK-21+-orange.svg" alt="JDK 21+">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
  <img src="https://img.shields.io/badge/Version-1.0.1-informational" alt="Version">
</div>

<div align="center">
  <h3>🚀 Générez automatiquement vos schémas GraphQL à partir de vos entités JPA et contrôleurs Spring Boot</h3>
</div>

## 📖 À propos

Spring Boot GraphQL Auto-Generator est une bibliothèque qui révolutionne votre développement GraphQL en générant automatiquement les schémas GraphQL à partir de vos entités JPA, DTOs et contrôleurs existants. Avec une configuration minimale et de simples annotations, vous pouvez transformer votre API REST existante en API GraphQL complète et flexible.

## ✨ Pourquoi GraphQL AutoGen ?

**Avant** (GraphQL traditionnel) :
```java
// 1. Définir le schéma manuellement
type Book {
    id: ID!
    title: String!
    author: Author!
}

// 2. Créer les entités
@Entity
public class Book {
    private Long id;
    private String title;
    // ...
}

// 3. Créer les resolvers
@Controller
public class BookController {
    @QueryMapping
    public Book book(@Argument String id) { ... }
}
```

**Après** (GraphQL AutoGen) :
```java
// Juste ajouter des annotations !
@Entity
@GraphQLType
public class Book {
    @GraphQLId
    private Long id;
    
    @GraphQLField
    private String title;
    
    @GraphQLField
    private Author author;
}

@GraphQLController
public class BookController {
    @GraphQLQuery
    public Book book(@GraphQLArgument String id) { ... }
}

// Le schéma est généré automatiquement ! 🎉
```

## 🎯 Fonctionnalités

- ✅ **Génération automatique** de types GraphQL (Object, Input, Enum)
- ✅ **Génération automatique** d'opérations (Query, Mutation, Subscription)
- ✅ **Intégration native** avec Spring Boot et JPA
- ✅ **Support complet** des relations (@OneToMany, @ManyToOne, etc.)
- ✅ **Validation automatique** avec Bean Validation
- ✅ **Performance optimisée** avec DataLoaders automatiques
- ✅ **Configuration zéro** pour les cas simples
- ✅ **Extensibilité** pour les cas complexes

## 📋 Table des matières

- [Installation](#-installation)
- [Guide de démarrage rapide](#-guide-de-démarrage-rapide)
- [Annotations disponibles](#-annotations-disponibles)
- [Configuration](#-configuration)
- [Structure du projet](#-structure-du-projet)
- [Exemples](#-exemples)
- [Guide de développement](#-guide-de-développement)
- [Feuille de route](#-feuille-de-route)
- [Contribuer](#-contribuer)
- [Licence](#-licence)

## 🔧 Installation

### Maven

```xml
<dependency>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.tky0065:graphql-autogen-spring-boot-starter:1.0.1'
```

## 🚀 Guide de démarrage rapide

### 1. Ajouter la dépendance

Intégrez la bibliothèque à votre projet Spring Boot comme indiqué dans la section [Installation](#-installation).

### 2. Annoter vos entités

```java
@Entity
@GType(description = "Livre de la librairie")
public class Book {
    @Id
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Titre du livre")
    private String title;
    
    @ManyToOne
    @GraphQLField
    private Author author;
    
    @GraphQLIgnore // Champ non exposé
    private String internalNotes;
}
```

### 3. Annoter vos contrôleurs

```java
@RestController
@GraphQLController
public class BookController {
    
    @GetMapping("/books/{id}")
    @GraphQLQuery(description = "Obtenir un livre par ID")
    public Book getBook(@PathVariable @GraphQLArgument Long id) {
        return bookService.findById(id);
    }
    
    @PostMapping("/books")
    @GraphQLMutation(description = "Créer un nouveau livre")
    public Book createBook(@RequestBody @GraphQLArgument CreateBookInput input) {
        return bookService.create(input);
    }
}
```

### 4. Configurer l'application (optionnel)

```yaml
# application.yml
spring:
  graphql:
    autogen:
      enabled: true
      base-packages: 
        - "com.example.model"
        - "com.example.controller"
      naming-strategy: "CAMEL_CASE"
```

### 5. Lancer l'application

```bash
mvn spring-boot:run
```

Votre schéma GraphQL est maintenant généré et disponible ! Accédez à l'interface GraphiQL : `http://localhost:8080/graphiql`

## 📋 Annotations disponibles

### Types
- `@GType` - Marque une classe comme type GraphQL
- `@GraphQLInput` - Marque une classe comme type Input
- `@GraphQLEnum` - Marque un enum comme enum GraphQL

### Champs
- `@GraphQLField` - Expose un champ ou méthode
- `@GraphQLId` - Marque un champ comme ID GraphQL
- `@GraphQLIgnore` - Ignore un élément

### Opérations
- `@GraphQLQuery` - Définit une query GraphQL
- `@GraphQLMutation` - Définit une mutation GraphQL
- `@GraphQLSubscription` - Définit une subscription GraphQL

### Configuration
- `@GraphQLController` - Marque un contrôleur GraphQL
- `@GraphQLArgument` - Configure un argument
- `@GraphQLScalar` - Définit un type scalaire personnalisé

## ⚙️ Configuration

```yaml
spring:
  graphql:
    autogen:
      enabled: true                     # Active/désactive l'auto-génération
      base-packages:                    # Packages à scanner
        - "com.example.model"
        - "com.example.controller"
      naming-strategy: "CAMEL_CASE"     # Stratégie de nommage (CAMEL_CASE, SNAKE_CASE)
      generate-inputs: true             # Génère automatiquement les types Input
      type-mapping:                     # Mapping des types Java vers GraphQL
        LocalDateTime: "DateTime"
        BigDecimal: "Decimal"
      data-loader:
        enabled: true                   # Active les DataLoaders pour les relations
        batch-size: 100                 # Taille des lots pour le chargement
      validation:
        enabled: true                   # Active la validation des inputs
```

## 📁 Structure du projet

```
spring-boot-graphql-autogen/
├── graphql-autogen-core/                 # Logique principale
│   ├── src/main/java/
│   │   └── io/github/tky0065/graphql/autogen/
│   │       ├── annotation/              # Annotations GraphQL
│   │       ├── generator/               # Générateurs de schéma
│   │       ├── scanner/                 # Scanner d'annotations
│   │       └── exception/               # Exceptions personnalisées
│   └── src/test/java/                   # Tests unitaires
│
├── graphql-autogen-spring-boot-starter/ # Starter Spring Boot
│   ├── src/main/java/
│   │   └── io/github/tky0065/graphql/autogen/
│   │       ├── autoconfigure/           # Auto-configuration
│   │       └── properties/              # Propriétés de configuration
│   └── src/main/resources/
│       └── META-INF/spring.factories    # Configuration Spring Boot
│
├── graphql-autogen-examples/            # Exemples d'applications
│   ├── blog-example/                    # Exemple de blog
│   └── ecommerce-example/               # Exemple e-commerce
│
├── graphql-autogen-cli/                 # Outil en ligne de commande
│   └── src/main/java/                   # Interface CLI
│
└── graphql-autogen-maven-plugin/        # Plugin Maven
    └── src/main/java/                   # Plugin pour Maven
```

## 🌟 Exemples

Consultez nos exemples fonctionnels dans le répertoire `graphql-autogen-examples/` :

- **Blog Example** : Application simple de blog avec des posts et des commentaires
- **E-commerce Example** : Application e-commerce plus complexe avec produits, catégories, paniers et commandes

## 🛠️ Guide de développement

### Prérequis
- Java 21+
- Maven 3.8+
- Spring Boot 3.3.1+

### Build du projet
```bash
# Build complet
mvn clean install

# Tests uniquement
mvn test

# Skip tests
mvn clean install -DskipTests
```

### Contributions
Nous accueillons les contributions ! Consultez notre [guide de contribution](CONTRIBUTING.md) pour plus d'informations.

## 🚦 Statut du projet

Le projet est activement développé et maintenu. Consultez notre [feuille de route](ROADMAP.md) pour connaître les fonctionnalités à venir.

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👥 Contact et Support

- **GitHub Issues**: Pour les bugs et demandes de fonctionnalités
- **Documentation**: Consultez notre [documentation complète](https://tky0065.github.io/spring-boot-graphql-autogen/)

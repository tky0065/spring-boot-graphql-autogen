# Spring Boot GraphQL Auto-Generator

🚀 **Révolutionnez votre développement GraphQL !** Cette librairie génère automatiquement vos schémas GraphQL à partir de vos entités JPA, DTOs et contrôleurs existants en utilisant de simples annotations.

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
- ✅ **Configuration zero** pour les cas simples
- ✅ **Extensibilité** pour les cas complexes

## 🚀 Démarrage rapide

### 1. Ajouter la dépendance

```xml
<dependency>
    <groupId>com.example.graphql</groupId>
    <artifactId>graphql-autogen-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Annoter vos entités

```java
@Entity
@GraphQLType(description = "Livre de la librairie")
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

### 4. Lancer l'application

```bash
mvn spring-boot:run
```

Le schéma GraphQL est généré automatiquement ! 🎉

Accédez à GraphiQL : `http://localhost:8080/graphiql`

## 📋 Annotations disponibles

### Types
- `@GraphQLType` - Marque une classe comme type GraphQL
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
      enabled: true
      base-packages: 
        - "com.example.model"
        - "com.example.controller"
      naming-strategy: "CAMEL_CASE"
      generate-inputs: true
      type-mapping:
        LocalDateTime: "DateTime"
        BigDecimal: "Decimal"
```

## 📁 Structure du projet

```
spring-boot-graphql-autogen/
├── graphql-autogen-core/                 # Logique principale
│   ├── src/main/java/
│   │   └── com/example/graphql/autogen/
│   │       ├── annotation/              # Annotations GraphQL
│   │       ├── generator/               # Générateurs de schéma
│   │       ├── scanner/                 # Scanner d'annotations
│   │       └── exception/               # Exceptions personnalisées
│   └── src/test/java/                   # Tests unitaires
│
├── graphql-autogen-spring-boot-starter/ # Starter Spring Boot
│   ├── src/main/java/
│   │   └── com/example/graphql/autogen/
│   │       ├── autoconfigure/           # Auto-configuration
│   │       └── properties/              # Propriétés de configuration
│   └── src/main/resources/
│       └── META-INF/spring.factories    # Configuration Spring Boot
│
└── graphql-autogen-examples/            # Exemples d'applications
    ├── simple-library-example/          # Exemple simple
    └── ecommerce-example/               # Exemple complexe
```

## ✅ Tâches terminées

### Phase 1 : Fondations et architecture ✅
- [x] **Structure multi-modules Maven** : Projet complet avec `core`, `starter`, `examples`
- [x] **13 Annotations GraphQL** : Toutes les annotations nécessaires créées
- [x] **Scanner d'annotations** : `DefaultAnnotationScanner` implémenté avec Reflections
- [x] **Tests unitaires** : Tests pour annotations et scanner
- [x] **Architecture de base** : Interfaces et exceptions définies
- [x] **Package EnokDev** : Migration complète vers `com.enokdev.graphql.autogen`

### Phase 2 : Scanner d'annotations ✅
- [x] **Interface AnnotationScanner** : Contrat défini
- [x] **DefaultAnnotationScanner** : Implémentation complète avec Reflections
- [x] **Scan par type** : Méthodes spécifiques pour Types, Inputs, Enums, Controllers
- [x] **Validation des classes** : Filtrage des classes invalides (interfaces, abstraites, etc.)
- [x] **Gestion d'erreurs** : Logging et gestion gracieuse des erreurs
- [x] **Tests complets** : Tests unitaires et d'intégration

### Prochaine étape : Tâche 3 - Générateur de types Object GraphQL

---

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

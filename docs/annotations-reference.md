# 📋 Référence complète des annotations

<div align="center">

**Guide complet de toutes les annotations GraphQL AutoGen**

*16 annotations pour couvrir tous vos besoins GraphQL*

</div>

---

## 📚 Table des matières

- [🏷️ Annotations de Types](#%EF%B8%8F-annotations-de-types)
- [🔍 Annotations de Champs](#-annotations-de-champs)
- [⚡ Annotations d'Opérations](#-annotations-dopérations)
- [🎯 Annotations de Contrôleur](#-annotations-de-contrôleur)
- [📥 Annotations d'Arguments](#-annotations-darguments)
- [🔄 Annotations Avancées](#-annotations-avancées)
- [📖 Conventions et Bonnes Pratiques](#-conventions-et-bonnes-pratiques)

---

## 🏷️ Annotations de Types

### `@GraphQLType`

Marque une classe comme type GraphQL Object.

```java
@GraphQLType(
    name = "Book",                    // Nom du type (optionnel)
    description = "Un livre"          // Description (optionnel)
)
public class Book { ... }
```

**Génère :**
```graphql
"""
Un livre
"""
type Book {
  # champs...
}
```

### `@GraphQLInput`

Marque une classe comme type GraphQL Input.

```java
@GraphQLInput(
    name = "CreateBookInput",         
    description = "Données pour créer un livre"
)
public class CreateBookDto { ... }
```

### `@GraphQLEnum`

Marque une enum comme type GraphQL Enum.

```java
@GraphQLEnum(
    name = "BookStatus",
    description = "Statut d'un livre"
)
public enum BookStatus {
    @GraphQLEnumValue(description = "Disponible") AVAILABLE,
    @GraphQLEnumValue(description = "Emprunté") BORROWED
}
```

### `@GraphQLScalar`

Marque une classe comme type scalaire GraphQL personnalisé.

```java
@GraphQLScalar(
    name = "DateTime",
    javaType = java.time.LocalDateTime.class,
    description = "Représente une date et une heure au format ISO-8601"
)
public class CustomDateTimeScalar {
    // Implémentation du sérialiseur/désérialiseur
}
```

### `@GraphQLInterface`

Marque une interface Java comme interface GraphQL.

```java
@GraphQLInterface(
    name = "Character",
    description = "Un personnage dans l'univers Star Wars"
)
public interface Character {
    String getName();
}
```

### `@GraphQLUnion`

Marque une classe comme union GraphQL.

```java
@GraphQLUnion(
    name = "SearchResult",
    types = {Book.class, Author.class},
    description = "Résultat de recherche pouvant être un livre ou un auteur"
)
public class SearchResultUnion {}
```

### `@GraphQLDescription`

Fournit une description pour un type, un champ ou un argument GraphQL.

```java
@GraphQLDescription("Description détaillée du champ")
private String myField;
```

---

## 🔍 Annotations de Champs

### `@GraphQLField`

Marque un champ ou méthode comme champ GraphQL.

```java
@GraphQLField(
    name = "title",                   
    description = "Titre du livre",  
    nullable = false                  
)
private String title;
```

### `@GraphQLId`

Marque un champ comme identifiant GraphQL.

```java
@GraphQLId
private Long id;
```

### `@GraphQLIgnore`

Exclut un champ de la génération GraphQL.

```java
@GraphQLIgnore(reason = "Champ interne")
private String internalField;
```

---

## ⚡ Annotations d'Opérations

### `@GraphQLQuery`

Marque une méthode comme Query GraphQL.

```java
@GraphQLQuery(name = "book", description = "Récupère un livre")
public Book getBook(@GraphQLArgument(name = "id") Long id) {
    return bookService.findById(id);
}
```

### `@GraphQLMutation`

Marque une méthode comme Mutation GraphQL.

```java
@GraphQLMutation(name = "createBook")
public Book createBook(@GraphQLArgument(name = "input") CreateBookInput input) {
    return bookService.create(input);
}
```

### `@GraphQLSubscription`

Marque une méthode comme Subscription GraphQL.

```java
@GraphQLSubscription(name = "newBook")
public Flux<Book> newBook() {
    return bookPublisher.getNewBookEvents();
}
```

---

## 🎯 Annotations de Contrôleur

### `@GraphQLController`

Marque une classe comme contrôleur GraphQL, indiquant qu'elle contient des méthodes annotées avec `@GraphQLQuery`, `@GraphQLMutation` ou `@GraphQLSubscription`.

```java
@GraphQLController
public class BookController {
    // ... méthodes de requête, mutation, subscription
}
```

---

## 📥 Annotations d'Arguments

### `@GraphQLArgument`

Marque un paramètre de méthode comme argument GraphQL.

```java
public Book getBook(@GraphQLArgument(name = "id", description = "ID du livre") Long id) {
    return bookService.findById(id);
}
```

### `@GParam`

Alias pour `@GraphQLArgument`.

```java
public Book getBook(@GParam("id") Long id) {
    return bookService.findById(id);
}
```

---

## 🔄 Annotations Avancées

### `@GraphQLDataLoader`

Configure un DataLoader pour optimiser les requêtes N+1.

```java
@GraphQLDataLoader(
    name = "authorDataLoader",      
    batchSize = 100,               
    cachingEnabled = true         
)
private Author author;
```

### `@GraphQLPagination`

Configure la pagination pour un champ.

```java
@GraphQLPagination(
    type = GraphQLPagination.PaginationType.RELAY_CURSOR,
    pageSize = 20,
    generateFilters = true
)
private List<Book> books;
```

### `@GraphQLOperation`

Annotation générique pour marquer une méthode comme une opération GraphQL (Query, Mutation, Subscription).

```java
@GraphQLOperation(type = GraphQLOperation.OperationType.QUERY, name = "allBooks")
public List<Book> getAllBooks() {
    return bookService.findAll();
}
```

---

**🎉 Référence complète disponible !**

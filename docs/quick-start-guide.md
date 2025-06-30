# 🚀 Guide de démarrage rapide

<div align="center">

**Créez votre première API GraphQL en 5 minutes avec Spring Boot GraphQL Auto-Generator**

</div>

---

## 📋 Prérequis

- ☑️ **Java 17+** (Java 21 recommandé)
- ☑️ **Spring Boot 3.0+** (3.3.1 recommandé)
- ☑️ **Maven 3.8+** ou **Gradle 7.0+**
- ☑️ **IDE** (IntelliJ IDEA, Eclipse, VS Code)

---

## ⚡ Démarrage ultra-rapide (2 minutes)

### 1️⃣ Ajoutez la dépendance

**Maven :**
```xml
<dependency>
    <groupId>com.enokdev</groupId>
    <artifactId>graphql-autogen-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Gradle :**
```gradle
implementation 'com.enokdev:graphql-autogen-spring-boot-starter:1.0.0-SNAPSHOT'
```

### 2️⃣ Annotez une entité

```java
@Entity
@GraphQLType(name = "Book", description = "Un livre de la bibliothèque")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Titre du livre")
    private String title;
    
    @GraphQLField(description = "Auteur du livre")
    private String author;
    
    @GraphQLField(description = "Prix du livre")
    private Double price;
    
    // Constructeurs, getters, setters...
}
```

### 3️⃣ Démarrez l'application

```java
@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
```

### 4️⃣ Testez votre API

Ouvrez http://localhost:8080/graphiql et testez :

```graphql
query {
  books {
    id
    title
    author
    price
  }
}
```

🎉 **Félicitations !** Vous avez créé votre première API GraphQL !

---

## 🛠️ Utilisation du CLI

Pour générer des schémas GraphQL sans démarrer l'application, vous pouvez utiliser notre outil CLI :

```bash
# Installation
java -jar graphql-autogen-cli.jar --install

# Génération d'un schéma
graphql-autogen --scan-package com.example.model --output schema.graphql
```

---

## 🏗️ Exemple complet (5 minutes)

### Créez un projet Spring Boot

```bash
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,h2 \
  -d javaVersion=21 \
  -d bootVersion=3.3.1 \
  -d name=graphql-library \
  -d packageName=com.example.library \
  -o library.zip && unzip library.zip
```

### Ajoutez les entités

**Book.java :**
```java
package com.example.library.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;

@Entity
@Table(name = "books")
@GraphQLType(name = "Book", description = "Un livre de la bibliothèque")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;
    
    /**
     * Le titre du livre.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Titre du livre")
    private String title;
    
    /**
     * L'ISBN du livre.
     */
    @Column(unique = true)
    @GraphQLField(description = "Code ISBN")
    private String isbn;
    
    /**
     * Le prix du livre en euros.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Prix en euros")
    private Double price;
    
    /**
     * L'auteur du livre.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @GraphQLField(description = "Auteur du livre")
    @GraphQLDataLoader(batchSize = 50, cachingEnabled = true)
    private Author author;
    
    /**
     * Le statut du livre.
     */
    @Enumerated(EnumType.STRING)
    @GraphQLField(description = "Statut de disponibilité")
    private BookStatus status;
    
    // Constructeurs
    public Book() {}
    
    public Book(String title, String isbn, Double price) {
        this.title = title;
        this.isbn = isbn;
        this.price = price;
        this.status = BookStatus.AVAILABLE;
    }
    
    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}
```

**Author.java :**
```java
package com.example.library.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "authors")
@GraphQLType(name = "Author", description = "Un auteur de livres")
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Nom de l'auteur")
    private String name;
    
    @GraphQLField(description = "Biographie de l'auteur")
    private String biography;
    
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @GraphQLField(description = "Livres de cet auteur")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.RELAY_CURSOR,
        pageSize = 10,
        generateSorting = true
    )
    private List<Book> books;
    
    // Constructeurs, getters, setters...
    public Author() {}
    
    public Author(String name, String biography) {
        this.name = name;
        this.biography = biography;
    }
    
    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
```

**BookStatus.java :**
```java
package com.example.library.model;

import com.enokdev.graphql.autogen.annotation.*;

@GraphQLEnum(name = "BookStatus", description = "Statut de disponibilité d'un livre")
public enum BookStatus {
    
    @GraphQLEnumValue(description = "Livre disponible à l'emprunt")
    AVAILABLE,
    
    @GraphQLEnumValue(description = "Livre actuellement emprunté")
    BORROWED,
    
    @GraphQLEnumValue(description = "Livre en maintenance")
    MAINTENANCE,
    
    @GraphQLEnumValue(description = "Livre retiré de la circulation")
    RETIRED
}
```

### Ajoutez les DTOs d'entrée

**CreateBookInput.java :**
```java
package com.example.library.dto;

import com.enokdev.graphql.autogen.annotation.*;

@GraphQLInput(name = "CreateBookInput", description = "Données pour créer un nouveau livre")
public class CreateBookInput {
    
    @GraphQLInputField(name = "title", required = true, description = "Titre du livre")
    private String title;
    
    @GraphQLInputField(name = "isbn", required = true, description = "Code ISBN")
    private String isbn;
    
    @GraphQLInputField(name = "price", required = true, description = "Prix du livre")
    private Double price;
    
    @GraphQLInputField(name = "authorId", required = true, description = "ID de l'auteur")
    private Long authorId;
    
    // Constructeurs, getters, setters...
    public CreateBookInput() {}
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
```

### Configurez l'application

**application.yml :**
```yaml
spring:
  application:
    name: library-graphql-api
  
  # Configuration GraphQL AutoGen
  graphql:
    autogen:
      enabled: true
      base-packages:
        - "com.example.library.model"
        - "com.example.library.dto"
      generate-inputs: true
      generate-data-loaders: true
      generate-pagination: true
      schema-location: "classpath:graphql/"
      naming-strategy: CAMEL_CASE
  
  # Base de données H2 pour les tests
  datasource:
    url: jdbc:h2:mem:library
    username: sa
    password: 
  
  h2:
    console:
      enabled: true
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  
  # Configuration GraphQL
  graphql:
    graphiql:
      enabled: true
      path: /graphiql

logging:
  level:
    com.enokdev.graphql.autogen: DEBUG
```

### Ajoutez des données de test

**DataLoader.java :**
```java
package com.example.library.config;

import com.example.library.model.*;
import com.example.library.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    
    public DataLoader(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }
    
    @Override
    public void run(String... args) {
        // Créer des auteurs
        Author hugo = new Author("Victor Hugo", "Écrivain français du 19ème siècle");
        Author shakespeare = new Author("William Shakespeare", "Dramaturge anglais");
        
        authorRepository.save(hugo);
        authorRepository.save(shakespeare);
        
        // Créer des livres
        Book miserables = new Book("Les Misérables", "978-2-07-040096-5", 12.50);
        miserables.setAuthor(hugo);
        miserables.setStatus(BookStatus.AVAILABLE);
        
        Book hamlet = new Book("Hamlet", "978-0-14-143724-4", 9.99);
        hamlet.setAuthor(shakespeare);
        hamlet.setStatus(BookStatus.AVAILABLE);
        
        Book macbeth = new Book("Macbeth", "978-0-14-143725-1", 8.50);
        macbeth.setAuthor(shakespeare);
        macbeth.setStatus(BookStatus.BORROWED);
        
        bookRepository.save(miserables);
        bookRepository.save(hamlet);
        bookRepository.save(macbeth);
        
        System.out.println("✅ Données de test chargées !");
    }
}
```

### Démarrez et testez

```bash
mvn spring-boot:run
```

Ouvrez http://localhost:8080/graphiql et testez ces requêtes :

**Lister tous les livres :**
```graphql
query {
  books {
    id
    title
    isbn
    price
    status
    author {
      id
      name
      biography
    }
  }
}
```

**Rechercher un livre par ID :**
```graphql
query {
  book(id: "1") {
    title
    price
    author {
      name
    }
  }
}
```

**Pagination avec un auteur :**
```graphql
query {
  author(id: "2") {
    name
    books(first: 2) {
      edges {
        node {
          title
          price
        }
        cursor
      }
      pageInfo {
        hasNextPage
        endCursor
      }
    }
  }
}
```

---

## 🎯 Résultat

Vous avez maintenant une API GraphQL complète avec :

- ✅ **Types automatiques** : Book, Author, BookStatus
- ✅ **Inputs automatiques** : CreateBookInput  
- ✅ **Pagination Relay** : avec cursors et PageInfo
- ✅ **DataLoaders** : optimisation automatique N+1
- ✅ **Documentation** : descriptions depuis JavaDoc
- ✅ **GraphiQL** : interface de test intégrée

---

## 🚀 Prochaines étapes

1. **Ajoutez des mutations** → [Guide des mutations](./mutations-guide.md)
2. **Configurez la sécurité** → [Guide Spring Security](./security-guide.md)
3. **Optimisez les performances** → [Guide des DataLoaders](./dataloaders-guide.md)
4. **Déployez en production** → [Guide de déploiement](./deployment-guide.md)

---

## 💡 Conseils rapides

### ⚡ Configuration minimale
Pour démarrer rapidement, vous n'avez besoin que de :
```yaml
spring:
  graphql:
    autogen:
      enabled: true
      base-packages: ["com.example.model"]
```

### 🏷️ Conventions de nommage
- **Types :** `@GraphQLType(name = "Book")` → `type Book`
- **Inputs :** `@GraphQLInput(name = "CreateBookInput")` → `input CreateBookInput`
- **Enums :** `@GraphQLEnum(name = "BookStatus")` → `enum BookStatus`

### 📝 Documentation automatique
Utilisez les commentaires JavaDoc :
```java
/**
 * Le titre principal du livre.
 */
@GraphQLField(description = "Titre du livre")
private String title;
```

### 🔄 Rechargement automatique
En développement, le schéma se recharge automatiquement au redémarrage.

---

**🎉 Félicitations ! Vous maîtrisez maintenant les bases de GraphQL AutoGen !**

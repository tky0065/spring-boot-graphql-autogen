# 🚀 Guide de Démarrage Rapide

<div align="center">

**Créez votre première API GraphQL en 5 minutes avec Spring Boot GraphQL Auto-Generator.**

</div>

---

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir les outils suivants installés :

- ☑️ **Java 17+** (Java 21 est recommandé)
- ☑️ **Maven 3.8+** ou **Gradle 7.0+**
- ☑️ Un **IDE** de votre choix (IntelliJ IDEA, Eclipse, VS Code)

---

## ⚡ Étape 1 : Créer un Nouveau Projet Spring Boot

Le moyen le plus simple de commencer est de créer un nouveau projet Spring Boot à l'aide de [Spring Initializr](https://start.spring.io/).

1.  Allez sur [start.spring.io](https://start.spring.io/).
2.  Sélectionnez les dépendances suivantes :
    *   **Spring Web**
    *   **Spring Data JPA**
    *   **H2 Database**
3.  Cliquez sur **Generate** pour télécharger le projet.

---

## 📦 Étape 2 : Ajouter la Dépendance GraphQL AutoGen

Ouvrez le fichier `pom.xml` de votre projet et ajoutez la dépendance suivante :

```xml
<dependency>
    <groupId>com.enokdev.graphql</groupId>
    <artifactId>graphql-autogen-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

---

## ✍️ Étape 3 : Annoter Vos Entités

Maintenant, créons une entité `Book` et annotons-la pour la génération de schéma GraphQL.

Créez un fichier `Book.java` dans votre projet :

```java
package com.example.demo;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    // Getters et Setters
}
```

---

## ⚙️ Étape 4 : Configurer l'Application

Ouvrez votre fichier `application.properties` et ajoutez la configuration suivante pour activer la génération de schéma et configurer la base de données H2 :

```properties
# Activer la génération de schéma GraphQL
spring.graphql.autogen.enabled=true
spring.graphql.autogen.base-packages=com.example.demo

# Configurer la base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Activer la console H2
spring.h2.console.enabled=true
```

---

## ▶️ Étape 5 : Démarrer l'Application

Vous êtes maintenant prêt à démarrer votre application. Exécutez la classe principale de votre application Spring Boot.

Une fois l'application démarrée, vous devriez voir dans les logs que le schéma GraphQL a été généré.

---

## 🧪 Étape 6 : Tester Votre API GraphQL

Ouvrez votre navigateur et allez à l'adresse `http://localhost:8080/graphiql`. Vous devriez voir l'interface GraphiQL, qui vous permet de tester votre API GraphQL.

Exécutez la requête suivante pour lister tous les livres :

```graphql
query {
  books {
    id
    title
    author
  }
}
```

🎉 **Félicitations !** Vous avez créé et testé avec succès votre première API GraphQL avec Spring Boot GraphQL Auto-Generator.

---

## 🚀 Prochaines Étapes

Maintenant que vous avez les bases, vous pouvez explorer des fonctionnalités plus avancées :

-   [Créer des Mutations pour modifier des données](./mutations-guide.md)
-   [Utiliser des DataLoaders pour optimiser les performances](./dataloaders-guide.md)
-   [Configurer la pagination pour les listes](./pagination-guide.md)
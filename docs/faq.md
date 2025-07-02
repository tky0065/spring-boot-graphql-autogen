# ❓ Foire Aux Questions (FAQ)

Cette section répond aux questions fréquemment posées et fournit des solutions aux problèmes courants que vous pourriez rencontrer en utilisant le starter Spring Boot GraphQL Auto-Generator.

---

### Q1 : J'ai une erreur `NoUniqueBeanDefinitionException` au démarrage de l'application. Que se passe-t-il ?

**R :** Cette erreur se produit généralement lorsque vous définissez manuellement un bean qui est également créé par la configuration automatique. Par exemple, si vous avez un bean `GraphQLAutoGenProperties` dans votre configuration de test et que vous activez également `@EnableConfigurationProperties(GraphQLAutoGenProperties.class)`.

**Solution :** Supprimez la définition manuelle du bean et laissez la configuration automatique le gérer.

---

### Q2 : Mon schéma n'est pas généré au démarrage. Comment puis-je déboguer cela ?

**R :** Voici quelques points à vérifier :

1.  **Vérifiez que la dépendance est correctement ajoutée** à votre `pom.xml` ou `build.gradle`.
2.  **Assurez-vous que la propriété `spring.graphql.autogen.enabled` est à `true`** (c'est la valeur par défaut).
3.  **Vérifiez que vos paquets de base sont correctement configurés** dans la propriété `spring.graphql.autogen.base-packages`. Si elle est vide, assurez-vous que vos entités et contrôleurs se trouvent dans le même paquet que votre classe d'application principale.
4.  **Activez le logging de débogage** pour le paquet `com.enokdev.graphql.autogen` pour voir des informations détaillées sur le processus de génération de schéma :
    ```yaml
    logging:
      level:
        com.enokdev.graphql.autogen: DEBUG
    ```

---

### Q3 : Comment puis-je utiliser un type scalaire personnalisé (par exemple, `UUID`) ?

**R :** Vous pouvez mapper des types Java à des scalaires GraphQL personnalisés à l'aide de la propriété `type-mapping`.

1.  **Configurez le mappage dans votre `application.yml`** :
    ```yaml
    spring:
      graphql:
        autogen:
          type-mapping:
            java.util.UUID: "UUID"
    ```
2.  **Créez un bean `GraphQLScalarType`** pour définir le comportement de votre scalaire personnalisé :
    ```java
    @Configuration
    public class GraphQLConfig {

        @Bean
        public GraphQLScalarType uuidScalar() {
            return GraphQLScalarType.newScalar()
                    .name("UUID")
                    .description("A custom UUID scalar")
                    .coercing(new Coercing<UUID, String>() {
                        // Implémentez ici la logique de conversion
                    })
                    .build();
        }
    }
    ```

---

### Q4 : Puis-je exclure certains champs de la génération de schéma ?

**R :** Oui, vous pouvez utiliser l'annotation `@GraphQLIgnore` sur n'importe quel champ ou méthode que vous ne souhaitez pas exposer dans votre schéma GraphQL.

```java
@Entity
@GraphQLType
public class User {

    @GraphQLField
    private String username;

    @GraphQLIgnore
    private String password;
}
```

---

### Q5 : Comment gérer les relations complexes (OneToMany, ManyToOne) ?

**R :** GraphQL AutoGen gère automatiquement les relations JPA. Pour optimiser les requêtes N+1, utilisez l'annotation `@GraphQLDataLoader` sur le champ de la relation.

```java
@Entity
@GraphQLType
public class Post {
    // ...
    @ManyToOne
    @GraphQLField
    @GraphQLDataLoader(batchSize = 100) // Active le DataLoader pour cette relation
    private Author author;
}
```

---

### Q6 : Comment configurer la pagination pour mes listes ?

**R :** Utilisez l'annotation `@GraphQLPagination` sur les champs de type `List` ou `Set`.

```java
@Entity
@GraphQLType
public class Author {
    // ...
    @OneToMany(mappedBy = "author")
    @GraphQLField
    @GraphQLPagination(type = GraphQLPagination.PaginationType.OFFSET_BASED, pageSize = 10) // Pagination par offset
    private List<Post> posts;
}
```

---

### Q7 : Mon application ne démarre pas avec une erreur liée à `graphql-java` ou `spring-graphql`. Que faire ?

**R :** Assurez-vous que les versions de `graphql-java` et `spring-graphql` sont compatibles avec votre version de Spring Boot. Vérifiez les dépendances transitives ou utilisez les versions gérées par Spring Boot si possible. Le starter GraphQL AutoGen est testé avec les versions de Spring Boot 2.7.x et 3.x.

---

### Q8 : Comment puis-je personnaliser les noms des types ou des champs GraphQL ?

**R :** Utilisez les attributs `name` dans les annotations respectives (`@GraphQLType(name = "MonType")`, `@GraphQLField(name = "monChamp")`, etc.). Pour une stratégie de nommage globale, configurez la propriété `spring.graphql.autogen.naming-strategy` dans `application.yml`.

```yaml
spring:
  graphql:
    autogen:
      naming-strategy: SNAKE_CASE # ou CAMEL_CASE, PASCAL_CASE, UNCHANGED
```

---

### Q9 : J'ai des conflits de types ou des erreurs de schéma. Comment les résoudre ?

**R :** Les conflits de types peuvent survenir lorsque deux classes Java différentes génèrent le même nom de type GraphQL, ou lorsque des types sont définis de manière incohérente. Consultez le [Guide de résolution des conflits de types](./troubleshooting-type-conflicts.md) pour des solutions détaillées.

---

### Q10 : Comment puis-je générer le schéma GraphQL au moment du build (sans démarrer l'application) ?

**R :** Utilisez le plugin Maven ou Gradle dédié. Référez-vous à la [Documentation des plugins Maven/Gradle](./maven-plugin-guide.md) pour plus de détails.

---

### Q11 : Est-il possible d'intégrer GraphQL AutoGen avec Spring Security ?

**R :** Oui, bien que GraphQL AutoGen ne fournisse pas d'intégration de sécurité prête à l'emploi, vous pouvez utiliser les mécanismes de sécurité de Spring Security (comme `@PreAuthorize`) sur vos méthodes de contrôleur GraphQL. Pour une autorisation au niveau des champs, vous devrez implémenter une logique personnalisée en utilisant les capacités de `graphql-java`.

---

### Q12 : Comment puis-je ajouter des descriptions à mes types et champs GraphQL ?

**R :** GraphQL AutoGen extrait automatiquement les descriptions des commentaires Javadoc de vos classes, champs et méthodes. Vous pouvez également utiliser l'attribut `description` dans la plupart des annotations GraphQL AutoGen (`@GraphQLType(description = "...")`, `@GraphQLField(description = "...")`, etc.).

---

### Q13 : Comment gérer les erreurs de validation (par exemple, `@NotNull`, `@Size`) ?

**R :** GraphQL AutoGen s'intègre avec Bean Validation. Si vous utilisez des annotations de validation sur vos entités ou DTOs, le schéma GraphQL généré inclura les directives `@constraint` correspondantes. Les erreurs de validation seront gérées par le mécanisme de validation de Spring GraphQL.

---

### Q14 : Puis-je utiliser GraphQL AutoGen avec Spring WebFlux (applications réactives) ?

**R :** Oui, GraphQL AutoGen est compatible avec Spring WebFlux. Vous pouvez utiliser des types de retour réactifs comme `Mono` et `Flux` dans vos méthodes de contrôleur GraphQL. Les subscriptions nécessitent généralement une intégration avec un broker de messages (comme Reactor Netty ou WebSockets) pour une gestion complète des événements en temps réel.

---

### Q15 : Comment puis-je contribuer au projet ?

**R :** Nous sommes ravis que vous souhaitiez contribuer ! Veuillez consulter notre [Guide de Contribution](./CONTRIBUTING.md) sur GitHub pour savoir comment soumettre des rapports de bugs, des demandes de fonctionnalités ou des pull requests.

---

**🎉 Vous avez des questions ? N'hésitez pas à ouvrir une issue sur GitHub !**
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
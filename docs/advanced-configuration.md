# 🔧 Configuration Avancée

Le starter Spring Boot GraphQL Auto-Generator est conçu pour être hautement extensible. Vous pouvez facilement remplacer n'importe quel bean fourni par la configuration automatique pour personnaliser le comportement de la génération de schéma.

---

## Remplacement de Beans

Pour remplacer un bean, il suffit de déclarer votre propre bean du même type dans votre configuration. Spring Boot utilisera votre bean à la place de celui par défaut.

Voici un exemple de remplacement du `TypeResolver` par défaut pour ajouter un mappage de type personnalisé par programme :

```java
@Configuration
public class CustomGraphQLConfig {

    @Bean
    @Primary
    public TypeResolver customTypeResolver() {
        DefaultTypeResolver typeResolver = new DefaultTypeResolver();
        typeResolver.registerTypeMapping(java.time.Year.class, "Year");
        return typeResolver;
    }
}
```

Dans cet exemple, nous créons un `DefaultTypeResolver` et y ajoutons un mappage de type personnalisé pour `java.time.Year`. L'annotation `@Primary` garantit que notre bean personnalisé est prioritaire sur celui par défaut.

---

## Beans Remplaçables

Voici une liste des beans que vous pouvez remplacer pour personnaliser la génération de schéma :

-   **`AnnotationScanner`** : Implémentez votre propre logique de scan pour découvrir les classes annotées.
-   **`TypeResolver`** : Personnalisez la manière dont les types Java sont résolus en types GraphQL.
-   **`FieldResolver`** : Modifiez la manière dont les champs des types GraphQL sont résolus.
-   **`OperationResolver`** : Personnalisez la découverte des opérations de query, mutation et subscription.
-   **`SchemaGenerator`** : Remplacez l'ensemble du processus de génération de schéma.
-   **`GraphQLSchemaGenerationService`** : Prenez le contrôle total du service de génération de schéma.

---

## Exemple : Remplacer le `SchemaGenerator`

Voici un exemple plus avancé qui remplace le `SchemaGenerator` par défaut pour ajouter un en-tête personnalisé au schéma généré :

```java
@Configuration
public class CustomSchemaGeneratorConfig {

    @Bean
    @Primary
    public SchemaGenerator customSchemaGenerator(
            TypeResolver typeResolver,
            FieldResolver fieldResolver,
            OperationResolver operationResolver,
            AnnotationScanner annotationScanner) {
        return new CustomSchemaGenerator(typeResolver, fieldResolver, operationResolver, annotationScanner);
    }

    private static class CustomSchemaGenerator extends DefaultSchemaGenerator {

        public CustomSchemaGenerator(TypeResolver typeResolver, FieldResolver fieldResolver, OperationResolver operationResolver, AnnotationScanner annotationScanner) {
            super(typeResolver, fieldResolver, operationResolver, annotationScanner);
        }

        @Override
        public String generateSchemaString(List<Class<?>> annotatedClasses) {
            String schema = super.generateSchemaString(annotatedClasses);
            return "# Schéma généré sur mesure\n\n" + schema;
        }
    }
}
```
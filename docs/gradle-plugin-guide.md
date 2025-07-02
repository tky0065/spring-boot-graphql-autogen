# 📦 Documentation du Plugin Gradle

<div align="center">

**Générez votre schéma GraphQL au moment du build avec le plugin Gradle.**

</div>

---

## 🎯 Vue d'ensemble

Le plugin Gradle de GraphQL AutoGen vous permet d'intégrer la génération de schéma GraphQL directement dans le cycle de vie de votre build Gradle. Cela garantit que votre schéma est toujours à jour avec votre code source.

---

## 🚀 Utilisation

### Étape 1 : Ajout du plugin au `build.gradle`

Ajoutez le plugin suivant à votre fichier `build.gradle` (pour Kotlin DSL, utilisez `build.gradle.kts`) :

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.x.x' // ou votre version
    id 'io.github.tky0065.graphql-autogen' version '1.0.0-SNAPSHOT' // Utilisez la version actuelle
}

// ... autres configurations

graphqlAutoGen {
    basePackages = ["com.example.demo"]
    schemaLocation = "${buildDir}/generated-resources/graphql"
    schemaFileName = "schema.graphqls"
    // Autres configurations optionnelles
}
```

### Étape 2 : Configuration du plugin

Le plugin offre plusieurs options de configuration via le bloc `graphqlAutoGen` :

-   **`basePackages`** (obligatoire) : Liste des packages à scanner pour les annotations GraphQL. Exemple : `['com.example.model', 'com.example.controller']`.
-   **`schemaLocation`** (optionnel) : Répertoire de sortie pour le fichier de schéma généré. Par défaut : `${buildDir}/generated-resources/graphql`.
-   **`schemaFileName`** (optionnel) : Nom du fichier de schéma généré. Par défaut : `schema.graphqls`.
-   **`enabled`** (optionnel) : Active ou désactive le plugin. Par défaut : `true`.
-   **`generateInputs`** (optionnel) : Génère automatiquement les types Input. Par défaut : `true`.
-   **`generateSubscriptions`** (optionnel) : Génère les types Subscription. Par défaut : `true`.
-   **`typeMapping`** (optionnel) : Mappages personnalisés de types Java vers des scalaires GraphQL.
    ```gradle
    typeMapping = [
        "java.time.LocalDateTime": "DateTime"
    ]
    ```

### Étape 3 : Exécution du plugin

Le plugin ajoute une tâche `generateGraphQLSchema` que vous pouvez exécuter directement :

```bash
gradle generateGraphQLSchema
```

Ou, il est automatiquement exécuté lors des tâches de build standard comme `build` ou `assemble`.

Après l'exécution, le fichier `schema.graphqls` sera généré dans le répertoire spécifié par `schemaLocation`.

---

## 💡 Bonnes Pratiques

-   **Intégration CI/CD** : Utilisez le plugin dans votre pipeline CI/CD pour garantir que votre schéma est toujours à jour.
-   **Versionnement du schéma** : Envisagez de versionner votre fichier `schema.graphqls` dans votre dépôt Git pour suivre les changements.
-   **Validation** : Utilisez des outils de validation de schéma GraphQL pour vérifier la conformité de votre schéma généré.

---

**🎉 Votre schéma GraphQL est maintenant généré automatiquement par Gradle !**

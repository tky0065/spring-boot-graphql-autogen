# 📦 Documentation du Plugin Maven

<div align="center">

**Générez votre schéma GraphQL au moment du build avec le plugin Maven.**

</div>

---

## 🎯 Vue d'ensemble

Le plugin Maven de GraphQL AutoGen vous permet d'intégrer la génération de schéma GraphQL directement dans le cycle de vie de votre build Maven. Cela garantit que votre schéma est toujours à jour avec votre code source.

---

## 🚀 Utilisation

### Étape 1 : Ajout du plugin au `pom.xml`

Ajoutez le plugin suivant à la section `<build><plugins>` de votre `pom.xml` :

```xml
<plugin>
    <groupId>io.github.tky0065</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version> <!-- Utilisez la version actuelle -->
    <executions>
        <execution>
            <goals>
                <goal>generate-schema</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- Configurations spécifiques du plugin -->
        <basePackages>
            <param>com.example.demo</param>
        </basePackages>
        <schemaLocation>${project.build.directory}/generated-resources/graphql</schemaLocation>
        <schemaFileName>schema.graphqls</schemaFileName>
    </configuration>
</plugin>
```

### Étape 2 : Configuration du plugin

Le plugin offre plusieurs options de configuration :

-   **`<basePackages>`** (obligatoire) : Liste des packages à scanner pour les annotations GraphQL. Exemple : `com.example.model`, `com.example.controller`.
-   **`<schemaLocation>`** (optionnel) : Répertoire de sortie pour le fichier de schéma généré. Par défaut : `${project.build.directory}/generated-resources/graphql`.
-   **`<schemaFileName>`** (optionnel) : Nom du fichier de schéma généré. Par défaut : `schema.graphqls`.
-   **`<enabled>`** (optionnel) : Active ou désactive le plugin. Par défaut : `true`.
-   **`<generateInputs>`** (optionnel) : Génère automatiquement les types Input. Par défaut : `true`.
-   **`<generateSubscriptions>`** (optionnel) : Génère les types Subscription. Par défaut : `true`.
-   **`<typeMapping>`** (optionnel) : Mappages personnalisés de types Java vers des scalaires GraphQL.
    ```xml
    <typeMapping>
        <entry>
            <key>java.time.LocalDateTime</key>
            <value>DateTime</value>
        </entry>
    </typeMapping>
    ```

### Étape 3 : Exécution du plugin

Le goal `generate-schema` est automatiquement lié à la phase `generate-resources` du cycle de vie Maven. Vous pouvez donc l'exécuter en lançant une commande Maven standard :

```bash
mvn clean install
```

Après l'exécution, le fichier `schema.graphqls` sera généré dans le répertoire spécifié par `<schemaLocation>`.

---

## 💡 Bonnes Pratiques

-   **Intégration CI/CD** : Utilisez le plugin dans votre pipeline CI/CD pour garantir que votre schéma est toujours à jour.
-   **Versionnement du schéma** : Envisagez de versionner votre fichier `schema.graphqls` dans votre dépôt Git pour suivre les changements.
-   **Validation** : Utilisez des outils de validation de schéma GraphQL pour vérifier la conformité de votre schéma généré.

---

**🎉 Votre schéma GraphQL est maintenant généré automatiquement par Maven !**

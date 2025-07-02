# ⚙️ Référence de Configuration

Ce guide de référence détaille toutes les propriétés de configuration disponibles pour le starter Spring Boot GraphQL Auto-Generator. Vous pouvez configurer ces propriétés dans votre fichier `application.yml` ou `application.properties` sous le préfixe `spring.graphql.autogen`.

---

## Propriétés Générales

### `enabled`

-   **Description** : Active ou désactive la génération automatique de schéma GraphQL.
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          enabled: false
    ```

---

## Configuration du Scan

### `base-packages`

-   **Description** : Liste des paquets de base à scanner pour les annotations GraphQL. Si cette liste est vide, le paquet de votre classe d'application principale sera utilisé.
-   **Type** : `List<String>`
-   **Valeur par défaut** : `[]` (liste vide)
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          base-packages:
            - "com.example.model"
            - "com.example.controller"
    ```

### `exclude-packages`

-   **Description** : Liste des paquets à exclure du scan. Utile pour ignorer les paquets de test ou les bibliothèques tierces.
-   **Type** : `List<String>`
-   **Valeur par défaut** : `[]` (liste vide)
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          exclude-packages:
            - "com.example.test"
    ```

---

## Configuration de la Génération

### `schema-location`

-   **Description** : Emplacement où le fichier `schema.graphqls` généré doit être écrit.
-   **Type** : `String`
-   **Valeur par défaut** : `classpath:graphql/`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          schema-location: "file:./target/graphql/"
    ```

### `naming-strategy`

-   **Description** : Stratégie de nommage à utiliser pour les types et les champs GraphQL.
-   **Type** : `Enum` (`CAMEL_CASE`, `PASCAL_CASE`, `SNAKE_CASE`)
-   **Valeur par défaut** : `CAMEL_CASE`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          naming-strategy: SNAKE_CASE
    ```

### `generate-inputs`

-   **Description** : Active ou désactive la génération automatique des types d'entrée (par exemple, `CreateBookInput`, `UpdateBookInput`).
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          generate-inputs: false
    ```

### `generate-payloads`

-   **Description** : Active ou désactive la génération automatique des types de payload pour les mutations.
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          generate-payloads: false
    ```

### `type-mapping`

-   **Description** : Mappages personnalisés des types Java vers les noms de scalaires GraphQL.
-   **Type** : `Map<String, String>`
-   **Valeur par défaut** : `{}` (map vide)
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          type-mapping:
            java.time.OffsetDateTime: "DateTime"
            java.math.BigInteger: "BigInt"
    ```

### `include-java-doc`

-   **Description** : Inclut les commentaires JavaDoc comme descriptions GraphQL.
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          include-java-doc: false
    ```

### `format-schema`

-   **Description** : Formate le fichier `schema.graphqls` généré pour une meilleure lisibilité.
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          format-schema: false
    ```

### `sort-schema`

-   **Description** : Trie les types et les champs par ordre alphabétique dans le schéma généré.
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          sort-schema: false
    ```

### `generation-mode`

-   **Description** : Mode de génération du schéma.
    -   `STARTUP` : Génère le schéma au démarrage de l'application.
    -   `BUILD_TIME` : Génère le schéma pendant la phase de construction (nécessite un plugin Maven ou Gradle).
-   **Type** : `Enum` (`STARTUP`, `BUILD_TIME`)
-   **Valeur par défaut** : `STARTUP`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          generation-mode: BUILD_TIME
    ```

### `validate-schema`

-   **Description** : Valide la cohérence du schéma généré.
-   **Type** : `boolean`
-   **Valeur par défaut** : `true`
-   **Exemple** :
    ```yaml
    spring:
      graphql:
        autogen:
          validate-schema: false
    ```

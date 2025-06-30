---
name: 🐛 Bug Report
about: Créer un rapport de bug pour nous aider à améliorer
title: '[BUG] '
labels: 'bug'
assignees: ''
---

## 🐛 Description du bug

Une description claire et concise de ce qui ne fonctionne pas.

## 🔄 Étapes pour reproduire

Étapes pour reproduire le comportement :

1. Ajoutez la dépendance '...'
2. Configurez '...'
3. Exécutez '...'
4. Voir l'erreur

## ✅ Comportement attendu

Une description claire et concise de ce que vous vous attendiez à voir se produire.

## ❌ Comportement actuel

Une description claire et concise de ce qui se passe actuellement.

## 📱 Environnement

**Versions :**
- OS : [e.g. Windows 11, macOS 13.0, Ubuntu 22.04]
- Java : [e.g. OpenJDK 21.0.1]
- Spring Boot : [e.g. 3.3.1]
- GraphQL AutoGen : [e.g. 1.0.0]
- Build tool : [e.g. Maven 3.9.4, Gradle 8.3]

**Configuration :**
```yaml
# Votre configuration application.yml/properties pertinente
spring:
  graphql:
    autogen:
      enabled: true
      # ...
```

## 📎 Logs d'erreur

```
Collez ici les logs d'erreur complets ou stack traces
```

## 💻 Code d'exemple

```java
// Code minimal pour reproduire le problème
@Entity
@GraphQLType
public class ExampleEntity {
    // ...
}

@RestController
@GraphQLController
public class ExampleController {
    // ...
}
```

## 📄 Fichiers générés

Si applicable, ajoutez le contenu du fichier schema.graphqls généré :

```graphql
# Schéma GraphQL généré (si applicable)
type Example {
    # ...
}
```

## 💡 Solutions tentées

Décrivez brièvement ce que vous avez déjà essayé pour résoudre le problème.

## 📸 Captures d'écran

Si applicable, ajoutez des captures d'écran pour aider à expliquer votre problème.

## 🔗 Contexte supplémentaire

Ajoutez tout autre contexte sur le problème ici. Par exemple :
- Cela fonctionnait-il dans une version précédente ?
- Y a-t-il des conditions spécifiques qui déclenchent le bug ?
- Avez-vous des configurations particulières ?

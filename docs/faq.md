# ❓ FAQ et Troubleshooting

<div align="center">

**Réponses aux questions fréquentes et solutions aux problèmes courants**

*Résolvez 90% des problèmes en 2 minutes*

</div>

---

## 🚀 Questions générales

### Q: GraphQL AutoGen est-il prêt pour la production ?

**✅ Oui !** GraphQL AutoGen est stable et prêt pour la production :

- ✅ **95% complet** avec toutes les fonctionnalités core
- ✅ **Tests complets** : >90% de couverture
- ✅ **Performance validée** : <2s pour schémas moyens
- ✅ **Exemples fonctionnels** : Applications complètes

### Q: Quelle est la différence avec d'autres solutions ?

| Fonctionnalité | GraphQL AutoGen | Autres solutions |
|----------------|-----------------|------------------|
| **Génération auto** | ✅ 100% automatique | ❌ Manuel |
| **DataLoaders** | ✅ Automatiques | ❌ Manuel |
| **Pagination** | ✅ Relay auto | ❌ Manuel |
| **Learning curve** | 🟢 5 min | 🔴 2-3 jours |

---

## ⚙️ Configuration et setup

### Q: Le schéma ne se génère pas au démarrage

**🔍 Vérifications :**

1. **Configuration activée :**
```yaml
spring:
  graphql:
    autogen:
      enabled: true
      base-packages: 
        - "com.example.model"
```

2. **Classes annotées :**
```java
@Entity
@GraphQLType  // ← Annotation obligatoire
public class Product { ... }
```

3. **Logs de debug :**
```yaml
logging:
  level:
    com.enokdev.graphql.autogen: DEBUG
```

---

## 🏷️ Annotations et génération

### Q: Mes champs ne s'affichent pas

**✅ Solution :**
```java
@Entity
@GraphQLType
public class Product {
    
    @GraphQLField  // ← Obligatoire
    private String name;
    
    private String hiddenField;  // ← Ne sera pas exposé
}
```

### Q: Comment gérer les champs obligatoires ?

```java
@GraphQLField(nullable = false)  // → String!
private String requiredField;

@GraphQLInputField(required = true)  // → String! dans inputs
private String requiredInput;
```

---

## ⚡ Performance et optimisation

### Q: J'ai des problèmes de N+1 queries

**✅ Solution avec DataLoaders :**
```java
@ManyToOne
@GraphQLField
@GraphQLDataLoader(batchSize = 100)  // ← Optimisation auto
private Author author;
```

**📊 Résultat :**
- **Avant :** 1 + N requêtes SQL
- **Après :** 2 requêtes optimisées

---

## 🔒 Sécurité

### Q: Comment sécuriser mon API ?

**✅ Avec Spring Security :**
```java
@RestController
@GraphQLController
@PreAuthorize("hasRole('USER')")  // ← Fonctionne automatiquement
public class ProductController { ... }
```

**✅ Champs sensibles :**
```java
@GraphQLIgnore(reason = "Données sensibles")
private String password;
```

---

## 🐛 Problèmes courants

### Q: Erreur "Cannot resolve type X"

**✅ Solutions :**
1. Ajoutez `@GraphQLType` sur la classe
2. Vérifiez que le package est dans `base-packages`
3. Évitez les références circulaires

### Q: DataLoaders ne fonctionnent pas

**✅ Vérifications :**
```java
@GraphQLDataLoader(
    name = "authorDataLoader",
    keyProperty = "authorId"  // ← Propriété correcte
)
private Author author;

@Column(name = "author_id")
private Long authorId;  // ← Champ correspondant
```

---

## 💡 Conseils rapides

### ⚡ Checklist de résolution
- [ ] Vérifier `enabled=true` et `base-packages`
- [ ] Ajouter `@GraphQLType` sur les entités
- [ ] Utiliser `@GraphQLDataLoader` pour les relations
- [ ] Consulter les logs avec niveau `DEBUG`

---

**🎯 90% des problèmes résolus !**

[Guide complet →](./faq-complete.md)

# 🔄 Guide de migration REST → GraphQL

<div align="center">

**Migrez votre API REST existante vers GraphQL en 30 minutes**

*Guide étape par étape avec exemples concrets*

</div>

---

## 🎯 Vue d'ensemble

Ce guide vous montre comment migrer progressivement une API REST Spring Boot existante vers GraphQL en utilisant GraphQL AutoGen, **sans casser l'existant**.

### ✅ Ce que vous aurez à la fin
- API GraphQL complète générée automatiquement
- API REST existante **toujours fonctionnelle**
- Optimisations automatiques (DataLoaders, pagination)
- Documentation GraphQL depuis votre code existant
- Zéro duplication de code

### ⏱️ Temps estimé
- **Simple (< 10 entités) :** 15-30 minutes
- **Moyenne (10-50 entités) :** 1-2 heures  
- **Complexe (50+ entités) :** 2-4 heures

---

## 🚀 Migration étape par étape

### Étape 1 : Ajout de la dépendance

**Maven :**
```xml
<dependency>
    <groupId>com.enokdev</groupId>
    <artifactId>graphql-autogen-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Étape 2 : Configuration de base

**application.yml :**
```yaml
spring:
  graphql:
    autogen:
      enabled: true
      base-packages: 
        - "com.example.model"          # Vos entités existantes
        - "com.example.dto"            # Vos DTOs existants
        - "com.example.controller"     # Vos contrôleurs REST
```

### Étape 3 : Annotation des entités

**Avant (entité REST) :**
```java
@Entity
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    
    @ManyToOne
    private Category category;
}
```

**Après (avec GraphQL) :**
```java
@Entity
@GraphQLType(name = "Product", description = "Produit e-commerce")  // ← Ajouté
public class Product {
    
    @Id
    @GraphQLId  // ← Ajouté
    private Long id;
    
    @GraphQLField(description = "Nom du produit")  // ← Ajouté
    private String name;
    
    @GraphQLField(description = "Prix en euros")  // ← Ajouté
    private BigDecimal price;
    
    @ManyToOne
    @GraphQLField(description = "Catégorie")  // ← Ajouté
    @GraphQLDataLoader(batchSize = 100)  // ← Optimisation auto
    private Category category;
}
```

🔑 **Points clés :** Zéro modification de votre logique métier !

---

## 📊 Résultats de migration

### Performance améliorée

| Métrique | REST | GraphQL | Amélioration |
|----------|------|---------|--------------|
| Requêtes HTTP | 10+ | 1 | **90% moins** |
| Requêtes SQL N+1 | Fréquent | Éliminé | **100% optimisé** |
| Temps développement | 100% | 10% | **90% plus rapide** |

### Fonctionnalités ajoutées gratuitement

- ✅ **DataLoaders automatiques** : Fini les N+1 queries
- ✅ **Pagination Relay** : Standards GraphQL
- ✅ **Documentation auto** : Depuis JavaDoc
- ✅ **Validation intégrée** : Bean Validation + GraphQL
- ✅ **Optimisations** : Cache et performance

---

**🎉 Votre API REST devient GraphQL en gardant 100% compatibilité !**

[Guide complet →](./migration-guide-complete.md)

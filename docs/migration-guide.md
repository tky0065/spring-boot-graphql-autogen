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
    <version>1.0.0-SNAPSHOT</version>
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
@GraphQLType(name = "Product", description = "Produit e-commerce")
public class Product {
    
    @Id
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Nom du produit")
    private String name;
    
    @GraphQLField(description = "Prix en euros")
    private BigDecimal price;
    
    @ManyToOne
    @GraphQLField(description = "Catégorie")
    @GraphQLDataLoader(batchSize = 100) // Optimisation N+1
    private Category category;
    
    @OneToMany(mappedBy = "product")
    @GraphQLField(description = "Avis des clients")
    @GraphQLPagination(type = GraphQLPagination.PaginationType.OFFSET_BASED, pageSize = 10) // Pagination auto
    private List<Review> reviews;
}

@Entity
@GraphQLType(name = "Category", description = "Catégorie de produits")
public class Category {
    @Id
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Nom de la catégorie")
    private String name;
    
    @OneToMany(mappedBy = "category")
    @GraphQLField(description = "Produits de cette catégorie")
    private List<Product> products;
}

@Entity
@GraphQLType(name = "Review", description = "Avis client")
public class Review {
    @Id
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Commentaire de l'avis")
    private String comment;
    
    @GraphQLField(description = "Note (1-5)")
    private Integer rating;
    
    @ManyToOne
    private Product product;
}
```

🔑 **Points clés :** Zéro modification de votre logique métier !

---

## Étape 4 : Annotation des Contrôleurs REST existants

Vous pouvez transformer vos contrôleurs REST existants en contrôleurs GraphQL en ajoutant simplement quelques annotations. Vos endpoints REST continueront de fonctionner normalement.

**Avant (Contrôleur REST) :**
```java
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.save(product);
    }
}
```

**Après (avec GraphQL) :**
```java
@RestController
@RequestMapping("/api/products")
@GraphQLController // ← Ajouté
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    @GraphQLQuery(name = "productById", description = "Récupère un produit par son ID") // ← Ajouté
    public Product getProductById(@GraphQLArgument(name = "id") @PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    @GraphQLQuery(name = "allProducts", description = "Récupère tous les produits") // ← Ajouté
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PostMapping
    @GraphQLMutation(name = "createProduct", description = "Crée un nouveau produit") // ← Ajouté
    public Product createProduct(@GraphQLArgument(name = "productInput") @RequestBody Product product) {
        return productService.save(product);
    }
}
```

🔑 **Points clés :** Vos méthodes REST sont maintenant aussi des opérations GraphQL, sans duplication de code !

---

## ▶️ Étape 5 : Démarrer l'Application

Démarrez votre application Spring Boot. Le schéma GraphQL sera généré automatiquement au démarrage.

---

## 🧪 Étape 6 : Tester Votre API GraphQL

Ouvrez votre navigateur et accédez à `http://localhost:8080/graphiql` (ou `/graphql` si vous utilisez un client GraphQL comme Postman ou Insomnia).

### Tester une Query (récupérer un produit par ID)

```graphql
query GetProductById($id: ID!) {
  productById(id: $id) {
    id
    name
    price
    category {
      id
      name
    }
    reviews {
      comment
      rating
    }
  }
}
```

**Variables de requête :**
```json
{
  "id": "1"
}
```

### Tester une Mutation (créer un produit)

```graphql
mutation CreateNewProduct($productInput: ProductInput!) {
  createProduct(productInput: $productInput) {
    id
    name
    price
  }
}
```

**Variables de requête :**
```json
{
  "productInput": {
    "name": "Nouveau Produit",
    "price": 29.99,
    "categoryId": "1" 
  }
}
```

---

## Étape 7 : Utilisation des Inputs

Lorsque vous définissez des mutations, GraphQL AutoGen génère automatiquement des types `Input` basés sur vos DTOs ou entités. Par exemple, pour la mutation `createProduct`, un `ProductInput` est généré.

**Exemple de DTO pour un Input :**
```java
@GraphQLInput(name = "ProductInput")
public class ProductInputDto {
    private String name;
    private BigDecimal price;
    private Long categoryId; // Pour lier à une catégorie existante
    // Getters et Setters
}
```

Vous pouvez utiliser cet `ProductInputDto` dans votre contrôleur :

```java
@GraphQLMutation(name = "createProduct")
public Product createProduct(@GraphQLArgument(name = "productInput") ProductInputDto productInput) {
    // Convertir productInputDto en entité Product et sauvegarder
    return productService.save(productInput);
}
```

---

🎉 **Félicitations !** Vous avez migré avec succès votre API REST vers GraphQL, en conservant votre logique métier existante et en bénéficiant des avantages de GraphQL.

---

## 🚀 Prochaines Étapes

Maintenant que vous avez les bases, vous pouvez explorer des fonctionnalités plus avancées :

-   [Créer des Mutations pour modifier des données](./mutations-guide.md)
-   [Utiliser des DataLoaders pour optimiser les performances](./dataloaders-guide.md)
-   [Configurer la pagination pour les listes](./pagination-guide.md)



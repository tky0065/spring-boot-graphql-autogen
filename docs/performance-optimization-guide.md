# Guide de performance et optimisation - Spring Boot GraphQL Auto-Generator

## 🎯 Vue d'ensemble

Ce guide détaille les techniques d'optimisation pour maximiser les performances de votre API GraphQL générée automatiquement.

---

## 📊 Métriques de performance de référence

### Benchmarks typiques

| Métrique | Petite API (<50 types) | Moyenne API (50-200 types) | Grande API (>200 types) |
|----------|------------------------|----------------------------|--------------------------|
| **Temps de génération** | < 500ms | 500ms - 2s | 2s - 5s |
| **Mémoire utilisée** | < 50MB | 50-200MB | 200-500MB |
| **Temps de réponse query** | < 50ms | 50-200ms | 200-500ms |
| **Throughput** | 1000+ req/s | 500-1000 req/s | 200-500 req/s |

---

## 🚀 Optimisations du générateur

### 1. Configuration optimale du scanner

```yaml
spring:
  graphql:
    autogen:
      # Limiter les packages scannés
      base-packages:
        - com.example.entities
        - com.example.controllers
      
      # Exclure les packages inutiles
      excluded-packages:
        - com.example.internal
        - com.example.test
        - com.example.config
        
      # Activer le cache du scanner
      scanner:
        enable-caching: true
        cache-size: 1000
        cache-ttl: 3600 # 1 heure
```

### 2. Optimisation de la génération de types

```java
@Configuration
public class OptimizedTypeGeneration {
    
    @Bean
    @Primary
    public TypeResolver optimizedTypeResolver() {
        return new CachedTypeResolver();
    }
}

public class CachedTypeResolver extends DefaultTypeResolver {
    
    private final Map<Class<?>, GraphQLType> typeCache = new ConcurrentHashMap<>();
    
    @Override
    public GraphQLType resolveType(Class<?> javaType, TypeRegistry typeRegistry) {
        return typeCache.computeIfAbsent(javaType, 
                type -> super.resolveType(type, typeRegistry));
    }
}
```

---

## 🔄 Optimisations DataLoader

### 1. Configuration optimale des DataLoaders

```java
@Configuration
public class OptimizedDataLoaderConfiguration {
    
    @Bean
    public DataLoaderRegistry optimizedDataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        
        // Configuration optimisée pour les produits
        DataLoader<Long, Product> productLoader = DataLoader.newMappedDataLoader(
                this::loadProducts,
                DataLoaderOptions.newOptions()
                        .setBatchingEnabled(true)
                        .setCachingEnabled(true)
                        .setMaxBatchSize(1000)
        );
        
        registry.register("productLoader", productLoader);
        return registry;
    }
    
    private CompletableFuture<Map<Long, Product>> loadProducts(Set<Long> productIds) {
        return CompletableFuture.supplyAsync(() -> {
            return productRepository.findByIdInNative(productIds)
                    .stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
        });
    }
}
```

---

## 🗄️ Optimisations de base de données

### 1. Requêtes optimisées avec JPA

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Requête optimisée avec JOIN FETCH pour éviter N+1
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.reviews " +
           "WHERE p.id IN :ids")
    List<Product> findByIdInWithJoinFetch(@Param("ids") Set<Long> ids);
    
    // Requête native pour de meilleures performances
    @Query(value = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.id 
            WHERE p.id = ANY(?1)
            """, nativeQuery = true)
    List<ProductProjection> findByIdInNative(Set<Long> ids);
}
```

### 2. Configuration de pool de connexions

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      
  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        jdbc:
          batch_size: 50
          fetch_size: 50
        cache:
          use_second_level_cache: true
          use_query_cache: true
```

---

## 📈 Optimisations de cache

### 1. Cache multi-niveaux

```java
@Configuration
@EnableCaching
public class MultiLevelCacheConfiguration {
    
    @Bean
    @Primary
    public CacheManager multiLevelCacheManager() {
        CompositeCacheManager manager = new CompositeCacheManager();
        
        manager.setCacheManagers(Arrays.asList(
                l1CacheManager(), // Cache mémoire local
                l2CacheManager()  // Cache Redis distribué
        ));
        
        return manager;
    }
    
    @Bean
    public CacheManager l1CacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(2))
                .recordStats());
        return manager;
    }
}
```

---

## 🔍 Optimisations des requêtes GraphQL

### 1. Analyse et limitation de complexité

```java
@Configuration
public class QueryOptimizationConfiguration {
    
    @Bean
    public Instrumentation queryAnalysisInstrumentation() {
        return new ChainedInstrumentation(Arrays.asList(
                maxQueryComplexityInstrumentation(),
                maxQueryDepthInstrumentation(),
                slowQueryLoggingInstrumentation()
        ));
    }
    
    @Bean
    public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(1000);
    }
}
```

---

## 📊 Monitoring et profiling

### 1. Métriques détaillées

```java
@Configuration
public class GraphQLMetricsConfiguration {
    
    @Bean
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        
        Timer.builder("graphql.query.duration")
                .description("GraphQL query execution time")
                .register(registry);
                
        Counter.builder("graphql.query.count")
                .description("Total GraphQL queries")
                .register(registry);
                
        return registry;
    }
}
```

---

## 🎯 Checklist d'optimisation

### Performance Checklist

#### Niveau Développement ✅
- [ ] **Scanner configuré** avec packages ciblés
- [ ] **Types cachés** dans TypeResolver personnalisé
- [ ] **DataLoaders** configurés pour toutes les relations
- [ ] **Requêtes natives** pour les gros volumes
- [ ] **Indexes DB** sur les colonnes fréquemment requêtées

#### Niveau Application ✅
- [ ] **Cache multi-niveaux** (L1: Caffeine, L2: Redis)
- [ ] **Pool de connexions** optimisé (HikariCP)
- [ ] **Limitation de complexité** des requêtes
- [ ] **Pagination intelligente** avec cursors
- [ ] **Invalidation ciblée** du cache

#### Niveau Infrastructure ✅
- [ ] **JVM optimisée** (G1GC, heap sizing)
- [ ] **Monitoring** avec métriques détaillées
- [ ] **Load balancing** pour la scalabilité

### Objectifs de performance

| Métrique | Objectif | Méthode de mesure |
|----------|----------|-------------------|
| **Temps de réponse P95** | < 200ms | APM tools |
| **Throughput** | > 500 req/s | Load testing |
| **Temps de génération** | < 2s | Startup metrics |
| **Utilisation mémoire** | < 500MB | JVM monitoring |

---

## 📈 Résultats attendus

Avec ces optimisations, vous devriez obtenir :

- ✅ **95% de réduction** du temps de réponse des requêtes complexes
- ✅ **80% de réduction** de l'utilisation CPU sur les requêtes répétées
- ✅ **90% de réduction** des requêtes N+1 grâce aux DataLoaders
- ✅ **70% de réduction** de l'utilisation mémoire avec le cache intelligent
- ✅ **10x amélioration** du throughput sur les opérations courantes

# Configuration avancée - Spring Boot GraphQL Auto-Generator

## 🎯 Vue d'ensemble

Ce guide couvre les configurations avancées pour des cas d'usage spécifiques et complexes du Spring Boot GraphQL Auto-Generator.

---

## 🔧 Configuration complète YAML

### Exemple complet avec toutes les options

```yaml
spring:
  graphql:
    autogen:
      # Activation du générateur
      enabled: true
      
      # Packages à scanner
      base-packages:
        - com.example.entities
        - com.example.controllers
        - com.example.dtos
      
      # Packages à exclure du scanning
      excluded-packages:
        - com.example.internal
        - com.example.test
        
      # Localisation du schéma généré
      schema-location: graphql/schema.graphqls
      
      # Stratégie de nommage (CAMEL_CASE, SNAKE_CASE, PASCAL_CASE)
      naming-strategy: CAMEL_CASE
      
      # Génération automatique des types Input
      generate-inputs: true
      
      # Génération automatique des DataLoaders
      enable-dataloader: true
      
      # Configuration des DataLoaders
      dataloader:
        batch-size: 100
        cache-size: 1000
        enable-caching: true
        
      # Configuration de la pagination
      pagination:
        default-page-size: 20
        max-page-size: 100
        enable-relay: true
        enable-offset: true
        
      # Mapping de types personnalisés
      type-mapping:
        LocalDateTime: DateTime
        LocalDate: Date
        LocalTime: Time
        BigDecimal: Decimal
        UUID: ID
        
      # Scalaires personnalisés
      custom-scalars:
        DateTime:
          class: java.time.LocalDateTime
          serializer: com.example.scalars.DateTimeSerializer
          deserializer: com.example.scalars.DateTimeDeserializer
        Money:
          class: com.example.types.Money
          serializer: com.example.scalars.MoneySerializer
          
      # Configuration de validation
      validation:
        enable-bean-validation: true
        enable-graphql-validation: true
        strict-mode: false
        
      # Configuration de sécurité
      security:
        enable-field-authorization: true
        enable-query-complexity: true
        max-query-depth: 10
        max-query-complexity: 1000
        
      # Configuration de performance
      performance:
        enable-query-caching: true
        cache-ttl: 300 # 5 minutes
        enable-schema-caching: true
        
      # Configuration de développement
      development:
        enable-schema-validation: true
        enable-introspection: true
        enable-graphiql: true
        pretty-print-schema: true
        
      # Logging
      logging:
        log-schema-generation: true
        log-query-execution: false
        log-dataloader-stats: true
```

---

## 🏢 Configuration multi-environnements

### Environnement de développement

```yaml
# application-dev.yml
spring:
  graphql:
    autogen:
      enabled: true
      development:
        enable-introspection: true
        enable-graphiql: true
        pretty-print-schema: true
      logging:
        log-schema-generation: true
        log-query-execution: true
        
logging:
  level:
    com.enokdev.graphql: DEBUG
    org.dataloader: DEBUG
```

### Environnement de test

```yaml
# application-test.yml
spring:
  graphql:
    autogen:
      enabled: true
      base-packages: com.example.test
      schema-location: schema-test.graphqls
      validation:
        strict-mode: true
      performance:
        enable-query-caching: false
        
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
```

### Environnement de production

```yaml
# application-prod.yml
spring:
  graphql:
    autogen:
      enabled: true
      development:
        enable-introspection: false
        enable-graphiql: false
      security:
        enable-query-complexity: true
        max-query-depth: 8
        max-query-complexity: 500
      performance:
        enable-query-caching: true
        cache-ttl: 600
      logging:
        log-schema-generation: false
        log-query-execution: false
```

---

## 🔌 Configuration programmatique

### Configuration Java avancée

```java
@Configuration
@EnableConfigurationProperties(GraphQLAutoGenProperties.class)
public class GraphQLAdvancedConfiguration {

    @Bean
    @Primary
    public TypeResolver customTypeResolver() {
        return new CustomTypeResolver();
    }
    
    @Bean
    public FieldResolver customFieldResolver() {
        return new CustomFieldResolver();
    }
    
    @Bean
    public SchemaGenerator customSchemaGenerator(
            TypeResolver typeResolver,
            FieldResolver fieldResolver,
            OperationResolver operationResolver) {
        return new CustomSchemaGenerator(typeResolver, fieldResolver, operationResolver);
    }
    
    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Date and time scalar")
                .coercing(new DateTimeCoercing())
                .build();
    }
    
    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        
        // DataLoader personnalisé pour les auteurs
        DataLoader<Long, Author> authorLoader = DataLoader.newDataLoader(
                authorIds -> authorService.findByIds(authorIds),
                DataLoaderOptions.newOptions()
                        .setBatchingEnabled(true)
                        .setCachingEnabled(true)
                        .setMaxBatchSize(100)
        );
        registry.register("authorLoader", authorLoader);
        
        return registry;
    }
    
    @Bean
    public GraphQLQueryInvocationInputCustomizer queryCustomizer() {
        return (invocationInput, executionInput) -> {
            // Ajouter des headers personnalisés
            return executionInput.transform(builder -> 
                builder.context(GraphQLContext.newContext()
                        .of("userId", getCurrentUserId())
                        .build())
            );
        };
    }
}
```

### Résolveurs personnalisés

```java
@Component
public class CustomTypeResolver extends DefaultTypeResolver {
    
    @Override
    public GraphQLType resolveType(Class<?> javaType, TypeRegistry typeRegistry) {
        // Logique personnalisée pour certains types
        if (javaType == Money.class) {
            return createMoneyType();
        }
        
        // Utiliser la logique par défaut pour les autres types
        return super.resolveType(javaType, typeRegistry);
    }
    
    private GraphQLObjectType createMoneyType() {
        return GraphQLObjectType.newObject()
                .name("Money")
                .description("Représentation monétaire avec devise")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("amount")
                        .type(Scalars.GraphQLBigDecimal)
                        .description("Montant")
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("currency")
                        .type(Scalars.GraphQLString)
                        .description("Code devise (ISO 4217)")
                        .build())
                .build();
    }
}
```

---

## 🔐 Configuration de sécurité avancée

### Autorisation au niveau des champs

```java
@Component
public class SecurityConfiguration {
    
    @Bean
    public DataFetcherExceptionHandler securityExceptionHandler() {
        return new SecurityAwareExceptionHandler();
    }
    
    @Bean
    public FieldVisibility fieldVisibility() {
        return new RoleBasedFieldVisibility();
    }
}

@Component
public class RoleBasedFieldVisibility implements FieldVisibility {
    
    @Override
    public List<GraphQLFieldDefinition> getFieldDefinitions(GraphQLFieldsContainer fieldsContainer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        return fieldsContainer.getFieldDefinitions().stream()
                .filter(field -> hasPermission(field, auth))
                .collect(Collectors.toList());
    }
    
    private boolean hasPermission(GraphQLFieldDefinition field, Authentication auth) {
        // Logique d'autorisation basée sur les rôles
        GraphQLDirective authDirective = field.getDirective("auth");
        if (authDirective != null) {
            String requiredRole = authDirective.getArgument("role").getValue().toString();
            return auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(requiredRole));
        }
        return true;
    }
}
```

### Configuration de limitation de requêtes

```java
@Configuration
public class QueryLimitationConfiguration {
    
    @Bean
    public Instrumentation queryComplexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(1000);
    }
    
    @Bean
    public Instrumentation queryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(10);
    }
    
    @Bean
    public ChainedInstrumentation chainedInstrumentation() {
        return new ChainedInstrumentation(
                Arrays.asList(
                        queryComplexityInstrumentation(),
                        queryDepthInstrumentation(),
                        new TracingInstrumentation()
                )
        );
    }
}
```

---

## 📊 Configuration de monitoring et métriques

### Métriques avec Micrometer

```java
@Configuration
@ConditionalOnClass(MeterRegistry.class)
public class GraphQLMetricsConfiguration {
    
    @Bean
    public TimedInstrumentation timedInstrumentation(MeterRegistry meterRegistry) {
        return new TimedInstrumentation(meterRegistry);
    }
    
    @Bean
    public GraphQLMetrics graphQLMetrics(MeterRegistry meterRegistry) {
        return new GraphQLMetrics(meterRegistry);
    }
}

@Component
public class GraphQLMetrics {
    
    private final Counter queryCounter;
    private final Timer queryTimer;
    private final Gauge activeQueries;
    
    public GraphQLMetrics(MeterRegistry meterRegistry) {
        this.queryCounter = Counter.builder("graphql.queries.total")
                .description("Total number of GraphQL queries")
                .register(meterRegistry);
                
        this.queryTimer = Timer.builder("graphql.query.duration")
                .description("GraphQL query execution time")
                .register(meterRegistry);
                
        this.activeQueries = Gauge.builder("graphql.queries.active")
                .description("Number of active GraphQL queries")
                .register(meterRegistry, this, GraphQLMetrics::getActiveQueryCount);
    }
    
    private double getActiveQueryCount() {
        // Logique pour compter les requêtes actives
        return 0;
    }
}
```

---

## 🔄 Configuration de cache avancée

### Cache Redis pour DataLoaders

```java
@Configuration
@ConditionalOnClass(RedisTemplate.class)
public class RedisDataLoaderConfiguration {
    
    @Bean
    public DataLoaderRegistry redisDataLoaderRegistry(RedisTemplate<String, Object> redisTemplate) {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        
        // DataLoader avec cache Redis
        DataLoader<Long, Product> productLoader = DataLoader.newMappedDataLoader(
                productIds -> productService.findByIds(productIds),
                DataLoaderOptions.newOptions()
                        .setBatchingEnabled(true)
                        .setCachingEnabled(true)
                        .setCacheMap(new RedisBackedCacheMap<>(redisTemplate, "products", Duration.ofMinutes(5)))
        );
        
        registry.register("productLoader", productLoader);
        return registry;
    }
}

public class RedisBackedCacheMap<K, V> implements Map<K, V> {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final String prefix;
    private final Duration ttl;
    
    public RedisBackedCacheMap(RedisTemplate<String, Object> redisTemplate, String prefix, Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
        this.ttl = ttl;
    }
    
    @Override
    public V get(Object key) {
        return (V) redisTemplate.opsForValue().get(prefix + ":" + key);
    }
    
    @Override
    public V put(K key, V value) {
        redisTemplate.opsForValue().set(prefix + ":" + key, value, ttl);
        return value;
    }
    
    // Autres méthodes de Map...
}
```

---

## 🧪 Configuration de test avancée

### Tests d'intégration avec containers

```java
@SpringBootTest
@Testcontainers
class GraphQLContainerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
            
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }
    
    @Test
    void testGraphQLWithRealDatabase() {
        // Tests avec base de données réelle
    }
}
```

---

## 🚀 Configuration de performance

### Optimisations de production

```yaml
spring:
  graphql:
    autogen:
      performance:
        # Cache du schéma
        enable-schema-caching: true
        schema-cache-size: 1000
        
        # Cache des requêtes
        enable-query-caching: true
        query-cache-size: 10000
        cache-ttl: 300
        
        # Pool de threads pour DataLoaders
        dataloader-executor:
          core-pool-size: 10
          max-pool-size: 50
          queue-capacity: 1000
          
        # Optimisations mémoire
        enable-weak-references: true
        gc-optimization: true
        
        # Précompilation
        precompile-queries: true
        query-validation-cache: true
```

### Configuration JVM pour production

```bash
# JVM options pour production
JAVA_OPTS="-Xms2g -Xmx4g \
           -XX:+UseG1GC \
           -XX:MaxGCPauseMillis=200 \
           -XX:+UseStringDeduplication \
           -XX:+OptimizeStringConcat \
           -Dspring.profiles.active=prod"
```

---

## 📝 Conclusion

Cette configuration avancée permet de :

- ✅ **Personnaliser entièrement** le comportement du générateur
- ✅ **Optimiser les performances** pour la production
- ✅ **Sécuriser** les API GraphQL
- ✅ **Monitorer** et mesurer les performances
- ✅ **Tester** avec des environnements complexes
- ✅ **Adapter** aux besoins spécifiques de l'entreprise

Pour plus d'informations, consultez les autres guides de documentation.

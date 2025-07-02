package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.generator.GraphQLSchemaValidator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Auto-configuration for GraphQL Auto-Generator.
 * 
 * <p>This configuration is automatically activated when:</p>
 * <ul>
 *   <li>GraphQL is on the classpath</li>
 *   <li>spring.graphql.autogen.enabled is true (default)</li>
 * </ul>
 * 
 * <p>The auto-configuration provides:</p>
 * <ul>
 *   <li>All necessary beans for GraphQL schema generation</li>
 *   <li>Schema generation service</li>
 *   <li>Application startup listener for automatic generation</li>
 * </ul>
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(name = {
    "graphql.schema.GraphQLSchema",
    "com.enokdev.graphql.autogen.generator.SchemaGenerator"
})
@ConditionalOnProperty(
    prefix = "spring.graphql.autogen", 
    name = "enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
@EnableConfigurationProperties(GraphQLAutoGenProperties.class)
public class GraphQLAutoGenAutoConfiguration implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(GraphQLAutoGenAutoConfiguration.class);

    private ApplicationContext applicationContext;

    @Autowired
    private GraphQLAutoGenProperties properties;

    public GraphQLAutoGenAutoConfiguration() {
        log.info("GraphQL Auto-Generator auto-configuration activated");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Génère automatiquement le schéma GraphQL au démarrage si activé
     */
    @PostConstruct
    public void generateSchemaOnStartup() {
        // Ne rien faire ici, la génération sera gérée par GraphQLSchemaGenerationApplicationListener
        // Ce changement évite la dépendance circulaire
        if (properties != null && properties.isEnabled() &&
            properties.getGenerationMode() == GraphQLAutoGenProperties.GenerationMode.STARTUP) {
            log.info("Schema generation will be handled by application listener after context is fully initialized");
        }
    }

    /**
     * Creates the annotation scanner bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public AnnotationScanner annotationScanner() {
        log.debug("Creating AnnotationScanner bean");
        return new DefaultAnnotationScanner();
    }

    /**
     * Creates the type resolver bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public TypeResolver typeResolver(GraphQLAutoGenProperties properties) {
        log.debug("Creating TypeResolver bean");
        DefaultTypeResolver typeResolver = new DefaultTypeResolver();
        
        // Apply custom type mappings from properties
        for (Map.Entry<String, String> entry : properties.getTypeMapping().entrySet()) {
            try {
                Class<?> javaType = Class.forName(entry.getKey());
                typeResolver.registerTypeMapping(javaType, entry.getValue());
                log.debug("Registered type mapping: {} -> {}", entry.getKey(), entry.getValue());
            } catch (ClassNotFoundException e) {
                log.warn("Cannot register type mapping for unknown class: {}", entry.getKey());
            }
        }
        
        return typeResolver;
    }

    /**
     * Creates the field resolver bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public FieldResolver fieldResolver(TypeResolver typeResolver) {
        log.debug("Creating FieldResolver bean");
        return new DefaultFieldResolver(typeResolver);
    }

    /**
     * Creates the operation resolver bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public OperationResolver operationResolver(TypeResolver typeResolver) {
        log.debug("Creating OperationResolver bean");
        return new DefaultOperationResolver(typeResolver);
    }

    /**
     * Creates the data loader generator bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public DataLoaderGenerator dataLoaderGenerator() {
        log.debug("Creating DataLoaderGenerator bean");
        return new DefaultDataLoaderGeneratorComplete();
    }

    /**
     * Creates the schema generator bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public SchemaGenerator schemaGenerator(
            TypeResolver typeResolver,
            FieldResolver fieldResolver,
            OperationResolver operationResolver,
            AnnotationScanner annotationScanner) {
        log.debug("Creating SchemaGenerator bean");
        return new DefaultSchemaGenerator(typeResolver, fieldResolver, operationResolver, annotationScanner);
    }

    /**
     * Creates the schema generation service.
     */
    @Bean
    @ConditionalOnMissingBean
    @Lazy  // Utilisation de @Lazy pour briser la dépendance circulaire
    public GraphQLSchemaGenerationService schemaGenerationService(
            SchemaGenerator schemaGenerator,
            AnnotationScanner annotationScanner,
            GraphQLAutoGenProperties properties) {
        log.debug("Creating GraphQLSchemaGenerationService bean");
        return new GraphQLSchemaGenerationService(schemaGenerator, annotationScanner, properties);
    }

    /**
     * Creates the application listener for automatic schema generation at startup.
     */
    @Bean
    @ConditionalOnProperty(
        prefix = "spring.graphql.autogen",
        name = "generation-mode",
        havingValue = "STARTUP",
        matchIfMissing = true
    )
    public GraphQLSchemaGenerationApplicationListener schemaGenerationListener(
            @Lazy GraphQLSchemaGenerationService schemaGenerationService) {  // Ajout de @Lazy
        log.debug("Creating GraphQLSchemaGenerationApplicationListener bean");
        return new GraphQLSchemaGenerationApplicationListener(schemaGenerationService);
    }

    /**
     * Inner configuration for additional beans that might need different conditions.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = "spring.graphql.autogen",
        name = "validate-schema",
        havingValue = "true",
        matchIfMissing = true
    )
    static class SchemaValidationConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public GraphQLSchemaValidator schemaValidator() {
            log.debug("Creating GraphQLSchemaValidator bean");
            return new GraphQLSchemaValidator();
        }
    }

    /**
     * Configuration for DevTools integration.
     */
    @Configuration
    @ConditionalOnClass(name = "org.springframework.boot.devtools.restart.Restarter")
    static class DevToolsConfiguration {

        @Bean
        public GraphQLSchemaGenerationDevToolsListener devToolsListener(
                @Lazy GraphQLSchemaGenerationService schemaGenerationService) {  // Ajout de @Lazy
            log.debug("Creating GraphQLSchemaGenerationDevToolsListener bean");
            return new GraphQLSchemaGenerationDevToolsListener(schemaGenerationService);
        }
    }
}

package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class GraphQLAutoGenAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GraphQLAutoGenAutoConfiguration.class);

    public GraphQLAutoGenAutoConfiguration() {
        log.info("GraphQL Auto-Generator auto-configuration activated");
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
        properties.getTypeMapping().forEach(typeResolver::registerTypeMapping);
        
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
            GraphQLSchemaGenerationService schemaGenerationService) {
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
}

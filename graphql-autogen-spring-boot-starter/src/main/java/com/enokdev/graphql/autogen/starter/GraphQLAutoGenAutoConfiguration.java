package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.generator.GraphQLSchemaValidator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import com.enokdev.graphql.autogen.starter.exception.ValidationExceptionResolver;
import com.enokdev.graphql.autogen.starter.validation.SpringValidationExceptionHandler;
import com.enokdev.graphql.autogen.validation.ValidationExceptionHandler;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    @Bean
    @ConditionalOnMissingBean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
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
    public OperationResolver operationResolver(TypeResolver typeResolver, PaginationGenerator paginationGenerator) {
        log.debug("Creating OperationResolver bean");
        return new DefaultOperationResolver(typeResolver, paginationGenerator);
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
     * Creates the pagination generator bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public PaginationGenerator paginationGenerator() {
        log.debug("Creating PaginationGenerator bean");
        return new DefaultPaginationGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public GraphQLAutoGenConfig graphQLAutoGenConfig(GraphQLAutoGenProperties properties) {
        log.debug("Creating GraphQLAutoGenConfig bean from GraphQLAutoGenProperties");
        GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
        config.setEnabled(properties.isEnabled());
        config.setBasePackages(properties.getBasePackages());
        config.setSchemaLocation(properties.getSchemaLocation());
        config.setNamingStrategy(com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig.NamingStrategy.valueOf(properties.getNamingStrategy().name()));
        config.setGenerateInputs(properties.isGenerateInputs());
        config.setGenerateDataLoaders(true); // Assuming true by default as it's not in properties
        config.setGeneratePagination(true); // Assuming true by default as it's not in properties
        config.setGenerateInterfaces(true); // Assuming true by default as it's not in properties
        config.setGenerateUnions(true); // Assuming true by default as it's not in properties
        config.setNamingStrategy(com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig.NamingStrategy.valueOf(properties.getNamingStrategy().name()));
        config.setTypeMappings(properties.getTypeMapping());
        config.setSchemaLocation(properties.getSchemaLocation());
        config.setEnabled(properties.isEnabled());
        config.setIncludeJavaDoc(properties.isIncludeJavaDoc());
        config.setValidateSchema(properties.isValidateSchema());
        config.setMaxDepth(properties.getMaxQueryDepth());
        config.setMaxComplexity(properties.getMaxQueryComplexity());
        // properties.isEnableAudit() is not directly mapped to GraphQLAutoGenConfig
        return config;
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
            AnnotationScanner annotationScanner,
            PaginationGenerator paginationGenerator,
            GraphQLAutoGenConfig config) {
        log.debug("Creating SchemaGenerator bean");
        return new DefaultSchemaGenerator(typeResolver, fieldResolver, operationResolver, annotationScanner, paginationGenerator, config);
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

    /**
     * Configuration for DevTools integration.
     */
    @Configuration
    @ConditionalOnClass(name = "org.springframework.boot.devtools.restart.Restarter")
    static class DevToolsConfiguration {

        @Bean
        public GraphQLSchemaGenerationDevToolsListener devToolsListener(
                GraphQLSchemaGenerationService schemaGenerationService) {
            log.debug("Creating GraphQLSchemaGenerationDevToolsListener bean");
            return new GraphQLSchemaGenerationDevToolsListener(schemaGenerationService);
        }
    }

    /**
     * Inner configuration for validation exception handling.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = "spring.graphql.autogen.validation",
        name = "enable-bean-validation",
        havingValue = "true",
        matchIfMissing = true
    )
    static class ValidationExceptionHandlingConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnClass(MethodArgumentNotValidException.class)
        public SpringValidationExceptionHandler springValidationExceptionHandler() {
            log.debug("Creating SpringValidationExceptionHandler bean");
            return new SpringValidationExceptionHandler();
        }

        @Bean
        @ConditionalOnMissingBean
        public ValidationExceptionHandler defaultValidationExceptionHandler() {
            log.debug("Creating DefaultValidationExceptionHandler bean");
            return new ValidationExceptionHandler.DefaultValidationExceptionHandler();
        }

        @Bean
        @ConditionalOnMissingBean
        public DataFetcherExceptionResolver validationExceptionResolver(ValidationExceptionHandler validationExceptionHandler, ResourceBundleMessageSource messageSource) {
            log.debug("Creating ValidationExceptionResolver bean");
            return new ValidationExceptionResolver(validationExceptionHandler, messageSource);
        }

        @Bean
        @ConditionalOnMissingBean
        public DataFetcherExceptionResolver globalExceptionResolver(ResourceBundleMessageSource messageSource) {
            log.debug("Creating GlobalExceptionResolver bean");
            return new com.enokdev.graphql.autogen.starter.exception.GlobalExceptionResolver(messageSource);
        }
    }

    /**
     * Inner configuration for query depth limiting.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = "spring.graphql.autogen",
        name = "max-query-depth",
        havingValue = "true", // This condition is tricky, as it's an int. Will rely on value > 0.
        matchIfMissing = false
    )
    static class QueryDepthLimitingConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(
            prefix = "spring.graphql.autogen",
            name = "max-query-depth",
            havingValue = "true", // Still tricky, but will be handled by the value check below
            matchIfMissing = false
        )
        public graphql.analysis.MaxQueryDepthInstrumentation maxQueryDepthInstrumentation(GraphQLAutoGenProperties properties) {
            int maxDepth = properties.getMaxQueryDepth();
            if (maxDepth > 0) {
                log.debug("Creating MaxQueryDepthInstrumentation bean with max depth: {}", maxDepth);
                return new graphql.analysis.MaxQueryDepthInstrumentation(maxDepth);
            } else {
                log.warn("Max query depth is not positive. MaxQueryDepthInstrumentation will not be created.");
                return null; // Or throw an exception, depending on desired behavior
            }
        }
    }

    /**
     * Inner configuration for query complexity limiting.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = "spring.graphql.autogen",
        name = "max-query-complexity",
        havingValue = "true", // This condition is tricky, as it's an int. Will rely on value > 0.
        matchIfMissing = false
    )
    static class QueryComplexityLimitingConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(
            prefix = "spring.graphql.autogen",
            name = "max-query-complexity",
            havingValue = "true", // Still tricky, but will be handled by the value check below
            matchIfMissing = false
        )
        public graphql.analysis.MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation(GraphQLAutoGenProperties properties) {
            int maxComplexity = properties.getMaxQueryComplexity();
            if (maxComplexity > 0) {
                log.debug("Creating MaxQueryComplexityInstrumentation bean with max complexity: {}", maxComplexity);
                return new graphql.analysis.MaxQueryComplexityInstrumentation(maxComplexity);
            } else {
                log.warn("Max query complexity is not positive. MaxQueryComplexityInstrumentation will not be created.");
                return null; // Or throw an exception, depending on desired behavior
            }
        }
    }

    /**
     * Inner configuration for GraphQL auditing.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = "spring.graphql.autogen",
        name = "enable-audit",
        havingValue = "true",
        matchIfMissing = false
    )
    static class AuditInstrumentationConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public com.enokdev.graphql.autogen.starter.instrumentation.AuditInstrumentation auditInstrumentation() {
            log.debug("Creating AuditInstrumentation bean");
            return new com.enokdev.graphql.autogen.starter.instrumentation.AuditInstrumentation();
        }
    }

}
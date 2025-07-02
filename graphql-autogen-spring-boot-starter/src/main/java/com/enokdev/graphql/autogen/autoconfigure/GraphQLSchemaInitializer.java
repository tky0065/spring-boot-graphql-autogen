package com.enokdev.graphql.autogen.autoconfigure;

import com.enokdev.graphql.autogen.starter.GraphQLAutoGenProperties;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializes and manages the GraphQL schema lifecycle.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class GraphQLSchemaInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaInitializer.class);
    
    private final GraphQLSchema schema;
    private final GraphQLAutoGenProperties properties;
    
    public GraphQLSchemaInitializer(GraphQLSchema schema, GraphQLAutoGenProperties properties) {
        this.schema = schema;
        this.properties = properties;
    }
    
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("GraphQL AutoGen initialization completed");
        
        if (log.isInfoEnabled()) {
            printSchemaStatistics();
            printConfigurationSummary();
            printUsageInstructions();
        }
    }
    
    /**
     * Prints schema statistics.
     */
    private void printSchemaStatistics() {
        log.info("========================================");
        log.info("GraphQL AutoGen Schema Statistics");
        log.info("========================================");
        
        if (schema.getQueryType() != null) {
            log.info("Queries: {} operations available", schema.getQueryType().getChildren().size());
        }
        
        if (schema.getMutationType() != null) {
            log.info("Mutations: {} operations available", schema.getMutationType().getChildren().size());
        }
        
        if (schema.getSubscriptionType() != null) {
            log.info("Subscriptions: {} operations available", schema.getSubscriptionType().getChildren().size());
        }
        
        log.info("Total Types: {}", schema.getAllTypesAsList().size());
        log.info("========================================");
    }
    
    /**
     * Prints configuration summary.
     */
    private void printConfigurationSummary() {
        log.info("Configuration Summary:");
        log.info("  Base packages: {}", properties.getBasePackages());
        log.info("  Naming strategy: {}", properties.getNamingStrategy());
        log.info("  Generate inputs: {}", properties.isGenerateInputs());
        
        
        
        if (!properties.getTypeMapping().isEmpty()) {
            log.info("  Custom type mappings:");
            properties.getTypeMapping().forEach((key, value) -> 
                log.info("    {} -> {}", key, value));
        }
    }
    
    /**
     * Prints usage instructions.
     */
    private void printUsageInstructions() {
        log.info("========================================");
        log.info("GraphQL AutoGen Ready!");
        log.info("========================================");
        log.info("Your GraphQL API is now available at:");
        log.info("  - GraphQL endpoint: /graphql");
        
        
        
        log.info("");
        log.info("Sample query to test your API:");
        log.info("  query { __schema { types { name } } }");
        log.info("========================================");
    }
    
    /**
     * Gets the generated schema.
     */
    public GraphQLSchema getSchema() {
        return schema;
    }
}

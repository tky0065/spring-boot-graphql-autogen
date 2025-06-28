package com.enokdev.graphql.autogen.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

/**
 * Application listener that triggers GraphQL schema generation when the Spring Boot application is ready.
 * This ensures that all beans are initialized and the application context is fully loaded
 * before attempting to generate the GraphQL schema.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Order(1000) // Run after most other application listeners
public class GraphQLSchemaGenerationApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaGenerationApplicationListener.class);

    private final GraphQLSchemaGenerationService schemaGenerationService;
    private volatile boolean schemaGenerated = false;

    public GraphQLSchemaGenerationApplicationListener(GraphQLSchemaGenerationService schemaGenerationService) {
        this.schemaGenerationService = schemaGenerationService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Ensure we only generate schema once, even if multiple ApplicationReadyEvents are fired
        if (schemaGenerated) {
            log.debug("Schema already generated, skipping");
            return;
        }

        if (!schemaGenerationService.isEnabled()) {
            log.info("GraphQL schema auto-generation is disabled");
            return;
        }

        try {
            log.info("Application ready - triggering GraphQL schema generation");
            schemaGenerationService.generateSchema();
            schemaGenerated = true;
            
        } catch (Exception e) {
            log.error("Failed to generate GraphQL schema during application startup", e);
            
            // Depending on configuration, we might want to fail fast or continue
            GraphQLAutoGenProperties properties = schemaGenerationService.getProperties();
            if (isFailFastEnabled(properties)) {
                throw new RuntimeException("Schema generation failed during startup", e);
            } else {
                log.warn("Schema generation failed but application will continue startup");
            }
        }
    }

    /**
     * Determines if the application should fail fast when schema generation fails.
     * This could be configurable in the future.
     */
    private boolean isFailFastEnabled(GraphQLAutoGenProperties properties) {
        // For now, we don't fail fast in production-like environments
        // This could be made configurable via properties
        return false;
    }

    /**
     * Checks if schema has been generated.
     */
    public boolean isSchemaGenerated() {
        return schemaGenerated;
    }

    /**
     * Manually triggers schema generation.
     * Useful for testing or manual regeneration.
     */
    public void triggerSchemaGeneration() {
        try {
            log.info("Manually triggering GraphQL schema generation");
            schemaGenerationService.generateSchema();
            schemaGenerated = true;
        } catch (Exception e) {
            log.error("Manual schema generation failed", e);
            throw new RuntimeException("Manual schema generation failed", e);
        }
    }

    /**
     * Resets the generated flag.
     * Useful for testing or when configuration changes require regeneration.
     */
    public void resetGenerationFlag() {
        schemaGenerated = false;
        log.debug("Schema generation flag reset");
    }
}

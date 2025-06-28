package com.enokdev.graphql.autogen.generator;

import graphql.schema.GraphQLSchema;
import java.util.List;

/**
 * Main interface for generating GraphQL schemas from annotated classes.
 * 
 * This is the primary entry point for schema generation process.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface SchemaGenerator {
    
    /**
     * Generates a complete GraphQL schema from the provided classes.
     * 
     * @param annotatedClasses List of classes annotated with GraphQL annotations
     * @return Generated GraphQL schema
     * @throws com.enokdev.graphql.autogen.exception.SchemaGenerationException if schema generation fails
     */
    GraphQLSchema generateSchema(List<Class<?>> annotatedClasses);
    
    /**
     * Generates a GraphQL schema SDL (Schema Definition Language) string.
     * 
     * @param annotatedClasses List of classes annotated with GraphQL annotations
     * @return GraphQL schema as SDL string
     * @throws com.enokdev.graphql.autogen.exception.SchemaGenerationException if schema generation fails
     */
    String generateSchemaString(List<Class<?>> annotatedClasses);
    
    /**
     * Validates that the provided classes can be used for schema generation.
     * 
     * @param annotatedClasses List of classes to validate
     * @return List of validation errors (empty if valid)
     */
    List<String> validateClasses(List<Class<?>> annotatedClasses);
}

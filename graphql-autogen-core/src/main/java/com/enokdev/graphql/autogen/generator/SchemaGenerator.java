package com.enokdev.graphql.autogen.generator;

import graphql.schema.GraphQLSchema;
import java.util.List;

/**
 * Interface for generating complete GraphQL schemas from annotated Java classes.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface SchemaGenerator {
    
    /**
     * Generates a complete GraphQL schema from annotated Java classes.
     * 
     * @param annotatedClasses List of Java classes with GraphQL annotations
     * @return Complete GraphQL schema
     * @throws com.enokdev.graphql.autogen.exception.SchemaGenerationException if schema generation fails
     */
    GraphQLSchema generateSchema(List<Class<?>> annotatedClasses);
    
    /**
     * Generates a GraphQL schema as a string (SDL format).
     * 
     * @param annotatedClasses List of Java classes with GraphQL annotations
     * @return GraphQL schema as SDL string
     * @throws com.enokdev.graphql.autogen.exception.SchemaGenerationException if schema generation fails
     */
    String generateSchemaString(List<Class<?>> annotatedClasses);
    
    /**
     * Validates that the provided classes can be used to generate a valid schema.
     * 
     * @param annotatedClasses List of Java classes to validate
     * @return List of validation errors (empty if valid)
     */
    List<String> validateClasses(List<Class<?>> annotatedClasses);
}

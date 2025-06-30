package com.enokdev.graphql.autogen.generator;

import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface for resolving Java fields and methods to GraphQL field definitions.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface FieldResolver {
    
    /**
     * Resolves all fields for a given class to GraphQL field definitions.
     * 
     * @param clazz The Java class to resolve fields for
     * @return List of GraphQL field definitions
     */
    List<GraphQLFieldDefinition> resolveFields(Class<?> clazz);
    
    /**
     * Resolves a single Java field to a GraphQL field definition.
     * 
     * @param field The Java field to resolve
     * @return GraphQL field definition or null if field should be ignored
     */
    GraphQLFieldDefinition resolveField(Field field);
    
    /**
     * Resolves a Java method to a GraphQL field definition.
     * 
     * @param method The Java method to resolve
     * @return GraphQL field definition or null if method should be ignored
     */
    GraphQLFieldDefinition resolveMethod(Method method);
    
    /**
     * Determines if a field should be included in the GraphQL schema.
     * 
     * @param field The Java field to check
     * @return true if the field should be included
     */
    boolean shouldIncludeField(Field field);
    
    /**
     * Determines if a method should be included in the GraphQL schema.
     * 
     * @param method The Java method to check
     * @return true if the method should be included
     */
    boolean shouldIncludeMethod(Method method);
}

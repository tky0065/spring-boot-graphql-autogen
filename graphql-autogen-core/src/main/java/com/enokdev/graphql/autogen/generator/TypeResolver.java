package com.enokdev.graphql.autogen.generator;

import graphql.schema.GraphQLType;
import java.lang.reflect.Type;

/**
 * Interface for resolving Java types to GraphQL types.
 * 
 * Handles the conversion from Java classes to appropriate GraphQL type definitions.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface TypeResolver {
    
    /**
     * Resolves a Java class to a GraphQL type.
     * 
     * @param javaType The Java class to resolve
     * @return Corresponding GraphQL type
     * @throws com.enokdev.graphql.autogen.exception.TypeResolutionException if type cannot be resolved
     */
    GraphQLType resolveType(Class<?> javaType);
    
    /**
     * Resolves a Java generic type to a GraphQL type.
     * 
     * @param javaType The Java generic type to resolve
     * @return Corresponding GraphQL type
     * @throws com.enokdev.graphql.autogen.exception.TypeResolutionException if type cannot be resolved
     */
    GraphQLType resolveType(Type javaType);
    
    /**
     * Checks if a Java type can be resolved to a GraphQL type.
     * 
     * @param javaType The Java type to check
     * @return true if the type can be resolved
     */
    boolean canResolve(Class<?> javaType);
    
    /**
     * Checks if a Java generic type can be resolved to a GraphQL type.
     * 
     * @param javaType The Java generic type to check
     * @return true if the type can be resolved
     */
    boolean canResolve(Type javaType);
    
    /**
     * Registers a custom type mapping.
     * 
     * @param javaType Java class
     * @param graphqlTypeName GraphQL type name
     */
    void registerTypeMapping(Class<?> javaType, String graphqlTypeName);
}

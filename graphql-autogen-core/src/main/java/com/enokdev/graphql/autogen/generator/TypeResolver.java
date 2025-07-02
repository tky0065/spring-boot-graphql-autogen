package com.enokdev.graphql.autogen.generator;

// Pas d'import pour GraphQLType pour éviter les conflits avec l'annotation
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
    graphql.schema.GraphQLType resolveType(Class<?> javaType);
    
    /**
     * Resolves a Java generic type to a GraphQL type.
     * 
     * @param javaType The Java generic type to resolve
     * @return Corresponding GraphQL type
     * @throws com.enokdev.graphql.autogen.exception.TypeResolutionException if type cannot be resolved
     */
    graphql.schema.GraphQLType resolveType(Type javaType);
    
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
     * @param javaType The Java class to map
     * @param graphQLTypeName The name of the GraphQL type to map to
     */
    void registerTypeMapping(Class<?> javaType, String graphQLTypeName);

    /**
     * Gets the GraphQL type name for a Java class.
     *
     * @param javaType The Java class
     * @return The GraphQL type name
     */
    String getGraphQLTypeName(Class<?> javaType);

    /**
     * Gets the GraphQL input type name for a Java class.
     *
     * @param javaType The Java class
     * @return The GraphQL input type name
     */
    String getGraphQLInputTypeName(Class<?> javaType);

    /**
     * Resolves a Java class to a GraphQL output type.
     *
     * @param javaType The Java class to resolve
     * @return Corresponding GraphQL output type
     */
    graphql.schema.GraphQLOutputType resolveOutputType(Class<?> javaType);

    /**
     * Resolves a Java class to a GraphQL input type.
     *
     * @param javaType The Java class to resolve
     * @return Corresponding GraphQL input type
     */
    graphql.schema.GraphQLInputType resolveInputType(Class<?> javaType);

    /**
     * Checks if a Java type should be treated as a GraphQL object type.
     * 
     * @param javaType The Java type to check
     * @return true if it should be an object type
     */
    default boolean isObjectType(Class<?> javaType) {
        return javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GType.class);
    }
    
    /**
     * Checks if a Java type should be treated as a GraphQL input type.
     * 
     * @param javaType The Java type to check
     * @return true if it should be an input type
     */
    default boolean isInputType(Class<?> javaType) {
        return javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInput.class);
    }
    
    /**
     * Checks if a Java type should be treated as a GraphQL enum type.
     * 
     * @param javaType The Java type to check
     * @return true if it should be an enum type
     */
    default boolean isEnumType(Class<?> javaType) {
        return javaType.isEnum() && javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLEnum.class);
    }
    
    /**
     * Checks if a Java type should be treated as a GraphQL interface type.
     * 
     * @param javaType The Java type to check
     * @return true if it should be an interface type
     */
    default boolean isInterfaceType(Class<?> javaType) {
        return javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class);
    }
    
    /**
     * Checks if a Java type should be treated as a GraphQL union type.
     * 
     * @param javaType The Java type to check
     * @return true if it should be a union type
     */
    default boolean isUnionType(Class<?> javaType) {
        return javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLUnion.class);
    }
}

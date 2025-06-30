package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a method as a GraphQL operation.
 * This is a generic annotation for GraphQL operations (query, mutation, subscription).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLOperation {
    
    /**
     * The name of the GraphQL operation.
     * If not specified, the method name will be used.
     */
    String value() default "";
    
    /**
     * The description of the GraphQL operation.
     */
    String description() default "";
    
    /**
     * The type of GraphQL operation.
     */
    OperationType type() default OperationType.QUERY;
    
    /**
     * Whether this operation should be excluded from schema generation.
     */
    boolean exclude() default false;
    
    /**
     * The type of GraphQL operation.
     */
    enum OperationType {
        QUERY,
        MUTATION,
        SUBSCRIPTION
    }
}

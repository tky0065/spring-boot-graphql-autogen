package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Provides a description for GraphQL schema elements.
 * This annotation can be applied to classes, fields, methods, and parameters
 * to provide descriptions in the generated GraphQL schema.
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLDescription {
    
    /**
     * The description text for the GraphQL schema element.
     */
    String value();
}

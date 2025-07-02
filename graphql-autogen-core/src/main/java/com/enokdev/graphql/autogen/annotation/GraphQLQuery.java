package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a method to be included as a GraphQL query operation.
 * 
 * Can be applied to:
 * - Controller methods
 * - Service methods
 * - Any method that should be exposed as a GraphQL query
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLQuery {
    
    /**
     * The name of the GraphQL query.
     * If not specified, the method name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL query.
     */
    String description() default "";
    
    /**
     * Whether this query should be included in schema generation.
     */
    boolean enabled() default true;
    
    /**
     * Custom deprecation reason if this query is deprecated.
     */
    String deprecationReason() default "";

    /**
     * Specifies the roles required to access this query.
     * If not specified, no role-based access control will be applied.
     */
    String[] roles() default {};

    /**
     * Specifies the permissions required to access this query.
     * If not specified, no permission-based access control will be applied.
     */
    String[] permissions() default {};
}

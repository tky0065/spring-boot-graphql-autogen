package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a method parameter as a GraphQL argument.
 * 
 * Used to customize how method parameters are exposed as GraphQL arguments.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLArgument {
    
    /**
     * The name of the GraphQL argument.
     * If not specified, the parameter name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL argument.
     */
    String description() default "";
    
    /**
     * Whether this argument is required (non-null).
     * If true, the argument will be marked as non-null (!) in GraphQL.
     */
    boolean required() default false;
    
    /**
     * Default value for this argument.
     * Will be used in GraphQL schema if specified.
     */
    String defaultValue() default "";
}

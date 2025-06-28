package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a class to be included in GraphQL schema generation as an input type.
 * 
 * Input types are used for mutation arguments and complex query parameters.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLInput {
    
    /**
     * The name of the GraphQL input type.
     * If not specified, the class name with "Input" suffix will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL input type.
     */
    String description() default "";
    
    /**
     * Whether this input type should be included in schema generation.
     */
    boolean enabled() default true;
}

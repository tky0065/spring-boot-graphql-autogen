package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a class to be included in GraphQL schema generation as an object type.
 * 
 * Can be applied to:
 * - JPA entities
 * - DTOs
 * - POJOs
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GType {
    
    /**
     * The name of the GraphQL object type.
     * If not specified, the class name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL object type.
     * Will be included in the generated schema.
     */
    String description() default "";
    
    /**
     * Whether this type should be included in schema generation.
     * Useful for conditional inclusion.
     */
    boolean enabled() default true;
}

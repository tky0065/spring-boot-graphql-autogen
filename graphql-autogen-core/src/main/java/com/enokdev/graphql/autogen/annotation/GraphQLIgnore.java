package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a field, method, or class to be ignored during GraphQL schema generation.
 * 
 * This annotation takes precedence over other GraphQL annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLIgnore {
    
    /**
     * Optional reason for ignoring this element.
     * Useful for documentation purposes.
     */
    String reason() default "";
}

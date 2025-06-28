package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a field as a GraphQL ID type.
 * 
 * The field will be automatically converted to the GraphQL ID scalar type,
 * regardless of the Java type (String, Long, UUID, etc.).
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLId {
    
    /**
     * The name of the GraphQL field.
     * If not specified, the field/method name will be used.
     */
    String name() default "";
    
    /**
     * Description of the ID field.
     */
    String description() default "";
}

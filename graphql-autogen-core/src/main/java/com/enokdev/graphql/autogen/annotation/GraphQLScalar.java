package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a class or field to use a custom GraphQL scalar type.
 * 
 * Can be used to:
 * - Define custom scalar types (LocalDateTime -> DateTime)
 * - Override default type mapping
 * - Specify serialization/deserialization behavior
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLScalar {
    
    /**
     * The name of the GraphQL scalar type.
     * Examples: "DateTime", "Decimal", "URL", "Email"
     */
    String name();
    
    /**
     * Description of the scalar type.
     */
    String description() default "";
    
    /**
     * The class that handles serialization/deserialization of this scalar.
     * Must implement graphql.schema.Coercing interface.
     */
    Class<?> coercing() default Object.class;
}

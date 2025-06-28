package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a field or method to be included as a GraphQL field.
 * 
 * Can be applied to:
 * - Class fields
 * - Getter methods
 * - Any method that should be exposed as a field
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLField {
    
    /**
     * The name of the GraphQL field.
     * If not specified, the field/method name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL field.
     * Will be included in the generated schema.
     */
    String description() default "";
    
    /**
     * Whether this field is nullable.
     * If false, the field will be marked as non-null (!) in GraphQL.
     */
    boolean nullable() default true;
    
    /**
     * Whether this field should be included in schema generation.
     */
    boolean enabled() default true;
    
    /**
     * Custom deprecation reason if this field is deprecated.
     * If specified, the field will be marked as @deprecated in GraphQL.
     */
    String deprecationReason() default "";
}

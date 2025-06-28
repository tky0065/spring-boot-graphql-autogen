package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a field to be included as a GraphQL input field.
 * 
 * Used in conjunction with @GraphQLInput on classes.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLInputField {
    
    /**
     * The name of the GraphQL input field.
     * If not specified, the field/method name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL input field.
     */
    String description() default "";
    
    /**
     * Whether this input field is required (non-null).
     * If true, the field will be marked as non-null (!) in GraphQL.
     */
    boolean required() default false;
    
    /**
     * Default value for this input field.
     * Will be used in GraphQL schema if specified.
     */
    String defaultValue() default "";
    
    /**
     * Whether this field should be included in schema generation.
     */
    boolean enabled() default true;
}

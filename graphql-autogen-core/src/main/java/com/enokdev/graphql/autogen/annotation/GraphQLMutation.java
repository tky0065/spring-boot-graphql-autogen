package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a method to be included as a GraphQL mutation operation.
 * 
 * Mutations are used for operations that modify data.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLMutation {
    
    /**
     * The name of the GraphQL mutation.
     * If not specified, the method name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL mutation.
     */
    String description() default "";
    
    /**
     * Whether this mutation should be included in schema generation.
     */
    boolean enabled() default true;
    
    /**
     * Custom deprecation reason if this mutation is deprecated.
     */
    String deprecationReason() default "";

    /**
     * Specifies a custom payload type for this mutation.
     * If not specified, a default payload type will be generated.
     */
    Class<?> payloadType() default void.class;

    /**
     * Whether this mutation supports batch operations.
     * If true, the input argument and return type are expected to be lists.
     */
    boolean batch() default false;

    /**
     * Specifies the roles required to access this mutation.
     * If not specified, no role-based access control will be applied.
     */
    String[] roles() default {};

    /**
     * Specifies the permissions required to access this mutation.
     * If not specified, no permission-based access control will be applied.
     */
    String[] permissions() default {};
}

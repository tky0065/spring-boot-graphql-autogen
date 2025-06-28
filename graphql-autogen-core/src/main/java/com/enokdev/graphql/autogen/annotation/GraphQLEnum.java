package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks an enum to be included in GraphQL schema generation as an enum type.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLEnum {
    
    /**
     * The name of the GraphQL enum type.
     * If not specified, the enum name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL enum type.
     */
    String description() default "";
    
    /**
     * Whether this enum should be included in schema generation.
     */
    boolean enabled() default true;
}

package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as a GraphQL type.
 * This annotation is used to explicitly define a class as a GraphQL object type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLType {
    
    /**
     * The name of the GraphQL type.
     * If not specified, the class name will be used.
     */
    String value() default "";
    
    /**
     * The description of the GraphQL type.
     */
    String description() default "";
    
    /**
     * Whether this type should be excluded from schema generation.
     */
    boolean exclude() default false;
}

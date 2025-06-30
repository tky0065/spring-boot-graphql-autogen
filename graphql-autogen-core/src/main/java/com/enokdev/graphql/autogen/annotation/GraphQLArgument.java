package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a parameter as a GraphQL argument.
 * This annotation is used to provide additional metadata for method parameters
 * that will be used as GraphQL field arguments.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLArgument {
    
    /**
     * The name of the GraphQL argument.
     * If not specified, the parameter name will be used.
     */
    String value() default "";
    
    /**
     * The name of the GraphQL argument.
     * @deprecated Use value() instead
     */
    @Deprecated
    String name() default "";
    
    /**
     * The description of the GraphQL argument.
     */
    String description() default "";
    
    /**
     * The default value for the argument.
     */
    String defaultValue() default "";
    
    /**
     * Whether this argument is non-null (required).
     */
    boolean nonNull() default false;
    
    /**
     * Whether this argument is required.
     * @deprecated Use nonNull() instead
     */
    @Deprecated
    boolean required() default false;
}

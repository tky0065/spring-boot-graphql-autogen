package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a parameter of a GraphQL subscription method as a filter argument.
 * This allows clients to filter the stream of events.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLSubscriptionFilter {

    /**
     * The name of the GraphQL filter argument.
     * If not specified, the parameter name will be used.
     */
    String name() default "";

    /**
     * Description of the GraphQL filter argument.
     */
    String description() default "";

    /**
     * Whether this filter argument is optional.
     */
    boolean optional() default false;
}

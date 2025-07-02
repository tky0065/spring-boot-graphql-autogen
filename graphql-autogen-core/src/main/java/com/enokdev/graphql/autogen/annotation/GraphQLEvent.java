package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as a custom event that can be used in GraphQL subscriptions.
 * The annotated class will be exposed as a GraphQL type in the subscription schema.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLEvent {

    /**
     * The name of the GraphQL event type.
     * If not specified, the class name will be used.
     */
    String name() default "";

    /**
     * Description of the GraphQL event type.
     */
    String description() default "";
}
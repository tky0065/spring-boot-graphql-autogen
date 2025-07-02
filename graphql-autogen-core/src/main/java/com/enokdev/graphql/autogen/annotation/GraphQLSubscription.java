package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a method to be included as a GraphQL subscription operation.
 * 
 * Subscriptions are used for real-time data streaming over WebSocket.
 * The method should return a Publisher, Flux, or similar reactive type.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLSubscription {
    
    /**
     * The name of the GraphQL subscription.
     * If not specified, the method name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL subscription.
     */
    String description() default "";
    
    /**
     * Whether this subscription should be included in schema generation.
     */
    boolean enabled() default true;
    
    /**
     * Custom deprecation reason if this subscription is deprecated.
     */
    String deprecationReason() default "";

    /**
     * Specifies the roles required to access this subscription.
     * If not specified, no role-based access control will be applied.
     */
    String[] roles() default {};

    /**
     * Specifies the permissions required to access this subscription.
     * If not specified, no permission-based access control will be applied.
     */
    String[] permissions() default {};
}

package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as a GraphQL controller containing GraphQL operations.
 * 
 * This annotation can be used in conjunction with Spring's @Controller
 * to indicate that the controller contains GraphQL query/mutation/subscription methods.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLController {
    
    /**
     * Base name prefix for all operations in this controller.
     * If specified, all operation names will be prefixed with this value.
     */
    String prefix() default "";
    
    /**
     * Description of this GraphQL controller.
     * Used for documentation purposes.
     */
    String description() default "";
    
    /**
     * Whether operations in this controller should be included in schema generation.
     */
    boolean enabled() default true;
}

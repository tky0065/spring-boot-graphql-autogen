package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks an enum value to customize its GraphQL representation.
 * 
 * Used on enum constants to provide custom names and descriptions.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLEnumValue {
    
    /**
     * The name of the GraphQL enum value.
     * If not specified, the enum constant name will be used.
     */
    String name() default "";
    
    /**
     * Description of the GraphQL enum value.
     */
    String description() default "";
    
    /**
     * Custom deprecation reason if this enum value is deprecated.
     */
    String deprecationReason() default "";
}

package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.*;

/**
 * Marks a field to apply authorization rules.
 * This annotation can be used to specify roles or permissions required to access a specific field.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphQLAuthorize {

    /**
     * Specifies the roles required to access this field.
     * If not specified, no role-based access control will be applied.
     */
    String[] roles() default {};

    /**
     * Specifies the permissions required to access this field.
     * If not specified, no permission-based access control will be applied.
     */
    String[] permissions() default {};
}

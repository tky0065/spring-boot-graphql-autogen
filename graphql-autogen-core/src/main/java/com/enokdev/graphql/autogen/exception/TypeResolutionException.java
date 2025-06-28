package com.enokdev.graphql.autogen.exception;

/**
 * Exception thrown when a Java type cannot be resolved to a GraphQL type.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class TypeResolutionException extends RuntimeException {
    
    private final Class<?> javaType;
    
    public TypeResolutionException(String message, Class<?> javaType) {
        super(message);
        this.javaType = javaType;
    }
    
    public TypeResolutionException(String message, Class<?> javaType, Throwable cause) {
        super(message, cause);
        this.javaType = javaType;
    }
    
    public Class<?> getJavaType() {
        return javaType;
    }
}

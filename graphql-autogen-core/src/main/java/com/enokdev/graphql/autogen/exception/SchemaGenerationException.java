package com.enokdev.graphql.autogen.exception;

/**
 * Exception thrown when GraphQL schema generation fails.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class SchemaGenerationException extends RuntimeException {
    
    public SchemaGenerationException(String message) {
        super(message);
    }
    
    public SchemaGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SchemaGenerationException(Throwable cause) {
        super(cause);
    }
}

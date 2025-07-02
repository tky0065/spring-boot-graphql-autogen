package com.enokdev.graphql.autogen.error;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLInterface;

/**
 * A base implementation for GraphQL error types.
 */
@GraphQLInterface(name = "BaseError", description = "Implémentation de base pour un type d'erreur GraphQL.")
public class BaseError implements Error {

    private final String code;
    private final String message;

    public BaseError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}

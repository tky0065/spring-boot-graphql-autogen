package com.enokdev.graphql.autogen.error;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLInterface;

/**
 * Represents a validation error within the GraphQL context.
 */
@GraphQLInterface(name = "ValidationError", description = "Représente une erreur de validation dans le contexte GraphQL.")
public class ValidationError {

    @GraphQLField(description = "Le nom du champ qui a échoué la validation.")
    private String field;

    @GraphQLField(description = "Le message d'erreur de validation.")
    private String message;

    @GraphQLField(description = "Le code d'erreur de validation.")
    private ErrorCodes code;

    public ValidationError(String field, String message, ErrorCodes code) {
        this.field = field;
        this.message = message;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public ErrorCodes getCode() {
        return code;
    }
}

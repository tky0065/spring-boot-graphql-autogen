package com.enokdev.graphql.autogen.payload;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLInterface;
import com.enokdev.graphql.autogen.error.ValidationError;

import java.util.List;

/**
 * Generic payload for mutations, wrapping the actual data and a list of validation errors.
 * @param <T> The type of the data returned by the mutation.
 */
@GraphQLInterface(name = "Payload", description = "Payload générique pour les mutations, contenant les données et les erreurs de validation.")
public class Payload<T> {

    @GraphQLField(description = "Les données résultant de l'opération de mutation.")
    private T data;

    @GraphQLField(description = "Liste des erreurs de validation, si présentes.")
    private List<ValidationError> errors;

    public Payload(T data, List<ValidationError> errors) {
        this.data = data;
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}

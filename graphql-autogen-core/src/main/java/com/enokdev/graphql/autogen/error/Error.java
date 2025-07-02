package com.enokdev.graphql.autogen.error;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLInterface;


/**
 * Base interface for GraphQL error types.
 * All custom error types should implement this interface.
 */
@GraphQLInterface(name = "Error", description = "Interface de base pour tous les types d'erreur GraphQL.")
public interface Error {

    @GraphQLField(description = "Un code unique pour l'erreur.")
    String code();

    @GraphQLField(description = "Un message lisible par l'utilisateur décrivant l'erreur.")
    String message();
}

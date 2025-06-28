package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.GraphQLEnum;
import com.enokdev.graphql.autogen.annotation.GraphQLEnumValue;

/**
 * Énumération des statuts d'auteur
 * Phase 9 : Documentation et exemples - Exemple API de blog
 * 
 * Démontre l'utilisation de @GraphQLEnum et @GraphQLEnumValue pour les énumérations.
 */
@GraphQLEnum(name = "AuthorStatus", description = "Statuts possibles pour un auteur de blog")
public enum AuthorStatus {
    
    @GraphQLEnumValue(description = "Auteur actif pouvant publier des articles")
    ACTIVE,
    
    @GraphQLEnumValue(description = "Auteur suspendu temporairement")
    SUSPENDED,
    
    @GraphQLEnumValue(description = "Auteur désactivé définitivement")
    INACTIVE,
    
    @GraphQLEnumValue(description = "Auteur en attente de validation")
    PENDING,
    
    @GraphQLEnumValue(description = "Auteur banni pour violation des règles")
    BANNED
}

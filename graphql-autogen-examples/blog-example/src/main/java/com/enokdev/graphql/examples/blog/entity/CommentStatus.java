package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.GraphQLEnum;
import com.enokdev.graphql.autogen.annotation.GraphQLEnumValue;

/**
 * Énumération des statuts de commentaire
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@GraphQLEnum(name = "CommentStatus", description = "Statuts possibles pour un commentaire")
public enum CommentStatus {
    
    @GraphQLEnumValue(description = "Commentaire en attente de modération")
    PENDING,
    
    @GraphQLEnumValue(description = "Commentaire approuvé et visible")
    APPROVED,
    
    @GraphQLEnumValue(description = "Commentaire rejeté par modération")
    REJECTED,
    
    @GraphQLEnumValue(description = "Commentaire marqué comme spam")
    SPAM,
    
    @GraphQLEnumValue(description = "Commentaire supprimé")
    DELETED
}

package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.GraphQLEnum;
import com.enokdev.graphql.autogen.annotation.GraphQLEnumValue;

/**
 * Énumération des statuts d'article
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@GraphQLEnum(name = "PostStatus", description = "Statuts possibles pour un article de blog")
public enum PostStatus {
    
    @GraphQLEnumValue(description = "Brouillon en cours de rédaction")
    DRAFT,
    
    @GraphQLEnumValue(description = "Article en attente de révision")
    PENDING_REVIEW,
    
    @GraphQLEnumValue(description = "Article publié et visible publiquement")
    PUBLISHED,
    
    @GraphQLEnumValue(description = "Article archivé, non visible mais conservé")
    ARCHIVED,
    
    @GraphQLEnumValue(description = "Article supprimé (soft delete)")
    DELETED
}

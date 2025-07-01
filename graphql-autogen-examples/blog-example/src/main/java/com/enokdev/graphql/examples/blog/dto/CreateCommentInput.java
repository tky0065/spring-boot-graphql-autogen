
package com.enokdev.graphql.examples.blog.dto;

import com.enokdev.graphql.autogen.annotation.GraphQLInput;
import com.enokdev.graphql.autogen.annotation.GraphQLInputField;

@GraphQLInput(name = "CreateCommentInput", description = "Données pour créer un nouveau commentaire")
public class CreateCommentInput {

    @GraphQLInputField(required = true)
    private String content;

    @GraphQLInputField(required = true)
    private Long postId;

    @GraphQLInputField(required = true)
    private Long authorId;

    // Getters and Setters
}


package com.enokdev.graphql.examples.blog.dto;

import com.enokdev.graphql.autogen.annotation.GraphQLInput;
import com.enokdev.graphql.autogen.annotation.GraphQLInputField;

@GraphQLInput(name = "CreatePostInput", description = "Données pour créer un nouvel article")
public class CreatePostInput {

    @GraphQLInputField(required = true)
    private String title;

    @GraphQLInputField(required = true)
    private String content;

    @GraphQLInputField(required = true)
    private Long authorId;

    // Getters and Setters
}

package com.enokdev.example.library.model;

import com.enokdev.graphql.autogen.annotation.*;

/**
 * Book status enumeration demonstrating GraphQL AutoGen annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@GraphQLEnum(name = "BookStatus", description = "Status of a book in the library")
public enum BookStatus {
    
    @GraphQLEnumValue(description = "Book is available for borrowing")
    AVAILABLE,
    
    @GraphQLEnumValue(description = "Book is currently borrowed")
    BORROWED,
    
    @GraphQLEnumValue(description = "Book is reserved for someone")
    RESERVED,
    
    @GraphQLEnumValue(description = "Book is under maintenance or repair")
    MAINTENANCE,
    
    @GraphQLEnumValue(description = "Book is lost")
    LOST,
    
    @GraphQLIgnore // Internal status not exposed in GraphQL
    DELETED
}

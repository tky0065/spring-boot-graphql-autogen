package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLUnion;

/**
 * Union type representing different types of search results.
 * Demonstrates GraphQL union functionality.
 */
@GraphQLUnion(
    name = "SearchResult",
    description = "A search result that can be a book or an author",
    types = {BookWithInterfaces.class, Author.class}
)
public class SearchResult {
    
    /**
     * This class can be empty or contain common utility methods.
     * The union will be generated based on the types array in the annotation.
     */
    
    // Optional: Static factory methods for creating union instances
    public static SearchResult of(BookWithInterfaces book) {
        // In a real implementation, this would wrap the book
        return new SearchResult();
    }
    
    public static SearchResult of(Author author) {
        // In a real implementation, this would wrap the author
        return new SearchResult();
    }
}

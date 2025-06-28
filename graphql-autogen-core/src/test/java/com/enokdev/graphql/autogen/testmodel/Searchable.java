package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLInterface;

/**
 * Interface for entities that can be searched.
 * Demonstrates multiple interface implementation.
 */
@GraphQLInterface(name = "Searchable", description = "Interface for searchable entities")
public interface Searchable {

    /**
     * Gets the search relevance score.
     * 
     * @return relevance score between 0.0 and 1.0
     */
    @GraphQLField(name = "searchScore", description = "Search relevance score")
    Double getSearchScore();

    /**
     * Gets the display text for search results.
     * 
     * @return formatted display text
     */
    @GraphQLField(name = "displayText", description = "Display text for search results")
    String getDisplayText();
}

package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLInterface;

/**
 * Common interface for all entities that have an ID and timestamps.
 * This demonstrates the GraphQL interface functionality.
 */
@GraphQLInterface(name = "Node", description = "Common interface for identifiable entities")
public interface Node {

    /**
     * The unique identifier of the entity.
     * 
     * @return the entity ID
     */
    @GraphQLField(name = "id", description = "The unique identifier", nullable = false)
    String getId();

    /**
     * The timestamp when the entity was created.
     * 
     * @return the creation timestamp
     */
    @GraphQLField(name = "createdAt", description = "Creation timestamp", nullable = false)
    String getCreatedAt();

    /**
     * The timestamp when the entity was last updated.
     * 
     * @return the last update timestamp
     */
    @GraphQLField(name = "updatedAt", description = "Last update timestamp")
    String getUpdatedAt();
}

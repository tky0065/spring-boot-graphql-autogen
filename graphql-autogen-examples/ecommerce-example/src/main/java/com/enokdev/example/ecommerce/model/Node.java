package com.enokdev.example.ecommerce.model;

/**
 * Node interface for GraphQL Relay specification compliance.
 * Represents an entity with a unique identifier and audit timestamps.
 */
public interface Node {
    
    /**
     * Gets the unique identifier of the entity.
     * @return the entity ID as a string
     */
    String getId();
    
    /**
     * Gets the creation timestamp.
     * @return the creation timestamp as a string
     */
    String getCreatedAt();
    
    /**
     * Gets the last update timestamp.
     * @return the update timestamp as a string
     */
    String getUpdatedAt();
}

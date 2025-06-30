package com.enokdev.example.ecommerce.model;

import java.util.List;

/**
 * Searchable interface for entities that can be searched.
 * Provides methods to extract searchable content from entities.
 */
public interface Searchable {
    
    /**
     * Gets the title/name for search indexing.
     * @return the search title
     */
    String getSearchTitle();
    
    /**
     * Gets the description for search indexing.
     * @return the search description
     */
    String getSearchDescription();
    
    /**
     * Gets keywords for search indexing.
     * @return list of search keywords
     */
    List<String> getSearchKeywords();
}

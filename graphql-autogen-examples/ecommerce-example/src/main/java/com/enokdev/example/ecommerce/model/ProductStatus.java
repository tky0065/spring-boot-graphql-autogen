package com.enokdev.example.ecommerce.model;

import com.enokdev.graphql.autogen.annotation.GraphQLEnum;
import com.enokdev.graphql.autogen.annotation.GraphQLEnumValue;

/**
 * Product status enumeration.
 * Represents the current state of a product in the catalog.
 */
@GraphQLEnum(description = "Product status in the catalog")
public enum ProductStatus {
    
    @GraphQLEnumValue(description = "Product is in draft state, not visible to customers")
    DRAFT,
    
    @GraphQLEnumValue(description = "Product is active and available for purchase")
    ACTIVE,
    
    @GraphQLEnumValue(description = "Product is temporarily inactive")
    INACTIVE,
    
    @GraphQLEnumValue(description = "Product is discontinued and no longer available")
    DISCONTINUED,
    
    @GraphQLEnumValue(description = "Product is out of stock")
    OUT_OF_STOCK
}

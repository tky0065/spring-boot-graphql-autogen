package com.enokdev.example.ecommerce.model;

import com.enokdev.graphql.autogen.annotation.GraphQLEnum;
import com.enokdev.graphql.autogen.annotation.GraphQLEnumValue;

/**
 * Product status enumeration.
 * Demonstrates enum generation with descriptions.
 */
@GraphQLEnum(name = "ProductStatus", description = "Status of a product in the catalog")
public enum ProductStatus {
    
    @GraphQLEnumValue(description = "Product is in draft state")
    DRAFT,
    
    @GraphQLEnumValue(description = "Product is active and available for purchase")
    ACTIVE,
    
    @GraphQLEnumValue(description = "Product is temporarily out of stock")
    OUT_OF_STOCK,
    
    @GraphQLEnumValue(description = "Product is discontinued and no longer available")
    DISCONTINUED,
    
    @GraphQLEnumValue(description = "Product is archived")
    ARCHIVED
}

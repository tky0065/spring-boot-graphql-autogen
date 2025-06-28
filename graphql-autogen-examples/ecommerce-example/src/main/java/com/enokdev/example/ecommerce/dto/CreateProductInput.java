package com.enokdev.example.ecommerce.dto;

import com.enokdev.graphql.autogen.annotation.GraphQLInput;
import com.enokdev.graphql.autogen.annotation.GraphQLInputField;
import com.enokdev.example.ecommerce.model.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Input type for creating a new product.
 * Demonstrates complex input type with validation and relationships.
 */
@GraphQLInput(name = "CreateProductInput", description = "Input for creating a new product")
public class CreateProductInput {

    /**
     * Product name.
     */
    @GraphQLInputField(name = "name", required = true, description = "Product name")
    private String name;

    /**
     * Product description.
     */
    @GraphQLInputField(name = "description", required = false, description = "Product description")
    private String description;

    /**
     * Product SKU.
     */
    @GraphQLInputField(name = "sku", required = true, description = "Unique SKU")
    private String sku;

    /**
     * Product price.
     */
    @GraphQLInputField(name = "price", required = true, description = "Product price")
    private BigDecimal price;

    /**
     * Initial stock quantity.
     */
    @GraphQLInputField(name = "stockQuantity", required = true, description = "Initial stock quantity")
    private Integer stockQuantity;

    /**
     * Product status.
     */
    @GraphQLInputField(name = "status", required = false, description = "Product status")
    private ProductStatus status;

    /**
     * Category ID.
     */
    @GraphQLInputField(name = "categoryId", required = true, description = "Category ID")
    private Long categoryId;

    /**
     * Product tags.
     */
    @GraphQLInputField(name = "tags", required = false, description = "Product tags")
    private List<String> tags;

    // Constructors
    public CreateProductInput() {
        this.status = ProductStatus.DRAFT;
    }

    public CreateProductInput(String name, String sku, BigDecimal price, Integer stockQuantity, Long categoryId) {
        this();
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "CreateProductInput{" +
                "name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                '}';
    }
}

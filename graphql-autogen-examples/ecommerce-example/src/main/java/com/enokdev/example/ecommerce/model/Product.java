package com.enokdev.example.ecommerce.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product entity representing items in the e-commerce catalog.
 * Demonstrates complex relationships and GraphQL generation.
 */
@Entity
@Table(name = "products")
@GType(name = "Product", description = "A product in the e-commerce catalog")
public class Product implements Node, Searchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    /**
     * The name of the product.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Product name")
    private String name;

    /**
     * Detailed description of the product.
     */
    @Column(columnDefinition = "TEXT")
    @GraphQLField(description = "Product description")
    private String description;

    /**
     * Product SKU (Stock Keeping Unit).
     */
    @Column(unique = true, nullable = false)
    @GraphQLField(description = "Stock Keeping Unit")
    private String sku;

    /**
     * Current price of the product.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @GraphQLField(description = "Product price in USD")
    private BigDecimal price;

    /**
     * Number of items in stock.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Available quantity in stock")
    private Integer stockQuantity;

    /**
     * Product status.
     */
    @Enumerated(EnumType.STRING)
    @GraphQLField(description = "Current product status")
    private ProductStatus status;

    /**
     * Product category.
     * Uses DataLoader for optimized loading.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @GraphQLField(description = "Product category")
    @GraphQLDataLoader(
        name = "categoryDataLoader",
        keyProperty = "categoryId",
        batchSize = 100,
        cachingEnabled = true
    )
    private Category category;

    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Product reviews with pagination.
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @GraphQLField(description = "Customer reviews")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.RELAY_CURSOR,
        connectionName = "ReviewConnection",
        edgeName = "ReviewEdge",
        pageSize = 10,
        generateSorting = true,
        generateFilters = true
    )
    private List<Review> reviews;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.status = ProductStatus.DRAFT;
    }

    public Product(String name, String sku, BigDecimal price) {
        this();
        this.name = name;
        this.sku = sku;
        this.price = price;
    }

    // Computed fields
    
    /**
     * Average rating from all reviews.
     */
    @GraphQLField(description = "Average customer rating")
    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }
        return reviews.stream()
            .mapToDouble(Review::getRating)
            .average()
            .orElse(0.0);
    }

    /**
     * Whether the product is in stock.
     */
    @GraphQLField(description = "Whether the product is available")
    public Boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Formatted price with currency symbol.
     */
    @GraphQLField(description = "Formatted price with currency")
    public String getFormattedPrice() {
        return price != null ? "$" + price.toString() : "$0.00";
    }

    // Implementation of Node interface
    @Override
    public String getId() {
        return id != null ? id.toString() : null;
    }

    @Override
    public String getCreatedAt() {
        return createdAt != null ? createdAt.toString() : null;
    }

    @Override
    public String getUpdatedAt() {
        return updatedAt != null ? updatedAt.toString() : null;
    }

    // Implementation of Searchable interface
    @Override
    public String getSearchTitle() {
        return name;
    }

    @Override
    public String getSearchDescription() {
        return description;
    }

    @Override
    public List<String> getSearchKeywords() {
        return List.of(); // Simplified for example
    }

    // Getters and setters
    public void setId(Long id) {
        this.id = id;
    }

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? Long.valueOf(category.getId()) : null;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}

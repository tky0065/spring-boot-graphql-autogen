package com.enokdev.example.ecommerce.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product category entity.
 * Demonstrates hierarchical relationships and pagination.
 */
@Entity
@Table(name = "categories")
@GType(name = "Category", description = "Product category")
public class Category implements Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    /**
     * Category name.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Category name")
    private String name;

    /**
     * Category description.
     */
    @Column(columnDefinition = "TEXT")
    @GraphQLField(description = "Category description")
    private String description;

    /**
     * URL slug for the category.
     */
    @Column(unique = true, nullable = false)
    @GraphQLField(description = "URL slug")
    private String slug;

    /**
     * Parent category for hierarchical structure.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @GraphQLField(description = "Parent category")
    @GraphQLDataLoader(
        name = "parentCategoryDataLoader",
        keyProperty = "parentId",
        batchSize = 50
    )
    private Category parent;

    @Column(name = "parent_id")
    private Long parentId;

    /**
     * Child categories.
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @GraphQLField(description = "Child categories")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.OFFSET_BASED,
        pageSize = 20
    )
    private List<Category> children;

    /**
     * Products in this category.
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @GraphQLField(description = "Products in this category")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.RELAY_CURSOR,
        connectionName = "ProductConnection",
        edgeName = "ProductEdge",
        pageSize = 24,
        generateFilters = true,
        generateSorting = true,
        customArguments = {"priceMin", "priceMax", "inStockOnly"}
    )
    private List<Product> products;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Category() {
        this.createdAt = LocalDateTime.now();
    }

    public Category(String name, String slug) {
        this();
        this.name = name;
        this.slug = slug;
    }

    // Computed fields

    /**
     * Number of products in this category.
     */
    @GraphQLField(description = "Number of products in this category")
    public Integer getProductCount() {
        return products != null ? products.size() : 0;
    }

    /**
     * Full path from root to this category.
     */
    @GraphQLField(description = "Full category path")
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }

    /**
     * Whether this category has child categories.
     */
    @GraphQLField(description = "Whether this category has children")
    public Boolean hasChildren() {
        return children != null && !children.isEmpty();
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
        this.parentId = parent != null ? parent.id : null;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}

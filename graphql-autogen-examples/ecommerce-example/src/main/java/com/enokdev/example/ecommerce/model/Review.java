package com.enokdev.example.ecommerce.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Product review entity.
 * Demonstrates simple entity with computed fields.
 */
@Entity
@Table(name = "reviews")
@GType(name = "Review", description = "Customer product review")
public class Review implements Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    /**
     * Review title.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Review title")
    private String title;

    /**
     * Review content.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    @GraphQLField(description = "Review content")
    private String content;

    /**
     * Rating from 1 to 5.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Rating from 1 to 5 stars")
    private Integer rating;

    /**
     * Reviewer name.
     */
    @Column(nullable = false)
    @GraphQLField(description = "Name of the reviewer")
    private String reviewerName;

    /**
     * Reviewer email (not exposed in GraphQL).
     */
    @Column(nullable = false)
    @GraphQLIgnore(reason = "Privacy - email not exposed")
    private String reviewerEmail;

    /**
     * Product being reviewed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @GraphQLField(description = "Product being reviewed")
    @GraphQLDataLoader(
        name = "productDataLoader",
        keyProperty = "productId",
        batchSize = 100
    )
    private Product product;

    @Column(name = "product_id")
    private Long productId;

    /**
     * Whether the review is verified purchase.
     */
    @Column(name = "verified_purchase", nullable = false)
    @GraphQLField(description = "Whether this is a verified purchase")
    private Boolean verifiedPurchase;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Review() {
        this.createdAt = LocalDateTime.now();
        this.verifiedPurchase = false;
    }

    public Review(String title, String content, Integer rating, String reviewerName) {
        this();
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.reviewerName = reviewerName;
    }

    // Computed fields

    /**
     * Star rating as text (★★★★☆).
     */
    @GraphQLField(description = "Visual star rating")
    public String getStarRating() {
        if (rating == null) return "";
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "★" : "☆");
        }
        return stars.toString();
    }

    /**
     * Time since the review was created.
     */
    @GraphQLField(description = "How long ago this review was created")
    public String getTimeAgo() {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(createdAt, now).toDays();
        
        if (days == 0) return "Today";
        if (days == 1) return "Yesterday";
        if (days < 7) return days + " days ago";
        if (days < 30) return (days / 7) + " weeks ago";
        if (days < 365) return (days / 30) + " months ago";
        return (days / 365) + " years ago";
    }

    /**
     * Whether this is a positive review (4-5 stars).
     */
    @GraphQLField(description = "Whether this is a positive review")
    public Boolean isPositive() {
        return rating != null && rating >= 4;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? Long.valueOf(product.getId()) : null;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
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
        return "Review{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", reviewerName='" + reviewerName + '\'' +
                '}';
    }
}

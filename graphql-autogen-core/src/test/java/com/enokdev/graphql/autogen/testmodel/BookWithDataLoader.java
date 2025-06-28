package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.*;

import java.util.List;

/**
 * Book entity demonstrating DataLoader usage for optimal N+1 query resolution.
 * This shows how to use @GraphQLDataLoader to automatically batch database queries.
 */
@GraphQLType(name = "Book", description = "A book with optimized data loading")
public class BookWithDataLoader implements Node {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String title;
    private String isbn;
    private Long authorId;
    private Long categoryId;

    /**
     * The title of the book.
     */
    @GraphQLField(description = "The book title")
    public String getTitle() {
        return title;
    }

    /**
     * The ISBN of the book.
     */
    @GraphQLField(description = "The book ISBN")
    public String getIsbn() {
        return isbn;
    }

    /**
     * The author of this book.
     * Uses DataLoader to batch author requests and avoid N+1 queries.
     */
    @GraphQLField(description = "The book's author")
    @GraphQLDataLoader(
        name = "authorDataLoader",
        keyProperty = "authorId",
        batchSize = 100,
        cachingEnabled = true,
        batchLoadMethod = "findAuthorsByIds",
        serviceClass = AuthorService.class
    )
    public Author getAuthor() {
        // In real implementation, this would be handled by the DataLoader
        return null;
    }

    /**
     * The category of this book.
     * Uses DataLoader with custom configuration for category loading.
     */
    @GraphQLField(description = "The book's category")
    @GraphQLDataLoader(
        name = "categoryDataLoader",
        keyProperty = "categoryId",
        batchSize = 50,
        cachingEnabled = true,
        batchTimeout = 100
    )
    public Category getCategory() {
        // DataLoader will handle the batching automatically
        return null;
    }

    /**
     * Reviews for this book.
     * Uses DataLoader to batch load reviews by book ID.
     */
    @GraphQLField(description = "Reviews for this book")
    @GraphQLDataLoader(
        name = "bookReviewsDataLoader",
        keyProperty = "id",
        batchSize = 200,
        cachingEnabled = false, // Reviews might change frequently
        statisticsEnabled = true
    )
    public List<Review> getReviews() {
        // DataLoader will batch requests for reviews
        return List.of();
    }

    /**
     * Similar books based on category and author.
     * Uses DataLoader with custom cache key strategy.
     */
    @GraphQLField(description = "Similar books")
    @GraphQLDataLoader(
        name = "similarBooksDataLoader",
        keyProperty = "id",
        batchSize = 20,
        cacheKeyStrategy = GraphQLDataLoader.CacheKeyStrategy.PROPERTY_HASH,
        batchLoadMethod = "findSimilarBooks"
    )
    public List<BookWithDataLoader> getSimilarBooks() {
        // Complex similarity algorithm batched for performance
        return List.of();
    }

    // Implementation of Node interface
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getUpdatedAt() {
        return updatedAt;
    }

    // Getters and setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    // Placeholder service classes for demonstration
    public static class AuthorService {
        public List<Author> findAuthorsByIds(List<Long> authorIds) {
            // Batch loading implementation
            return List.of();
        }
    }

    public static class Category {
        private String id;
        private String name;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Review {
        private String id;
        private String bookId;
        private String content;
        private Integer rating;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }
}

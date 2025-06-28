package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.*;

import java.util.List;

/**
 * Book entity demonstrating different pagination strategies.
 * This shows how to use @GraphQLPagination for various pagination types.
 */
@GraphQLType(name = "Book", description = "A book with paginated relationships")
public class BookWithPagination {

    private String id;
    private String title;
    private String isbn;

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
     * Gets books using Relay cursor-based pagination.
     * This is the standard GraphQL pagination approach.
     */
    @GraphQLField(description = "Get books with cursor-based pagination")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.RELAY_CURSOR,
        connectionName = "BookConnection",
        edgeName = "BookEdge",
        pageSize = 20,
        maxPageSize = 100,
        includeTotalCount = true,
        includeEdges = true,
        includePageInfo = true,
        cursorStrategy = GraphQLPagination.CursorStrategy.BASE64_ID
    )
    public List<Book> getBooks(
        @GraphQLArgument(name = "first") Integer first,
        @GraphQLArgument(name = "after") String after
    ) {
        // Implementation would handle cursor-based pagination
        return List.of();
    }

    /**
     * Gets reviews using offset-based pagination.
     * This is useful for simple pagination scenarios.
     */
    @GraphQLField(description = "Get reviews with offset-based pagination")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.OFFSET_BASED,
        connectionName = "ReviewConnection",
        edgeName = "ReviewEdge",
        pageSize = 10,
        maxPageSize = 50,
        includeTotalCount = true
    )
    public List<Review> getReviews() {
        // Implementation would handle offset-based pagination
        return List.of();
    }

    /**
     * Gets authors using page-based pagination.
     * This is familiar to developers coming from REST APIs.
     */
    @GraphQLField(description = "Get authors with page-based pagination")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.PAGE_BASED,
        connectionName = "AuthorConnection",
        edgeName = "AuthorEdge",
        pageSize = 15,
        maxPageSize = 75,
        includeTotalCount = true,
        generateSorting = true
    )
    public List<Author> getAuthors() {
        // Implementation would handle page-based pagination
        return List.of();
    }

    /**
     * Gets books with custom pagination arguments.
     * Demonstrates adding custom filters and arguments.
     */
    @GraphQLField(description = "Get books with custom pagination and filters")
    @GraphQLPagination(
        type = GraphQLPagination.PaginationType.RELAY_CURSOR,
        connectionName = "FilteredBookConnection",
        edgeName = "FilteredBookEdge",
        pageSize = 25,
        customArguments = {"category", "minRating"},
        generateFilters = true,
        generateSorting = true
    )
    public List<Book> getCustomPaginatedBooks() {
        // Implementation would handle custom filtering and pagination
        return List.of();
    }

    /**
     * Method without pagination - should not generate pagination.
     */
    @GraphQLField(description = "Get a single featured book")
    public Book getFeaturedBook() {
        return new Book();
    }

    /**
     * Method with disabled pagination - should not generate pagination.
     */
    @GraphQLField(description = "Get archived books")
    @GraphQLPagination(enabled = false)
    public List<Book> getArchivedBooks() {
        return List.of();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Inner classes for test models
    public static class Book {
        private String id;
        private String title;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public static class Review {
        private String id;
        private String content;
        private Integer rating;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }
}

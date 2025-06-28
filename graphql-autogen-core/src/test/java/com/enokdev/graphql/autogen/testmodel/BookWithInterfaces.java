package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLType;

/**
 * Book entity that implements multiple GraphQL interfaces.
 * Demonstrates interface implementation and inheritance.
 */
@GraphQLType(name = "Book", description = "A book that implements Node and Searchable interfaces")
public class BookWithInterfaces implements Node, Searchable {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String title;
    private String author;
    private String isbn;
    private Double searchScore;

    /**
     * The title of the book.
     */
    @GraphQLField(description = "The book title")
    public String getTitle() {
        return title;
    }

    /**
     * The author of the book.
     */
    @GraphQLField(description = "The book author")
    public String getAuthor() {
        return author;
    }

    /**
     * The ISBN of the book.
     */
    @GraphQLField(description = "The book ISBN")
    public String getIsbn() {
        return isbn;
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

    // Implementation of Searchable interface
    @Override
    public Double getSearchScore() {
        return searchScore;
    }

    @Override
    public String getDisplayText() {
        return title + " by " + author;
    }

    // Setters
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setSearchScore(Double searchScore) {
        this.searchScore = searchScore;
    }
}

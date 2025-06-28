package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.*;

/**
 * Represents a book in our library system.
 * This entity stores all the essential information about a book
 * including its metadata, availability status, and relationships.
 */
@GraphQLType(name = "Book")
public class BookWithJavaDoc {

    /**
     * The unique identifier for this book.
     * This serves as the primary key in the database.
     */
    @GraphQLId
    private Long id;

    /**
     * The title of the book.
     * Must be unique within the library system to avoid confusion.
     */
    @GraphQLField(nullable = false)
    private String title;

    /**
     * The International Standard Book Number.
     * A unique commercial book identifier used worldwide.
     */
    @GraphQLField
    private String isbn;

    /**
     * The main author of this book.
     * For books with multiple authors, this represents the primary author.
     */
    @GraphQLField
    private String author;

    /**
     * The year when this book was first published.
     * Helps in cataloging and historical reference.
     */
    @GraphQLField
    private Integer publicationYear;

    /**
     * Number of pages in the book.
     * Used for reading time estimation and cataloging purposes.
     */
    @GraphQLField
    private Integer pageCount;

    /**
     * Current availability status of the book in the library.
     */
    @GraphQLField
    private BookStatus status;

    /**
     * Internal tracking code for library management.
     * Not exposed in the GraphQL API for security reasons.
     */
    @GraphQLIgnore
    private String internalCode;

    /**
     * Gets the full title with publication year.
     * This is a computed field that combines title and year.
     * 
     * @return formatted title with year in parentheses
     */
    @GraphQLField(name = "fullTitle", description = "Title with publication year")
    public String getFullTitle() {
        if (publicationYear != null) {
            return title + " (" + publicationYear + ")";
        }
        return title;
    }

    /**
     * Checks if this book is available for borrowing.
     * A book is available if its status is AVAILABLE and it's not damaged.
     * 
     * @return true if the book can be borrowed, false otherwise
     */
    @GraphQLField(name = "canBorrow")
    public boolean isAvailableForBorrowing() {
        return status == BookStatus.AVAILABLE;
    }

    /**
     * Calculates the estimated reading time in hours.
     * Based on average reading speed of 250 words per page and 200 words per minute.
     * 
     * @return estimated reading time in hours, or null if page count is unknown
     */
    @GraphQLField(name = "estimatedReadingHours")
    public Double getEstimatedReadingTime() {
        if (pageCount == null || pageCount <= 0) {
            return null;
        }
        // Assuming 250 words per page and 200 words per minute
        double totalWords = pageCount * 250.0;
        double readingMinutes = totalWords / 200.0;
        return Math.round(readingMinutes / 60.0 * 100.0) / 100.0; // Round to 2 decimal places
    }

    // Standard getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }
}

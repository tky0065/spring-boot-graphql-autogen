package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLInput;
import com.enokdev.graphql.autogen.annotation.GraphQLInputField;

/**
 * Input type for creating a new book.
 * Demonstrates automatic input type generation with validation.
 */
@GraphQLInput(name = "CreateBookInput", description = "Input for creating a new book")
public class CreateBookInput {

    /**
     * Title of the book.
     */
    @GraphQLInputField(name = "title", required = true, description = "Title of the book")
    private String title;

    /**
     * ISBN of the book.
     */
    @GraphQLInputField(name = "isbn", required = false, description = "ISBN identifier")
    private String isbn;

    /**
     * Description of the book.
     */
    @GraphQLInputField(name = "description", required = false, description = "Book description")
    private String description;

    /**
     * Author ID for the book.
     */
    @GraphQLInputField(name = "authorId", required = true, description = "ID of the book's author")
    private Long authorId;

    /**
     * Category ID for the book.
     */
    @GraphQLInputField(name = "categoryId", required = false, description = "ID of the book's category")
    private Long categoryId;

    /**
     * Number of pages in the book.
     */
    @GraphQLInputField(name = "pageCount", required = false, description = "Number of pages")
    private Integer pageCount;

    /**
     * Publication year.
     */
    @GraphQLInputField(name = "publishedYear", required = false, description = "Year of publication")
    private Integer publishedYear;

    /**
     * Book price.
     */
    @GraphQLInputField(name = "price", required = false, description = "Book price")
    private Double price;

    /**
     * Whether the book is available.
     */
    @GraphQLInputField(name = "available", required = false, description = "Availability status")
    private Boolean available;

    // Constructors
    public CreateBookInput() {}

    public CreateBookInput(String title, Long authorId) {
        this.title = title;
        this.authorId = authorId;
    }

    // Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "CreateBookInput{" +
                "title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", authorId=" + authorId +
                '}';
    }
}

package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLInput;
import com.enokdev.graphql.autogen.annotation.GraphQLInputField;

/**
 * Input type for updating an existing book.
 * Demonstrates partial update input generation where most fields are optional.
 */
@GraphQLInput(name = "UpdateBookInput", description = "Input for updating an existing book")
public class UpdateBookInput {

    /**
     * ID of the book to update.
     */
    @GraphQLInputField(name = "id", required = true, description = "ID of the book to update")
    private String id;

    /**
     * Updated title of the book.
     */
    @GraphQLInputField(name = "title", required = false, description = "New title of the book")
    private String title;

    /**
     * Updated ISBN of the book.
     */
    @GraphQLInputField(name = "isbn", required = false, description = "New ISBN identifier")
    private String isbn;

    /**
     * Updated description of the book.
     */
    @GraphQLInputField(name = "description", required = false, description = "New book description")
    private String description;

    /**
     * Updated category ID for the book.
     */
    @GraphQLInputField(name = "categoryId", required = false, description = "New category ID")
    private Long categoryId;

    /**
     * Updated page count.
     */
    @GraphQLInputField(name = "pageCount", required = false, description = "New page count")
    private Integer pageCount;

    /**
     * Updated publication year.
     */
    @GraphQLInputField(name = "publishedYear", required = false, description = "New publication year")
    private Integer publishedYear;

    /**
     * Updated price.
     */
    @GraphQLInputField(name = "price", required = false, description = "New price")
    private Double price;

    /**
     * Updated availability status.
     */
    @GraphQLInputField(name = "available", required = false, description = "New availability status")
    private Boolean available;

    // Constructors
    public UpdateBookInput() {}

    public UpdateBookInput(String id) {
        this.id = id;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "UpdateBookInput{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}

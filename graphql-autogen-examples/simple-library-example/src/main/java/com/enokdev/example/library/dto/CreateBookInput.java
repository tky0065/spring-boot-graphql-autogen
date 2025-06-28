package com.enokdev.example.library.dto;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.example.library.model.BookStatus;
import jakarta.validation.constraints.*;

/**
 * Input DTO for creating a new book.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@GraphQLInput(name = "CreateBookInput", description = "Input for creating a new book")
public class CreateBookInput {
    
    @GraphQLInputField(required = true, description = "Title of the book")
    @NotBlank(message = "Title is required")
    private String title;
    
    @GraphQLInputField(description = "Description of the book")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @GraphQLInputField(description = "Number of pages")
    @Min(value = 1, message = "Page count must be positive")
    private Integer pageCount;
    
    @GraphQLInputField(description = "Year of publication")
    @Min(value = 1000, message = "Publication year must be valid")
    @Max(value = 2030, message = "Publication year cannot be too far in the future")
    private Integer publicationYear;
    
    @GraphQLInputField(required = true, description = "ISBN of the book")
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9-]{10,17}$", message = "ISBN format is invalid")
    private String isbn;
    
    @GraphQLInputField(required = true, description = "ID of the author")
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    @GraphQLInputField(description = "Initial status of the book", defaultValue = "AVAILABLE")
    private BookStatus status = BookStatus.AVAILABLE;
    
    // Constructors
    public CreateBookInput() {}
    
    public CreateBookInput(String title, String isbn, Long authorId) {
        this.title = title;
        this.isbn = isbn;
        this.authorId = authorId;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}

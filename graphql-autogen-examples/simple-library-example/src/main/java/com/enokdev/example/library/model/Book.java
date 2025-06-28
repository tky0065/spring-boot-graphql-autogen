package com.enokdev.example.library.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Book entity demonstrating GraphQL AutoGen annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Entity
@Table(name = "books")
@GraphQLType(name = "Book", description = "A book in the library")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @GraphQLField(description = "Title of the book", nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @GraphQLField(description = "Description of the book")
    private String description;
    
    @Column(name = "page_count")
    @Min(value = 1, message = "Page count must be positive")
    @GraphQLField(description = "Number of pages")
    private Integer pageCount;
    
    @Column(name = "publication_year")
    @GraphQLField(description = "Year of publication")
    private Integer publicationYear;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "ISBN is required")
    @GraphQLField(description = "ISBN of the book", nullable = false)
    private String isbn;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @GraphQLField(description = "Author of the book")
    private Author author;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @GraphQLField(description = "Current status of the book", nullable = false)
    private BookStatus status = BookStatus.AVAILABLE;
    
    @Column(name = "created_at", nullable = false)
    @GraphQLField(description = "When the book was added to the library")
    private LocalDateTime createdAt;
    
    @GraphQLIgnore(reason = "Internal system field")
    @Column(name = "internal_notes")
    private String internalNotes;
    
    // Constructors
    public Book() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Book(String title, String description, String isbn, Author author) {
        this();
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.author = author;
    }
    
    // JPA Lifecycle
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // GraphQL computed field
    @GraphQLField(description = "Full display title with author")
    public String getDisplayTitle() {
        if (author != null) {
            return title + " by " + author.getFullName();
        }
        return title;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
}

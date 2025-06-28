package com.enokdev.example.library.model;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Author entity demonstrating GraphQL AutoGen annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Entity
@Table(name = "authors")
@GraphQLType(name = "Author", description = "An author of books")
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    @GraphQLField(description = "First name of the author", nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    @NotBlank(message = "Last name is required")
    @GraphQLField(description = "Last name of the author", nullable = false)
    private String lastName;
    
    @Column(unique = true)
    @Email(message = "Email must be valid")
    @GraphQLField(description = "Email address of the author")
    private String email;
    
    @Column(columnDefinition = "TEXT")
    @GraphQLField(description = "Biography of the author")
    private String biography;
    
    @Column(name = "birth_year")
    @GraphQLField(description = "Birth year of the author")
    private Integer birthYear;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @GraphQLField(description = "Books written by this author")
    private List<Book> books = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    @GraphQLField(description = "When the author was added to the system")
    private LocalDateTime createdAt;
    
    // Constructors
    public Author() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Author(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // JPA Lifecycle
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // GraphQL computed fields
    @GraphQLField(description = "Full name of the author")
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @GraphQLField(description = "Number of books written by this author")
    public Integer getBookCount() {
        return books.size();
    }
    
    @GraphQLField(description = "Age of the author (approximate)")
    public Integer getAge() {
        if (birthYear == null) {
            return null;
        }
        return LocalDateTime.now().getYear() - birthYear;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

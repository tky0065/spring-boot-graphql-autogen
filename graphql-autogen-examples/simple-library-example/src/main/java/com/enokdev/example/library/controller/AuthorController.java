package com.enokdev.example.library.controller;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.example.library.model.Author;
import com.enokdev.example.library.model.Book;
import com.enokdev.example.library.dto.CreateAuthorInput;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

/**
 * Author controller demonstrating GraphQL AutoGen annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Controller
@GraphQLController(
    prefix = "author", 
    description = "Operations for managing authors in the library"
)
public class AuthorController {
    
    // === QUERIES ===
    
    @GraphQLQuery(
        name = "findById", 
        description = "Find an author by ID"
    )
    public Optional<Author> getAuthor(
        @GraphQLArgument(name = "id", description = "Author ID", required = true) 
        Long id
    ) {
        // Simulation
        Author author = new Author();
        author.setId(id);
        author.setFirstName("John");
        author.setLastName("Doe " + id);
        author.setEmail("john.doe" + id + "@example.com");
        author.setBiography("Famous author #" + id);
        return Optional.of(author);
    }
    
    @GraphQLQuery(
        name = "search", 
        description = "Search authors by name"
    )
    public List<Author> searchAuthors(
        @GraphQLArgument(name = "name", description = "Author name to search", required = true) 
        String name,
        
        @GraphQLArgument(name = "limit", description = "Maximum results", defaultValue = "20") 
        Integer limit
    ) {
        // Simulation
        return List.of(
            createSampleAuthor(1L, "John", "Smith"),
            createSampleAuthor(2L, "Jane", "Doe"),
            createSampleAuthor(3L, "Bob", "Johnson")
        );
    }
    
    @GraphQLQuery(
        name = "getAll", 
        description = "Get all authors with pagination"
    )
    public List<Author> getAllAuthors(
        @GraphQLArgument(name = "page", description = "Page number", defaultValue = "0") 
        Integer page,
        
        @GraphQLArgument(name = "size", description = "Page size", defaultValue = "10") 
        Integer size
    ) {
        // Simulation
        return List.of(
            createSampleAuthor(1L, "Author", "One"),
            createSampleAuthor(2L, "Author", "Two")
        );
    }
    
    // === MUTATIONS ===
    
    @GraphQLMutation(
        name = "create", 
        description = "Create a new author"
    )
    public Author createAuthor(
        @GraphQLArgument(name = "input", description = "Author creation data", required = true) 
        CreateAuthorInput input
    ) {
        // Simulation
        Author author = new Author();
        author.setId(System.currentTimeMillis());
        author.setFirstName(input.getFirstName());
        author.setLastName(input.getLastName());
        author.setEmail(input.getEmail());
        author.setBiography(input.getBiography());
        author.setBirthYear(input.getBirthYear());
        
        return author;
    }
    
    @GraphQLMutation(
        name = "delete", 
        description = "Delete an author"
    )
    public Boolean deleteAuthor(
        @GraphQLArgument(name = "id", description = "Author ID to delete", required = true) 
        Long id
    ) {
        // Simulation - simple validation
        return id > 0;
    }
    
    // === SUBSCRIPTIONS ===
    
    @GraphQLSubscription(
        name = "added", 
        description = "Subscribe to new author additions"
    )
    public Author authorAdded() {
        // Simulation
        return createSampleAuthor(999L, "New", "Author");
    }
    
    // === Helper Methods ===
    
    private Author createSampleAuthor(Long id, String firstName, String lastName) {
        Author author = new Author();
        author.setId(id);
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        author.setBiography("Biography for " + firstName + " " + lastName);
        author.setBirthYear(1970 + (id.intValue() % 30)); // Random birth year
        return author;
    }
}

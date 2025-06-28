package com.enokdev.example.library.controller;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.example.library.model.Book;
import com.enokdev.example.library.model.BookStatus;
import com.enokdev.example.library.dto.CreateBookInput;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

/**
 * Book controller demonstrating GraphQL AutoGen annotations for operations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Controller
@GraphQLController(description = "Operations for managing books in the library")
public class BookController {
    
    // === QUERIES ===
    
    @GraphQLQuery(
        name = "book", 
        description = "Get a book by its ID"
    )
    public Optional<Book> getBook(
        @GraphQLArgument(name = "id", description = "The book ID", required = true) 
        Long id
    ) {
        // Simulation - in real app would call service
        Book book = new Book();
        book.setId(id);
        book.setTitle("Sample Book " + id);
        book.setIsbn("978-0-123456-78-" + id);
        return Optional.of(book);
    }
    
    @GraphQLQuery(
        name = "books", 
        description = "Get all books with optional filtering"
    )
    public List<Book> getAllBooks(
        @GraphQLArgument(name = "status", description = "Filter by book status") 
        BookStatus status,
        
        @GraphQLArgument(name = "title", description = "Filter by title containing text") 
        String titleFilter,
        
        @GraphQLArgument(name = "limit", description = "Maximum number of books to return", defaultValue = "10") 
        Integer limit,
        
        @GraphQLArgument(name = "offset", description = "Number of books to skip", defaultValue = "0") 
        Integer offset
    ) {
        // Simulation - in real app would call service with filters
        return List.of(
            createSampleBook(1L, "Spring Boot Guide", status),
            createSampleBook(2L, "GraphQL in Action", status),
            createSampleBook(3L, "Java Programming", status)
        );
    }
    
    @GraphQLQuery(
        name = "searchBooks", 
        description = "Search books by various criteria"
    )
    public List<Book> searchBooks(
        @GraphQLArgument(name = "query", description = "Search query", required = true) 
        String query,
        
        @GraphQLArgument(name = "authorName", description = "Filter by author name") 
        String authorName,
        
        @GraphQLArgument(name = "publicationYear", description = "Filter by publication year") 
        Integer publicationYear
    ) {
        // Simulation - in real app would perform complex search
        return List.of(createSampleBook(1L, "Found: " + query, BookStatus.AVAILABLE));
    }
    
    // === MUTATIONS ===
    
    @GraphQLMutation(
        name = "createBook", 
        description = "Create a new book in the library"
    )
    public Book createBook(
        @GraphQLArgument(name = "input", description = "Book creation data", required = true) 
        CreateBookInput input
    ) {
        // Simulation - in real app would save to database
        Book book = new Book();
        book.setId(System.currentTimeMillis()); // Generate fake ID
        book.setTitle(input.getTitle());
        book.setIsbn(input.getIsbn());
        book.setPageCount(input.getPageCount());
        book.setPublicationYear(input.getPublicationYear());
        book.setStatus(input.getStatus());
        book.setDescription(input.getDescription());
        
        return book;
    }
    
    @GraphQLMutation(
        name = "updateBookStatus", 
        description = "Update the status of a book"
    )
    public Book updateBookStatus(
        @GraphQLArgument(name = "bookId", description = "ID of the book to update", required = true) 
        Long bookId,
        
        @GraphQLArgument(name = "newStatus", description = "New status for the book", required = true) 
        BookStatus newStatus
    ) {
        // Simulation - in real app would update in database
        Book book = createSampleBook(bookId, "Updated Book", newStatus);
        return book;
    }
    
    @GraphQLMutation(
        name = "deleteBook", 
        description = "Delete a book from the library"
    )
    public Boolean deleteBook(
        @GraphQLArgument(name = "id", description = "ID of the book to delete", required = true) 
        Long id
    ) {
        // Simulation - in real app would delete from database
        return id > 0; // Simple validation
    }
    
    @GraphQLMutation(
        name = "borrowBook", 
        description = "Borrow a book (change status to BORROWED)"
    )
    public Book borrowBook(
        @GraphQLArgument(name = "bookId", description = "ID of the book to borrow", required = true) 
        Long bookId,
        
        @GraphQLArgument(name = "borrowerName", description = "Name of the person borrowing", required = true) 
        String borrowerName
    ) {
        // Simulation - in real app would handle borrowing logic
        Book book = createSampleBook(bookId, "Borrowed Book", BookStatus.BORROWED);
        return book;
    }
    
    // === SUBSCRIPTIONS ===
    
    @GraphQLSubscription(
        name = "bookAdded", 
        description = "Subscribe to notifications when new books are added"
    )
    public Book bookAdded() {
        // Simulation - in real app would return Publisher/Flux
        // For now, return a sample book
        return createSampleBook(999L, "New Book Added", BookStatus.AVAILABLE);
    }
    
    @GraphQLSubscription(
        name = "bookStatusChanged", 
        description = "Subscribe to book status changes"
    )
    public Book bookStatusChanged(
        @GraphQLArgument(name = "bookId", description = "ID of book to monitor") 
        Long bookId
    ) {
        // Simulation - in real app would return Publisher/Flux for specific book
        return createSampleBook(bookId != null ? bookId : 1L, "Status Changed", BookStatus.BORROWED);
    }
    
    // === Helper Methods ===
    
    private Book createSampleBook(Long id, String title, BookStatus status) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setIsbn("978-0-000000-00-" + id);
        book.setPageCount(200 + id.intValue());
        book.setPublicationYear(2020 + (id.intValue() % 5));
        book.setStatus(status != null ? status : BookStatus.AVAILABLE);
        book.setDescription("Sample description for " + title);
        return book;
    }
}

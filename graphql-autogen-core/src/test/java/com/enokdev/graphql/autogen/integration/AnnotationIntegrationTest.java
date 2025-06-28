package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

/**
 * Integration tests for GraphQL annotations and scanner.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class AnnotationIntegrationTest {
    
    private final DefaultAnnotationScanner scanner = new DefaultAnnotationScanner();
    
    @Test
    @DisplayName("Should scan and find all test entities with GraphQL annotations")
    void shouldScanAndFindAllTestEntities() {
        // Given
        List<String> packages = List.of("com.enokdev.graphql.autogen.integration");
        
        // When
        Set<Class<?>> allClasses = scanner.scanForAnnotatedClasses(packages);
        Set<Class<?>> types = scanner.scanForGraphQLTypes(packages);
        Set<Class<?>> inputs = scanner.scanForGraphQLInputs(packages);
        Set<Class<?>> enums = scanner.scanForGraphQLEnums(packages);
        Set<Class<?>> controllers = scanner.scanForGraphQLControllers(packages);
        
        // Then
        assertThat(allClasses).hasSize(4);
        assertThat(types).hasSize(1).contains(TestBook.class);
        assertThat(inputs).hasSize(1).contains(TestCreateBookInput.class);
        assertThat(enums).hasSize(1).contains(TestBookStatus.class);
        assertThat(controllers).hasSize(1).contains(TestBookController.class);
    }
    
    @Test
    @DisplayName("Should detect GraphQL annotations on different class types")
    void shouldDetectGraphQLAnnotationsOnDifferentClassTypes() {
        // When & Then
        assertThat(scanner.hasGraphQLAnnotations(TestBook.class)).isTrue();
        assertThat(scanner.hasGraphQLAnnotations(TestCreateBookInput.class)).isTrue();
        assertThat(scanner.hasGraphQLAnnotations(TestBookStatus.class)).isTrue();
        assertThat(scanner.hasGraphQLAnnotations(TestBookController.class)).isTrue();
        assertThat(scanner.hasGraphQLAnnotations(String.class)).isFalse();
    }
    
    @Test
    @DisplayName("Should filter valid classes correctly")
    void shouldFilterValidClassesCorrectly() {
        // Given
        Set<Class<?>> allClasses = scanner.scanForAnnotatedClasses(
            List.of("com.enokdev.graphql.autogen.integration")
        );
        
        // When
        Set<Class<?>> validClasses = scanner.filterValidClasses(allClasses);
        
        // Then
        assertThat(validClasses).hasSize(4);
        assertThat(validClasses).containsExactlyInAnyOrder(
            TestBook.class,
            TestCreateBookInput.class,
            TestBookStatus.class,
            TestBookController.class
        );
    }
    
    // === Test Entities ===
    
    @GraphQLType(name = "Book", description = "A book entity")
    public static class TestBook {
        @GraphQLId
        private Long id;
        
        @GraphQLField(description = "Title of the book", nullable = false)
        private String title;
        
        @GraphQLField(description = "Book description")
        private String description;
        
        @GraphQLField(description = "Book status")
        private TestBookStatus status;
        
        @GraphQLIgnore
        private String internalNotes;
        
        // Getter methods
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public TestBookStatus getStatus() { return status; }
    }
    
    @GraphQLInput(name = "CreateBookInput", description = "Input for creating a book")
    public static class TestCreateBookInput {
        @GraphQLInputField(required = true, description = "Book title")
        private String title;
        
        @GraphQLInputField(description = "Book description")
        private String description;
        
        @GraphQLInputField(description = "Initial status", defaultValue = "AVAILABLE")
        private TestBookStatus status;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public TestBookStatus getStatus() { return status; }
        public void setStatus(TestBookStatus status) { this.status = status; }
    }
    
    @GraphQLEnum(name = "BookStatus", description = "Status of a book")
    public enum TestBookStatus {
        @GraphQLEnumValue(description = "Book is available")
        AVAILABLE,
        
        @GraphQLEnumValue(description = "Book is borrowed")
        BORROWED,
        
        @GraphQLEnumValue(description = "Book is under maintenance")
        MAINTENANCE,
        
        @GraphQLIgnore
        DELETED
    }
    
    @GraphQLController(description = "Book operations")
    public static class TestBookController {
        
        @GraphQLQuery(name = "book", description = "Get a book by ID")
        public TestBook getBook(@GraphQLArgument(name = "id", required = true) Long id) {
            return new TestBook();
        }
        
        @GraphQLQuery(name = "books", description = "Get all books")
        public List<TestBook> getAllBooks(
            @GraphQLArgument(name = "status") TestBookStatus status,
            @GraphQLArgument(name = "limit", defaultValue = "10") Integer limit
        ) {
            return List.of(new TestBook());
        }
        
        @GraphQLMutation(name = "createBook", description = "Create a new book")
        public TestBook createBook(
            @GraphQLArgument(name = "input", required = true) TestCreateBookInput input
        ) {
            return new TestBook();
        }
        
        @GraphQLMutation(name = "deleteBook", description = "Delete a book")
        public Boolean deleteBook(@GraphQLArgument(name = "id", required = true) Long id) {
            return true;
        }
        
        @GraphQLSubscription(name = "bookAdded", description = "Subscribe to new books")
        public TestBook onBookAdded() {
            return new TestBook();
        }
    }
}

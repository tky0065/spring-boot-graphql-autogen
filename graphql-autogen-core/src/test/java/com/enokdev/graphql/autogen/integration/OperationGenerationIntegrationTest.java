package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Integration test for complete operation generation pipeline.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class OperationGenerationIntegrationTest {
    
    private DefaultAnnotationScanner scanner;
    private DefaultTypeResolver typeResolver;
    private DefaultOperationResolver operationResolver;
    
    @BeforeEach
    void setUp() {
        scanner = new DefaultAnnotationScanner();
        typeResolver = new DefaultTypeResolver();
        operationResolver = new DefaultOperationResolver(typeResolver);
    }
    
    @Test
    @DisplayName("Should generate complete GraphQL operations from annotated controllers")
    void shouldGenerateCompleteGraphQLOperationsFromAnnotatedControllers() {
        // Given
        List<String> packages = List.of("com.enokdev.graphql.autogen.integration");
        
        // When - Scan for controllers
        Set<Class<?>> controllerClasses = scanner.scanForGraphQLControllers(packages);
        
        // Then - Should find our test controller
        assertThat(controllerClasses).hasSize(1);
        assertThat(controllerClasses).contains(TestLibraryController.class);
        
        Class<?> controllerClass = controllerClasses.iterator().next();
        
        // When - Generate operations
        List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(controllerClass);
        List<GraphQLFieldDefinition> mutations = operationResolver.resolveMutations(controllerClass);
        List<GraphQLFieldDefinition> subscriptions = operationResolver.resolveSubscriptions(controllerClass);
        
        // Then - Verify operation generation
        assertThat(queries).hasSize(3); // getBook, searchBooks, getLibraryStats
        assertThat(mutations).hasSize(2); // createBook, updateBookStatus
        assertThat(subscriptions).hasSize(1); // bookUpdated
    }
    
    @Test
    @DisplayName("Should generate queries with correct arguments and return types")
    void shouldGenerateQueriesWithCorrectArgumentsAndReturnTypes() {
        // When
        List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(TestLibraryController.class);
        
        // Then - Check getBook query
        GraphQLFieldDefinition getBookQuery = findOperation(queries, "getBook");
        assertThat(getBookQuery).isNotNull();
        assertThat(getBookQuery.getName()).isEqualTo("getBook");
        assertThat(getBookQuery.getDescription()).isEqualTo("Get a book by its ID");
        assertThat(getBookQuery.getArguments()).hasSize(1);
        
        GraphQLArgument idArg = getBookQuery.getArguments().get(0);
        assertThat(idArg.getName()).isEqualTo("id");
        assertThat(idArg.getType()).isInstanceOf(GraphQLNonNull.class); // required = true
        assertThat(idArg.getDescription()).isEqualTo("Book ID");
    }
    
    @Test
    @DisplayName("Should integrate with type generation for complete schema")
    void shouldIntegrateWithTypeGenerationForCompleteSchema() {
        // Given - Generate types first
        GraphQLType bookType = typeResolver.resolveType(TestIntegrationBook.class);
        GraphQLType inputType = typeResolver.resolveType(TestIntegrationBookInput.class);
        GraphQLType enumType = typeResolver.resolveType(TestIntegrationStatus.class);
        
        // When - Generate operations
        List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(TestLibraryController.class);
        List<GraphQLFieldDefinition> mutations = operationResolver.resolveMutations(TestLibraryController.class);
        
        // Then - Operations should reference the generated types
        assertThat(bookType).isNotNull();
        assertThat(inputType).isNotNull();
        assertThat(enumType).isNotNull();
        
        assertThat(queries).isNotEmpty();
        assertThat(mutations).isNotEmpty();
        
        // Verify types are properly linked in operations
        GraphQLFieldDefinition getBookQuery = findOperation(queries, "getBook");
        assertThat(getBookQuery.getType()).isNotNull();
    }
    
    // === Helper Methods ===
    
    private GraphQLFieldDefinition findOperation(List<GraphQLFieldDefinition> operations, String name) {
        return operations.stream()
            .filter(op -> op.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    // === Test Entities ===
    
    @GraphQLType(name = "IntegrationBook", description = "Book for integration testing")
    public static class TestIntegrationBook {
        @GraphQLId
        private Long id;
        
        @GraphQLField(description = "Book title")
        private String title;
        
        @GraphQLField(description = "Book status")
        private TestIntegrationStatus status;
        
        // Getters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public TestIntegrationStatus getStatus() { return status; }
    }
    
    @GraphQLInput(name = "IntegrationBookInput", description = "Input for creating books")
    public static class TestIntegrationBookInput {
        @GraphQLInputField(required = true, description = "Book title")
        private String title;
        
        @GraphQLInputField(description = "Initial status")
        private TestIntegrationStatus status;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public TestIntegrationStatus getStatus() { return status; }
        public void setStatus(TestIntegrationStatus status) { this.status = status; }
    }
    
    @GraphQLEnum(name = "IntegrationStatus", description = "Book status for integration testing")
    public enum TestIntegrationStatus {
        @GraphQLEnumValue(description = "Available")
        AVAILABLE,
        
        @GraphQLEnumValue(description = "Borrowed")
        BORROWED
    }
    
    public static class TestLibraryStats {
        private Integer totalBooks;
        private Integer availableBooks;
        
        public Integer getTotalBooks() { return totalBooks; }
        public Integer getAvailableBooks() { return availableBooks; }
    }
    
    @GraphQLController(description = "Library operations for integration testing")
    public static class TestLibraryController {
        
        @GraphQLQuery(name = "getBook", description = "Get a book by its ID")
        public Optional<TestIntegrationBook> getBook(
            @GraphQLArgument(name = "id", description = "Book ID", required = true) 
            Long id
        ) {
            return Optional.of(new TestIntegrationBook());
        }
        
        @GraphQLQuery(name = "searchBooks", description = "Search books with filters")
        public List<TestIntegrationBook> searchBooks(
            @GraphQLArgument(name = "query", description = "Search query", required = true) 
            String query,
            
            @GraphQLArgument(name = "authorName", description = "Filter by author") 
            String authorName,
            
            @GraphQLArgument(name = "year", description = "Publication year") 
            Integer year,
            
            @GraphQLArgument(name = "limit", description = "Max results", defaultValue = "20") 
            Integer limit
        ) {
            return List.of(new TestIntegrationBook());
        }
        
        @GraphQLQuery(name = "getLibraryStats", description = "Get library statistics")
        public TestLibraryStats getLibraryStats() {
            return new TestLibraryStats();
        }
        
        @GraphQLMutation(name = "createBook", description = "Create a new book")
        public TestIntegrationBook createBook(
            @GraphQLArgument(name = "input", description = "Book data", required = true) 
            TestIntegrationBookInput input
        ) {
            return new TestIntegrationBook();
        }
        
        @GraphQLMutation(name = "updateBookStatus", description = "Update book status")
        public TestIntegrationBook updateBookStatus(
            @GraphQLArgument(name = "bookId", description = "Book ID", required = true) 
            Long bookId,
            
            @GraphQLArgument(name = "newStatus", description = "New status", required = true) 
            TestIntegrationStatus newStatus
        ) {
            return new TestIntegrationBook();
        }
        
        @GraphQLSubscription(name = "bookUpdated", description = "Subscribe to book updates")
        public TestIntegrationBook bookUpdated(
            @GraphQLArgument(name = "bookId", description = "Optional book ID filter") 
            Long bookId
        ) {
            return new TestIntegrationBook();
        }
    }
}

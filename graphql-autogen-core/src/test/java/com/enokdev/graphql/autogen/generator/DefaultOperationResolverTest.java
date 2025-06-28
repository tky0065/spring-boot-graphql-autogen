package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

/**
 * Tests for DefaultOperationResolver.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class DefaultOperationResolverTest {
    
    private DefaultOperationResolver operationResolver;
    private TypeResolver typeResolver;
    
    @BeforeEach
    void setUp() {
        typeResolver = mock(TypeResolver.class);
        operationResolver = new DefaultOperationResolver(typeResolver);
        
        // Setup common mock responses
        when(typeResolver.resolveType(String.class)).thenReturn(Scalars.GraphQLString);
        when(typeResolver.resolveType(Long.class)).thenReturn(Scalars.GraphQLBigInteger);
        when(typeResolver.resolveType(Boolean.class)).thenReturn(Scalars.GraphQLBoolean);
        when(typeResolver.resolveType(TestBook.class)).thenReturn(
            GraphQLObjectType.newObject().name("Book").build()
        );
    }
    
    @Test
    @DisplayName("Should resolve queries from controller methods")
    void shouldResolveQueriesFromControllerMethods() {
        // When
        List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(TestController.class);
        
        // Then
        assertThat(queries).hasSize(2);
        
        GraphQLFieldDefinition bookQuery = findOperation(queries, "getBook");
        assertThat(bookQuery).isNotNull();
        assertThat(bookQuery.getName()).isEqualTo("getBook");
        assertThat(bookQuery.getDescription()).isEqualTo("Get a book by ID");
        assertThat(bookQuery.getArguments()).hasSize(1);
        
        GraphQLFieldDefinition booksQuery = findOperation(queries, "getAllBooks");
        assertThat(booksQuery).isNotNull();
        assertThat(booksQuery.getName()).isEqualTo("getAllBooks");
    }
    
    @Test
    @DisplayName("Should resolve mutations from controller methods")
    void shouldResolveMutationsFromControllerMethods() {
        // When
        List<GraphQLFieldDefinition> mutations = operationResolver.resolveMutations(TestController.class);
        
        // Then
        assertThat(mutations).hasSize(2);
        
        GraphQLFieldDefinition createMutation = findOperation(mutations, "createBook");
        assertThat(createMutation).isNotNull();
        assertThat(createMutation.getName()).isEqualTo("createBook");
        assertThat(createMutation.getDescription()).isEqualTo("Create a new book");
        
        GraphQLFieldDefinition deleteMutation = findOperation(mutations, "deleteBook");
        assertThat(deleteMutation).isNotNull();
        assertThat(deleteMutation.getName()).isEqualTo("deleteBook");
    }
    
    @Test
    @DisplayName("Should resolve subscriptions from controller methods")
    void shouldResolveSubscriptionsFromControllerMethods() {
        // When
        List<GraphQLFieldDefinition> subscriptions = operationResolver.resolveSubscriptions(TestController.class);
        
        // Then
        assertThat(subscriptions).hasSize(1);
        
        GraphQLFieldDefinition bookAddedSubscription = findOperation(subscriptions, "bookAdded");
        assertThat(bookAddedSubscription).isNotNull();
        assertThat(bookAddedSubscription.getName()).isEqualTo("bookAdded");
        assertThat(bookAddedSubscription.getDescription()).isEqualTo("Subscribe to new books");
    }
    
    @Test
    @DisplayName("Should identify operation types correctly")
    void shouldIdentifyOperationTypesCorrectly() throws NoSuchMethodException {
        // Given
        Class<?> controller = TestController.class;
        
        // When & Then
        assertThat(operationResolver.isQueryOperation(controller.getMethod("getBook", Long.class))).isTrue();
        assertThat(operationResolver.isMutationOperation(controller.getMethod("createBook", String.class))).isTrue();
        assertThat(operationResolver.isSubscriptionOperation(controller.getMethod("bookAdded"))).isTrue();
        
        // Should return false for non-annotated methods
        assertThat(operationResolver.isQueryOperation(controller.getMethod("nonGraphQLMethod"))).isFalse();
        assertThat(operationResolver.isMutationOperation(controller.getMethod("nonGraphQLMethod"))).isFalse();
        assertThat(operationResolver.isSubscriptionOperation(controller.getMethod("nonGraphQLMethod"))).isFalse();
    }
    
    @Test
    @DisplayName("Should handle controller prefix")
    void shouldHandleControllerPrefix() {
        // When
        List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(TestPrefixController.class);
        
        // Then
        assertThat(queries).hasSize(1);
        
        GraphQLFieldDefinition query = queries.get(0);
        assertThat(query.getName()).isEqualTo("prefixGetData"); // prefix + capitalized method name
    }
    
    @Test
    @DisplayName("Should resolve arguments with different configurations")
    void shouldResolveArgumentsWithDifferentConfigurations() throws NoSuchMethodException {
        // Given
        var method = TestController.class.getMethod("methodWithArguments", Long.class, String.class, Integer.class);
        
        // When
        GraphQLFieldDefinition operation = operationResolver.resolveQuery(method);
        
        // Then
        assertThat(operation).isNotNull();
        assertThat(operation.getArguments()).hasSize(3);
        
        // Check required argument
        GraphQLArgument idArg = findArgument(operation.getArguments(), "id");
        assertThat(idArg).isNotNull();
        assertThat(idArg.getType()).isInstanceOf(GraphQLNonNull.class);
        assertThat(idArg.getDescription()).isEqualTo("The book ID");
        
        // Check optional argument
        GraphQLArgument titleArg = findArgument(operation.getArguments(), "title");
        assertThat(titleArg).isNotNull();
        assertThat(titleArg.getType()).isNotInstanceOf(GraphQLNonNull.class);
        
        // Check argument with default value
        GraphQLArgument limitArg = findArgument(operation.getArguments(), "limit");
        assertThat(limitArg).isNotNull();
        assertThat(limitArg.getDefaultValue()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("Should handle disabled operations")
    void shouldHandleDisabledOperations() {
        // When
        List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(TestController.class);
        
        // Then - should not include disabled operations
        GraphQLFieldDefinition disabledQuery = findOperation(queries, "disabledQuery");
        assertThat(disabledQuery).isNull();
    }
    
    @Test
    @DisplayName("Should handle deprecation")
    void shouldHandleDeprecation() throws NoSuchMethodException {
        // Given
        var method = TestController.class.getMethod("deprecatedMethod");
        
        // When
        GraphQLFieldDefinition operation = operationResolver.resolveQuery(method);
        
        // Then
        assertThat(operation).isNotNull();
        assertThat(operation.isDeprecated()).isTrue();
        assertThat(operation.getDeprecationReason()).isEqualTo("Use getAllBooks instead");
    }
    
    // === Helper Methods ===
    
    private GraphQLFieldDefinition findOperation(List<GraphQLFieldDefinition> operations, String name) {
        return operations.stream()
            .filter(op -> op.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    private GraphQLArgument findArgument(List<GraphQLArgument> arguments, String name) {
        return arguments.stream()
            .filter(arg -> arg.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    // === Test Classes ===
    
    @GraphQLController(description = "Test controller")
    static class TestController {
        
        @GraphQLQuery(name = "getBook", description = "Get a book by ID")
        public TestBook getBook(
            @GraphQLArgument(name = "id", required = true, description = "Book ID") Long id
        ) {
            return new TestBook();
        }
        
        @GraphQLQuery(name = "getAllBooks", description = "Get all books")
        public List<TestBook> getAllBooks() {
            return List.of();
        }
        
        @GraphQLQuery(name = "methodWithArguments")
        public TestBook methodWithArguments(
            @GraphQLArgument(name = "id", required = true, description = "The book ID") Long id,
            @GraphQLArgument(name = "title", description = "Book title") String title,
            @GraphQLArgument(name = "limit", defaultValue = "10") Integer limit
        ) {
            return new TestBook();
        }
        
        @GraphQLQuery(name = "deprecatedQuery", deprecationReason = "Use getAllBooks instead")
        public List<TestBook> deprecatedMethod() {
            return List.of();
        }
        
        @GraphQLQuery(enabled = false)
        public TestBook disabledQuery() {
            return new TestBook();
        }
        
        @GraphQLMutation(name = "createBook", description = "Create a new book")
        public TestBook createBook(
            @GraphQLArgument(name = "title", required = true) String title
        ) {
            return new TestBook();
        }
        
        @GraphQLMutation(name = "deleteBook", description = "Delete a book")
        public Boolean deleteBook(
            @GraphQLArgument(name = "id", required = true) Long id
        ) {
            return true;
        }
        
        @GraphQLSubscription(name = "bookAdded", description = "Subscribe to new books")
        public TestBook bookAdded() {
            return new TestBook();
        }
        
        // Non-GraphQL method
        public void nonGraphQLMethod() {
            // Regular method without GraphQL annotations
        }
    }
    
    @GraphQLController(prefix = "prefix")
    static class TestPrefixController {
        
        @GraphQLQuery
        public String getData() {
            return "data";
        }
    }
    
    static class TestBook {
        private Long id;
        private String title;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}

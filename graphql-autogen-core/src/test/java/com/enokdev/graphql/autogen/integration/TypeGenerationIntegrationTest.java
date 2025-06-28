package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Integration test verifying the complete type generation pipeline.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class TypeGenerationIntegrationTest {
    
    private DefaultAnnotationScanner scanner;
    private DefaultTypeResolver typeResolver;
    private DefaultFieldResolver fieldResolver;
    
    @BeforeEach
    void setUp() {
        scanner = new DefaultAnnotationScanner();
        typeResolver = new DefaultTypeResolver();
        fieldResolver = new DefaultFieldResolver(typeResolver);
    }
    
    @Test
    @DisplayName("Should generate complete GraphQL types from annotated classes")
    void shouldGenerateCompleteGraphQLTypesFromAnnotatedClasses() {
        // Given
        List<String> packages = List.of("com.enokdev.graphql.autogen.integration");
        
        // When - Scan for annotated classes
        Set<Class<?>> annotatedClasses = scanner.scanForAnnotatedClasses(packages);
        
        // Then - Should find our test classes
        assertThat(annotatedClasses).hasSize(4);
        assertThat(annotatedClasses).contains(
            TestLibraryBook.class,
            TestLibraryAuthor.class,
            TestCreateBookInput.class,
            TestBookStatus.class
        );
        
        // When - Generate GraphQL types
        GraphQLType bookType = typeResolver.resolveType(TestLibraryBook.class);
        GraphQLType authorType = typeResolver.resolveType(TestLibraryAuthor.class);
        GraphQLType inputType = typeResolver.resolveType(TestCreateBookInput.class);
        GraphQLType enumType = typeResolver.resolveType(TestBookStatus.class);
        
        // Then - Verify type generation
        assertThat(bookType).isInstanceOf(GraphQLObjectType.class);
        assertThat(bookType.getName()).isEqualTo("LibraryBook");
        
        assertThat(authorType).isInstanceOf(GraphQLObjectType.class);
        assertThat(authorType.getName()).isEqualTo("LibraryAuthor");
        
        assertThat(inputType).isInstanceOf(GraphQLInputObjectType.class);
        assertThat(inputType.getName()).isEqualTo("CreateBookInput");
        
        assertThat(enumType).isInstanceOf(GraphQLEnumType.class);
        assertThat(enumType.getName()).isEqualTo("BookStatus");
    }
    
    @Test
    @DisplayName("Should resolve fields correctly for complex types")
    void shouldResolveFieldsCorrectlyForComplexTypes() {
        // When
        List<GraphQLFieldDefinition> bookFields = fieldResolver.resolveFields(TestLibraryBook.class);
        List<GraphQLFieldDefinition> authorFields = fieldResolver.resolveFields(TestLibraryAuthor.class);
        
        // Then - Verify book fields
        assertThat(bookFields).hasSize(6); // id, title, isbn, author, status, displayTitle
        
        GraphQLFieldDefinition idField = findField(bookFields, "id");
        assertThat(idField).isNotNull();
        assertThat(idField.getType()).isInstanceOf(GraphQLNonNull.class); // ID should be non-null
        
        GraphQLFieldDefinition titleField = findField(bookFields, "title");
        assertThat(titleField).isNotNull();
        assertThat(titleField.getType()).isInstanceOf(GraphQLNonNull.class); // nullable = false
        
        GraphQLFieldDefinition authorField = findField(bookFields, "author");
        assertThat(authorField).isNotNull();
        assertThat(authorField.getDescription()).isEqualTo("Author of this book");
        
        GraphQLFieldDefinition displayTitleField = findField(bookFields, "displayTitle");
        assertThat(displayTitleField).isNotNull(); // Method-based field
        assertThat(displayTitleField.getDescription()).isEqualTo("Full display title");
        
        // Then - Verify author fields
        assertThat(authorFields).hasSize(4); // id, fullName, email, bookCount
        
        GraphQLFieldDefinition fullNameField = findField(authorFields, "fullName");
        assertThat(fullNameField).isNotNull();
        assertThat(fullNameField.getDescription()).isEqualTo("Full name of the author");
    }
    
    @Test
    @DisplayName("Should handle custom scalar types correctly")
    void shouldHandleCustomScalarTypesCorrectly() {
        // When
        GraphQLType dateTimeType = typeResolver.resolveType(LocalDateTime.class);
        
        // Then
        assertThat(dateTimeType).isInstanceOf(GraphQLScalarType.class);
        assertThat(dateTimeType.getName()).isEqualTo("DateTime");
        assertThat(((GraphQLScalarType) dateTimeType).getDescription())
            .contains("Custom scalar type for LocalDateTime");
    }
    
    @Test
    @DisplayName("Should respect @GraphQLIgnore annotations")
    void shouldRespectGraphQLIgnoreAnnotations() {
        // When
        List<GraphQLFieldDefinition> bookFields = fieldResolver.resolveFields(TestLibraryBook.class);
        
        // Then - Should not include ignored fields
        GraphQLFieldDefinition ignoredField = findField(bookFields, "internalNotes");
        assertThat(ignoredField).isNull();
    }
    
    @Test
    @DisplayName("Should handle enum values with descriptions")
    void shouldHandleEnumValuesWithDescriptions() {
        // When
        GraphQLEnumType enumType = (GraphQLEnumType) typeResolver.resolveType(TestBookStatus.class);
        
        // Then
        assertThat(enumType.getName()).isEqualTo("BookStatus");
        assertThat(enumType.getDescription()).isEqualTo("Status of a book");
        
        // Should have defined values (excluding DELETED which is ignored)
        List<GraphQLEnumValueDefinition> values = enumType.getValues();
        assertThat(values).hasSize(2); // AVAILABLE and BORROWED (DELETED is ignored)
        
        GraphQLEnumValueDefinition availableValue = findEnumValue(values, "AVAILABLE");
        assertThat(availableValue).isNotNull();
        assertThat(availableValue.getDescription()).isEqualTo("Book is available");
    }
    
    // === Helper Methods ===
    
    private GraphQLFieldDefinition findField(List<GraphQLFieldDefinition> fields, String name) {
        return fields.stream()
            .filter(field -> field.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    private GraphQLEnumValueDefinition findEnumValue(List<GraphQLEnumValueDefinition> values, String name) {
        return values.stream()
            .filter(value -> value.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    // === Test Entities ===
    
    @GraphQLType(name = "LibraryBook", description = "A book in the library system")
    public static class TestLibraryBook {
        @GraphQLId
        private Long id;
        
        @GraphQLField(description = "Title of the book", nullable = false)
        private String title;
        
        @GraphQLField(description = "ISBN of the book")
        private String isbn;
        
        @GraphQLField(description = "Author of this book")
        private TestLibraryAuthor author;
        
        @GraphQLField(description = "Current status")
        private TestBookStatus status;
        
        @GraphQLIgnore(reason = "Internal system field")
        private String internalNotes;
        
        private LocalDateTime createdAt;
        
        @GraphQLField(description = "Full display title")
        public String getDisplayTitle() {
            return title + " (ISBN: " + isbn + ")";
        }
        
        // Getters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getIsbn() { return isbn; }
        public TestLibraryAuthor getAuthor() { return author; }
        public TestBookStatus getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    @GraphQLType(name = "LibraryAuthor", description = "An author in the library system")
    public static class TestLibraryAuthor {
        @GraphQLId
        private Long id;
        
        @GraphQLField(description = "Email address")
        private String email;
        
        @GraphQLField(description = "Full name of the author")
        public String getFullName() {
            return "John Doe"; // Simplified for test
        }
        
        @GraphQLField(description = "Number of books written")
        public Integer getBookCount() {
            return 5; // Simplified for test
        }
        
        // Getters
        public Long getId() { return id; }
        public String getEmail() { return email; }
    }
    
    @GraphQLInput(name = "CreateBookInput", description = "Input for creating a new book")
    public static class TestCreateBookInput {
        @GraphQLInputField(required = true, description = "Book title")
        private String title;
        
        @GraphQLInputField(description = "Book ISBN")
        private String isbn;
        
        @GraphQLInputField(required = true, description = "Author ID")
        private Long authorId;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
    }
    
    @GraphQLEnum(name = "BookStatus", description = "Status of a book")
    public enum TestBookStatus {
        @GraphQLEnumValue(description = "Book is available")
        AVAILABLE,
        
        @GraphQLEnumValue(description = "Book is borrowed")
        BORROWED,
        
        @GraphQLIgnore
        DELETED
    }
}

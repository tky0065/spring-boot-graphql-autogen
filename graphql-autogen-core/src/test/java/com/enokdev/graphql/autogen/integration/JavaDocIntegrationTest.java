package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.JavaDocExtractor;
import com.enokdev.graphql.autogen.testmodel.BookStatus;
import com.enokdev.graphql.autogen.testmodel.BookWithJavaDoc;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for JavaDoc functionality in type and field generation.
 * Tests the complete flow from JavaDoc extraction to GraphQL schema generation.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class JavaDocIntegrationTest {

    private DefaultTypeResolver typeResolver;
    private DefaultFieldResolver fieldResolver;

    @BeforeEach
    void setUp() {
        typeResolver = new DefaultTypeResolver();
        fieldResolver = new DefaultFieldResolver(typeResolver);
    }

    @Test
    void testBookWithJavaDocTypeGeneration() {
        // Generate GraphQL Object Type
        GraphQLType graphqlType = typeResolver.resolveType(BookWithJavaDoc.class);
        
        assertNotNull(graphqlType);
        assertTrue(graphqlType instanceof GraphQLObjectType);
        
        GraphQLObjectType objectType = (GraphQLObjectType) graphqlType;
        assertEquals("Book", objectType.getName());
        
        // The description should come from JavaDoc (or annotation if present)
        String description = objectType.getDescription();
        assertNotNull(description);
        // Note: In actual usage with source files, this would contain the JavaDoc
    }

    @Test
    void testBookStatusEnumGeneration() {
        // Generate GraphQL Enum Type
        GraphQLType graphqlType = typeResolver.resolveType(BookStatus.class);
        
        assertNotNull(graphqlType);
        assertTrue(graphqlType instanceof GraphQLEnumType);
        
        GraphQLEnumType enumType = (GraphQLEnumType) graphqlType;
        assertEquals("BookStatus", enumType.getName());
        
        // Check enum values
        List<GraphQLEnumValueDefinition> values = enumType.getValues();
        assertFalse(values.isEmpty());
        
        // Should not include the @GraphQLIgnore value
        boolean hasInternalProcessing = values.stream()
            .anyMatch(v -> "INTERNAL_PROCESSING".equals(v.getName()));
        assertFalse(hasInternalProcessing, "INTERNAL_PROCESSING should be ignored");
        
        // Check that enum values have descriptions
        GraphQLEnumValueDefinition availableValue = values.stream()
            .filter(v -> "AVAILABLE".equals(v.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(availableValue);
        assertNotNull(availableValue.getDescription());
    }

    @Test
    void testFieldDescriptionExtraction() throws NoSuchFieldException {
        // Test field with annotation description (should take precedence)
        Field titleField = BookWithJavaDoc.class.getDeclaredField("title");
        String titleDescription = JavaDocExtractor.extractDescriptionWithFallback(
            titleField, "Custom title description"
        );
        assertEquals("Custom title description", titleDescription);
        
        // Test field without annotation description (should try JavaDoc)
        Field isbnField = BookWithJavaDoc.class.getDeclaredField("isbn");
        String isbnDescription = JavaDocExtractor.extractDescriptionWithFallback(
            isbnField, ""
        );
        assertNotNull(isbnDescription);
    }

    @Test
    void testMethodDescriptionExtraction() throws NoSuchMethodException {
        // Test method with custom GraphQL field
        Method fullTitleMethod = BookWithJavaDoc.class.getDeclaredMethod("getFullTitle");
        String description = JavaDocExtractor.extractDescriptionWithFallback(
            fullTitleMethod, "Title with publication year"
        );
        assertEquals("Title with publication year", description);
        
        // Test method without annotation description
        Method readingTimeMethod = BookWithJavaDoc.class.getDeclaredMethod("getEstimatedReadingTime");
        String readingTimeDescription = JavaDocExtractor.extractDescriptionWithFallback(
            readingTimeMethod, ""
        );
        assertNotNull(readingTimeDescription);
    }

    @Test
    void testFieldResolverWithJavaDoc() {
        // Resolve all fields for the BookWithJavaDoc class
        List<GraphQLFieldDefinition> fields = fieldResolver.resolveFields(BookWithJavaDoc.class);
        
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        
        // Find the ID field
        GraphQLFieldDefinition idField = fields.stream()
            .filter(f -> "id".equals(f.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(idField);
        assertEquals(Scalars.GraphQLID, idField.getType());
        
        // Find the title field
        GraphQLFieldDefinition titleField = fields.stream()
            .filter(f -> "title".equals(f.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(titleField);
        assertTrue(titleField.getType() instanceof GraphQLNonNull);
        
        // Find computed field
        GraphQLFieldDefinition fullTitleField = fields.stream()
            .filter(f -> "fullTitle".equals(f.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(fullTitleField);
        assertEquals("Title with publication year", fullTitleField.getDescription());
        
        // Ensure ignored field is not present
        boolean hasInternalCode = fields.stream()
            .anyMatch(f -> "internalCode".equals(f.getName()));
        assertFalse(hasInternalCode, "Internal code field should be ignored");
    }

    @Test
    void testCompleteSchemaGeneration() {
        // This is a basic test to ensure no exceptions are thrown during schema generation
        
        // Generate the main type
        GraphQLType bookType = typeResolver.resolveType(BookWithJavaDoc.class);
        assertNotNull(bookType);
        
        // Generate the enum type
        GraphQLType statusType = typeResolver.resolveType(BookStatus.class);
        assertNotNull(statusType);
        
        // Generate fields
        List<GraphQLFieldDefinition> fields = fieldResolver.resolveFields(BookWithJavaDoc.class);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        
        // Verify that the schema generation process completes without errors
        assertTrue(fields.size() > 5, "Should have resolved multiple fields");
    }

    @Test
    void testJavaDocFallbackBehavior() throws NoSuchFieldException {
        // Test that when annotation description is empty, it falls back to JavaDoc
        Field pageCountField = BookWithJavaDoc.class.getDeclaredField("pageCount");
        
        // Empty annotation description should trigger JavaDoc lookup
        String description1 = JavaDocExtractor.extractDescriptionWithFallback(pageCountField, "");
        assertNotNull(description1);
        
        // Null annotation description should trigger JavaDoc lookup
        String description2 = JavaDocExtractor.extractDescriptionWithFallback(pageCountField, null);
        assertNotNull(description2);
        
        // Whitespace-only annotation description should trigger JavaDoc lookup
        String description3 = JavaDocExtractor.extractDescriptionWithFallback(pageCountField, "   \n\t  ");
        assertNotNull(description3);
        
        // Non-empty annotation description should be used directly
        String description4 = JavaDocExtractor.extractDescriptionWithFallback(pageCountField, "Custom description");
        assertEquals("Custom description", description4);
    }
}

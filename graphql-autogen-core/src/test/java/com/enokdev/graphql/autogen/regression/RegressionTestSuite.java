package com.enokdev.graphql.autogen.regression;

import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import com.enokdev.graphql.autogen.testmodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test suite to ensure existing functionality continues to work.
 * These tests verify that new changes don't break existing features.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class RegressionTestSuite {

    private DefaultSchemaGenerator schemaGenerator;
    private DefaultAnnotationScanner scanner;
    private DefaultTypeResolver typeResolver;
    private DefaultFieldResolver fieldResolver;
    private DefaultOperationResolver operationResolver;
    private DefaultPaginationGenerator paginationGenerator;
    private DefaultDataLoaderGeneratorComplete dataLoaderGenerator;

    @BeforeEach
    void setUp() {
        schemaGenerator = new DefaultSchemaGenerator();
        scanner = new DefaultAnnotationScanner();
        typeResolver = new DefaultTypeResolver();
        fieldResolver = new DefaultFieldResolver();
        operationResolver = new DefaultOperationResolver();
        paginationGenerator = new DefaultPaginationGenerator();
        dataLoaderGenerator = new DefaultDataLoaderGeneratorComplete();
    }

    @Test
    void testBasicTypeGeneration() {
        // Regression test: Basic type generation should still work
        String schema = schemaGenerator.generateSchema(List.of(Book.class));
        
        assertNotNull(schema);
        assertTrue(schema.contains("type Book"));
        assertTrue(schema.contains("id: ID"));
        assertTrue(schema.contains("title: String"));
        
        System.out.println("✅ Basic type generation regression test passed");
    }

    @Test
    void testInputTypeGeneration() {
        // Regression test: Input type generation should still work
        String schema = schemaGenerator.generateSchema(List.of(CreateBookInput.class));
        
        assertNotNull(schema);
        assertTrue(schema.contains("input CreateBookInput"));
        assertTrue(schema.contains("title: String!"));
        assertTrue(schema.contains("authorId: ID!"));
        
        System.out.println("✅ Input type generation regression test passed");
    }

    @Test
    void testEnumGeneration() {
        // Regression test: Enum generation should still work
        String schema = schemaGenerator.generateSchema(List.of(BookStatus.class));
        
        assertNotNull(schema);
        assertTrue(schema.contains("enum BookStatus"));
        assertTrue(schema.contains("AVAILABLE"));
        assertTrue(schema.contains("BORROWED"));
        
        System.out.println("✅ Enum generation regression test passed");
    }

    @Test
    void testJavaDocExtraction() {
        // Regression test: JavaDoc extraction should still work
        String schema = schemaGenerator.generateSchema(List.of(BookWithJavaDoc.class));
        
        assertNotNull(schema);
        // Should contain descriptions from JavaDoc
        assertTrue(schema.contains("\"") || schema.contains("#"), "Should contain descriptions");
        
        System.out.println("✅ JavaDoc extraction regression test passed");
    }

    @Test
    void testInterfaceGeneration() {
        // Regression test: Interface generation should still work
        String schema = schemaGenerator.generateSchema(List.of(BookWithInterfaces.class, Node.class));
        
        assertNotNull(schema);
        assertTrue(schema.contains("interface Node"));
        assertTrue(schema.contains("implements Node"));
        
        System.out.println("✅ Interface generation regression test passed");
    }

    @Test
    void testUnionGeneration() {
        // Regression test: Union generation should still work
        String schema = schemaGenerator.generateSchema(List.of(SearchResult.class, Book.class, Author.class));
        
        assertNotNull(schema);
        assertTrue(schema.contains("union SearchResult"));
        
        System.out.println("✅ Union generation regression test passed");
    }

    @Test
    void testDataLoaderGeneration() {
        // Regression test: DataLoader generation should still work
        List<DataLoaderGenerator.DataLoaderConfiguration> configs = 
            dataLoaderGenerator.generateDataLoaders(BookWithDataLoader.class);
        
        assertNotNull(configs);
        assertFalse(configs.isEmpty());
        
        // Verify configuration generation
        for (DataLoaderGenerator.DataLoaderConfiguration config : configs) {
            assertNotNull(config.getName());
            assertNotNull(config.getKeyType());
            assertNotNull(config.getValueType());
        }
        
        System.out.println("✅ DataLoader generation regression test passed");
    }

    @Test
    void testPaginationGeneration() {
        // Regression test: Pagination generation should still work
        List<PaginationGenerator.PaginationConfiguration> configs = 
            paginationGenerator.generatePaginationConfigurations(BookWithPagination.class);
        
        assertNotNull(configs);
        assertFalse(configs.isEmpty());
        
        // Verify configuration generation
        for (PaginationGenerator.PaginationConfiguration config : configs) {
            assertNotNull(config.getConnectionName());
            assertNotNull(config.getEdgeName());
            assertNotNull(config.getNodeTypeName());
        }
        
        System.out.println("✅ Pagination generation regression test passed");
    }

    @Test
    void testAnnotationScanning() {
        // Regression test: Annotation scanning should still work
        List<Class<?>> graphqlTypes = scanner.scanForGraphQLTypes("com.enokdev.graphql.autogen.testmodel");
        List<Class<?>> inputTypes = scanner.scanForInputTypes("com.enokdev.graphql.autogen.testmodel");
        List<Class<?>> enumTypes = scanner.scanForEnumTypes("com.enokdev.graphql.autogen.testmodel");
        
        assertFalse(graphqlTypes.isEmpty(), "Should find GraphQL types");
        assertFalse(inputTypes.isEmpty(), "Should find input types");
        assertFalse(enumTypes.isEmpty(), "Should find enum types");
        
        System.out.println("✅ Annotation scanning regression test passed");
    }

    @Test
    void testTypeResolution() {
        // Regression test: Type resolution should still work for all basic types
        
        // Test primitive types
        assertEquals("String", typeResolver.resolveGraphQLTypeName(String.class));
        assertEquals("Int", typeResolver.resolveGraphQLTypeName(Integer.class));
        assertEquals("Float", typeResolver.resolveGraphQLTypeName(Double.class));
        assertEquals("Boolean", typeResolver.resolveGraphQLTypeName(Boolean.class));
        assertEquals("ID", typeResolver.resolveGraphQLTypeName(Long.class));
        
        // Test complex types
        assertTrue(typeResolver.isGraphQLType(Book.class));
        assertTrue(typeResolver.isInputType(CreateBookInput.class));
        assertTrue(typeResolver.isEnumType(BookStatus.class));
        
        System.out.println("✅ Type resolution regression test passed");
    }

    @Test
    void testFieldResolution() {
        // Regression test: Field resolution should still work
        List<String> fields = fieldResolver.resolveFieldNames(Book.class);
        
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertTrue(fields.contains("id"));
        assertTrue(fields.contains("title"));
        
        System.out.println("✅ Field resolution regression test passed");
    }

    @Test
    void testCompleteWorkflow() {
        // Regression test: Complete workflow should still work end-to-end
        List<Class<?>> allClasses = List.of(
            Book.class,
            Author.class,
            BookStatus.class,
            CreateBookInput.class,
            UpdateBookInput.class,
            BookWithDataLoader.class,
            BookWithPagination.class,
            Node.class,
            SearchResult.class
        );
        
        String schema = schemaGenerator.generateSchema(allClasses);
        
        assertNotNull(schema);
        assertFalse(schema.trim().isEmpty());
        
        // Verify all types are present
        assertTrue(schema.contains("type Book"));
        assertTrue(schema.contains("type Author"));
        assertTrue(schema.contains("enum BookStatus"));
        assertTrue(schema.contains("input CreateBookInput"));
        assertTrue(schema.contains("input UpdateBookInput"));
        assertTrue(schema.contains("interface Node"));
        assertTrue(schema.contains("union SearchResult"));
        
        // Verify pagination types are generated
        assertTrue(schema.contains("Connection"));
        assertTrue(schema.contains("Edge"));
        assertTrue(schema.contains("PageInfo"));
        
        System.out.println("✅ Complete workflow regression test passed");
        System.out.println("Generated schema length: " + schema.length() + " characters");
    }

    @Test
    void testBackwardCompatibility() {
        // Regression test: Ensure backward compatibility with previous versions
        
        // Test that old annotation patterns still work
        String schema = schemaGenerator.generateSchema(List.of(Book.class));
        
        assertNotNull(schema);
        
        // Verify standard GraphQL schema format
        assertFalse(schema.contains("class "), "Should not contain Java syntax");
        assertFalse(schema.contains("public "), "Should not contain Java modifiers");
        assertFalse(schema.contains("private "), "Should not contain Java modifiers");
        assertTrue(schema.contains("type "), "Should contain GraphQL type definitions");
        
        // Verify proper GraphQL syntax
        assertTrue(schema.matches(".*type\\s+\\w+\\s*\\{.*"), "Should have proper type syntax");
        
        System.out.println("✅ Backward compatibility regression test passed");
    }
}

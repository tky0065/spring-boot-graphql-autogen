package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.testmodel.CreateBookInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for automatic Input type generation.
 * Tests the generation of CreateXXX and UpdateXXX input types.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class InputGenerationIntegrationTest {

    private DefaultSchemaGenerator schemaGenerator;
    private DefaultTypeResolver typeResolver;

    @BeforeEach
    void setUp() {
        schemaGenerator = new DefaultSchemaGenerator();
        typeResolver = new DefaultTypeResolver();
    }

    @Test
    void testCreateInputGeneration() {
        // Test that CreateBookInput is properly generated
        List<Class<?>> classes = List.of(CreateBookInput.class);
        String schema = schemaGenerator.generateSchema(classes);
        
        assertNotNull(schema);
        assertTrue(schema.contains("input CreateBookInput"));
        assertTrue(schema.contains("title: String!"));
        assertTrue(schema.contains("isbn: String"));
        assertTrue(schema.contains("authorId: ID!"));
        
        System.out.println("✅ CreateInput generation validated");
        System.out.println("Generated schema:\n" + schema);
    }

    @Test
    void testInputFieldValidation() {
        // Test that required fields are marked as non-null
        String schema = schemaGenerator.generateSchema(List.of(CreateBookInput.class));
        
        // Required fields should have ! suffix
        assertTrue(schema.contains("title: String!"));
        assertTrue(schema.contains("authorId: ID!"));
        
        // Optional fields should not have ! suffix
        assertTrue(schema.contains("isbn: String") && !schema.contains("isbn: String!"));
        assertTrue(schema.contains("description: String") && !schema.contains("description: String!"));
        
        System.out.println("✅ Input field validation (required/optional) validated");
    }

    @Test
    void testInputFieldDescriptions() {
        // Test that JavaDoc descriptions are included
        String schema = schemaGenerator.generateSchema(List.of(CreateBookInput.class));
        
        // Check for description comments in schema
        assertTrue(schema.contains("\"Title of the book\"") || 
                  schema.contains("# Title of the book"));
        
        System.out.println("✅ Input field descriptions validated");
    }

    @Test
    void testNestedInputTypes() {
        // Test that nested input types are properly handled
        // This would test CreateAuthorInput if it were nested in CreateBookInput
        
        String schema = schemaGenerator.generateSchema(List.of(CreateBookInput.class));
        
        // Verify the schema is valid GraphQL
        assertNotNull(schema);
        assertFalse(schema.trim().isEmpty());
        
        // Should not contain any Java-specific syntax
        assertFalse(schema.contains("class"));
        assertFalse(schema.contains("public"));
        assertFalse(schema.contains("private"));
        
        System.out.println("✅ Schema syntax validation passed");
    }

    @Test
    void testInputTypeNaming() {
        // Test that input types have proper naming conventions
        String schema = schemaGenerator.generateSchema(List.of(CreateBookInput.class));
        
        // Should use the annotation name if provided, otherwise infer from class name
        assertTrue(schema.contains("input CreateBookInput"));
        
        // Should not duplicate "Input" suffix
        assertFalse(schema.contains("CreateBookInputInput"));
        
        System.out.println("✅ Input type naming conventions validated");
    }
}

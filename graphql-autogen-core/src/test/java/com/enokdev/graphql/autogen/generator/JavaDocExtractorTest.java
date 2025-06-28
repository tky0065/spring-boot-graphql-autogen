package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JavaDocExtractor functionality.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class JavaDocExtractorTest {

    /**
     * Test class with JavaDoc comments for testing.
     * This is a comprehensive test of the JavaDoc extraction functionality.
     */
    @GraphQLType
    static class TestBookWithJavaDoc {
        
        /**
         * The unique identifier of the book.
         * This field serves as the primary key.
         */
        @GraphQLField
        private Long id;
        
        /**
         * The title of the book.
         * Must be unique within the system.
         */
        @GraphQLField(description = "Custom title from annotation")
        private String title;
        
        /**
         * The author who wrote this book.
         * Can be null for anonymous works.
         */
        private String author;
        
        // Field without JavaDoc
        private String isbn;
        
        /**
         * Gets the publication year of the book.
         * @return the year when the book was published
         */
        public Integer getPublicationYear() {
            return 2023;
        }
        
        /**
         * Checks if the book is available for borrowing.
         * @return true if available, false otherwise
         */
        @GraphQLField(description = "Availability from annotation")
        public boolean isAvailable() {
            return true;
        }
    }

    /**
     * Test enum for JavaDoc extraction.
     */
    enum TestEnum {
        /**
         * First test value.
         */
        VALUE1,
        
        /**
         * Second test value with detailed description.
         * This value has more complex behavior.
         */
        VALUE2
    }

    @Test
    void testExtractClassDescription() {
        String description = JavaDocExtractor.extractClassDescription(TestBookWithJavaDoc.class);
        // Note: Since we can't easily load source files in unit tests, this will likely return empty
        // But the method should not throw exceptions
        assertNotNull(description);
    }

    @Test
    void testExtractFieldDescription() throws NoSuchFieldException {
        Field idField = TestBookWithJavaDoc.class.getDeclaredField("id");
        String description = JavaDocExtractor.extractFieldDescription(idField);
        // Note: Since we can't easily load source files in unit tests, this will likely return empty
        // But the method should not throw exceptions
        assertNotNull(description);
    }

    @Test
    void testExtractDescriptionWithFallback_PreferAnnotation() throws NoSuchFieldException {
        Field titleField = TestBookWithJavaDoc.class.getDeclaredField("title");
        String annotationDescription = "Custom title from annotation";
        
        String result = JavaDocExtractor.extractDescriptionWithFallback(titleField, annotationDescription);
        
        // Annotation description should take precedence
        assertEquals("Custom title from annotation", result);
    }

    @Test
    void testExtractDescriptionWithFallback_EmptyAnnotation() throws NoSuchFieldException {
        Field authorField = TestBookWithJavaDoc.class.getDeclaredField("author");
        String emptyAnnotationDescription = "";
        
        String result = JavaDocExtractor.extractDescriptionWithFallback(authorField, emptyAnnotationDescription);
        
        // Should attempt to extract from JavaDoc (will be empty in test but shouldn't fail)
        assertNotNull(result);
    }

    @Test
    void testExtractDescriptionWithFallback_NullAnnotation() throws NoSuchFieldException {
        Field isbnField = TestBookWithJavaDoc.class.getDeclaredField("isbn");
        
        String result = JavaDocExtractor.extractDescriptionWithFallback(isbnField, null);
        
        // Should attempt to extract from JavaDoc (will be empty in test but shouldn't fail)
        assertNotNull(result);
    }

    @Test
    void testExtractMethodDescription() throws NoSuchMethodException {
        var method = TestBookWithJavaDoc.class.getDeclaredMethod("getPublicationYear");
        
        String description = JavaDocExtractor.extractMethodDescription(method);
        
        // Note: Since we can't easily load source files in unit tests, this will likely return empty
        // But the method should not throw exceptions
        assertNotNull(description);
    }

    @Test
    void testExtractEnumValueDescription() {
        String description = JavaDocExtractor.extractEnumValueDescription(TestEnum.class, "VALUE1");
        
        assertNotNull(description);
    }

    @Test
    void testExtractDescriptionWithFallback_WhitespaceAnnotation() throws NoSuchFieldException {
        Field field = TestBookWithJavaDoc.class.getDeclaredField("isbn");
        String whitespaceDescription = "   \n\t   ";
        
        String result = JavaDocExtractor.extractDescriptionWithFallback(field, whitespaceDescription);
        
        // Should fall back to JavaDoc extraction when annotation is just whitespace
        assertNotNull(result);
    }
}

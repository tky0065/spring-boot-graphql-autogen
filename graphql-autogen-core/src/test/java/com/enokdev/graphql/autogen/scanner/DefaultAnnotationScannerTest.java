package com.enokdev.graphql.autogen.scanner;

import com.enokdev.graphql.autogen.annotation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

/**
 * Tests for DefaultAnnotationScanner.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class DefaultAnnotationScannerTest {
    
    private DefaultAnnotationScanner scanner;
    
    @BeforeEach
    void setUp() {
        scanner = new DefaultAnnotationScanner();
    }
    
    @Test
    @DisplayName("Should return empty set for null base packages")
    void shouldReturnEmptySetForNullPackages() {
        // When
        Set<Class<?>> result = scanner.scanForAnnotatedClasses(null);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should return empty set for empty base packages")
    void shouldReturnEmptySetForEmptyPackages() {
        // When
        Set<Class<?>> result = scanner.scanForAnnotatedClasses(List.of());
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should detect GraphQL annotations on test classes")
    void shouldDetectGraphQLAnnotationsOnTestClasses() {
        // Given
        List<String> packages = List.of("com.enokdev.graphql.autogen.scanner");
        
        // When
        Set<Class<?>> result = scanner.scanForAnnotatedClasses(packages);
        
        // Then
        assertThat(result).contains(TestGraphQLType.class, TestGraphQLController.class);
    }
    
    @Test
    @DisplayName("Should detect specific annotation types")
    void shouldDetectSpecificAnnotationTypes() {
        // Given
        List<String> packages = List.of("com.enokdev.graphql.autogen.scanner");
        
        // When
        Set<Class<?>> types = scanner.scanForGraphQLTypes(packages);
        Set<Class<?>> controllers = scanner.scanForGraphQLControllers(packages);
        
        // Then
        assertThat(types).contains(TestGraphQLType.class);
        assertThat(controllers).contains(TestGraphQLController.class);
    }
    
    @Test
    @DisplayName("Should identify classes with GraphQL annotations")
    void shouldIdentifyClassesWithGraphQLAnnotations() {
        // When & Then
        assertThat(scanner.hasGraphQLAnnotations(TestGraphQLType.class)).isTrue();
        assertThat(scanner.hasGraphQLAnnotations(TestGraphQLController.class)).isTrue();
        assertThat(scanner.hasGraphQLAnnotations(String.class)).isFalse();
        assertThat(scanner.hasGraphQLAnnotations(null)).isFalse();
    }
    
    @Test
    @DisplayName("Should filter out invalid classes")
    void shouldFilterOutInvalidClasses() {
        // Given
        Set<Class<?>> classes = Set.of(
            TestGraphQLType.class,          // Valid
            TestInterface.class,            // Invalid - interface
            TestAbstractClass.class,        // Invalid - abstract
            String.class                    // Valid but no annotations
        );
        
        // When
        Set<Class<?>> result = scanner.filterValidClasses(classes);
        
        // Then
        assertThat(result).contains(TestGraphQLType.class, String.class);
        assertThat(result).doesNotContain(TestInterface.class, TestAbstractClass.class);
    }
    
    @Test
    @DisplayName("Should handle scanning errors gracefully")
    void shouldHandleScanningErrorsGracefully() {
        // Given - non-existent package
        List<String> packages = List.of("com.nonexistent.package");
        
        // When
        Set<Class<?>> result = scanner.scanForAnnotatedClasses(packages);
        
        // Then - should not throw exception and return empty set
        assertThat(result).isEmpty();
    }
    
    // === Test Classes ===
    
    @GraphQLType(name = "TestType", description = "Test GraphQL type")
    static class TestGraphQLType {
        @GraphQLId
        private Long id;
        
        @GraphQLField
        private String name;
    }
    
    @GraphQLController
    static class TestGraphQLController {
        @GraphQLQuery
        public TestGraphQLType getTest() {
            return new TestGraphQLType();
        }
        
        @GraphQLMutation
        public TestGraphQLType createTest() {
            return new TestGraphQLType();
        }
    }
    
    @GraphQLInput
    static class TestGraphQLInput {
        @GraphQLInputField(required = true)
        private String name;
    }
    
    @GraphQLEnum
    enum TestGraphQLEnum {
        VALUE1, VALUE2
    }
    
    // Invalid classes for testing
    interface TestInterface {
        void method();
    }
    
    abstract static class TestAbstractClass {
        abstract void method();
    }
}

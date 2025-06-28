package com.enokdev.graphql.autogen;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.exception.*;
import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.scanner.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Test to verify that all classes compile and imports work correctly.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class CompilationTest {
    
    @Test
    @DisplayName("Should compile all annotations without errors")
    void shouldCompileAllAnnotations() {
        // Verify annotation classes exist and are accessible
        assertThat(GraphQLType.class).isNotNull();
        assertThat(GraphQLField.class).isNotNull();
        assertThat(GraphQLId.class).isNotNull();
        assertThat(GraphQLIgnore.class).isNotNull();
        assertThat(GraphQLInput.class).isNotNull();
        assertThat(GraphQLInputField.class).isNotNull();
        assertThat(GraphQLEnum.class).isNotNull();
        assertThat(GraphQLEnumValue.class).isNotNull();
        assertThat(GraphQLQuery.class).isNotNull();
        assertThat(GraphQLMutation.class).isNotNull();
        assertThat(GraphQLSubscription.class).isNotNull();
        assertThat(GraphQLController.class).isNotNull();
        assertThat(GraphQLArgument.class).isNotNull();
        assertThat(GraphQLScalar.class).isNotNull();
    }
    
    @Test
    @DisplayName("Should compile all exception classes without errors")
    void shouldCompileAllExceptions() {
        // Verify exception classes exist and are accessible
        assertThat(SchemaGenerationException.class).isNotNull();
        assertThat(TypeResolutionException.class).isNotNull();
        
        // Test exception creation
        assertThatThrownBy(() -> {
            throw new SchemaGenerationException("Test message");
        }).isInstanceOf(SchemaGenerationException.class)
          .hasMessage("Test message");
        
        assertThatThrownBy(() -> {
            throw new TypeResolutionException("Test message", String.class);
        }).isInstanceOf(TypeResolutionException.class)
          .hasMessage("Test message");
    }
    
    @Test
    @DisplayName("Should compile all generator interfaces without errors")
    void shouldCompileAllGeneratorInterfaces() {
        // Verify interface classes exist and are accessible
        assertThat(SchemaGenerator.class).isNotNull();
        assertThat(AnnotationScanner.class).isNotNull();
    }
    
    @Test
    @DisplayName("Should compile scanner implementation without errors")
    void shouldCompileScannerImplementation() {
        // Verify scanner implementation exists and is accessible
        assertThat(DefaultAnnotationScanner.class).isNotNull();
        
        // Test scanner instantiation
        DefaultAnnotationScanner scanner = new DefaultAnnotationScanner();
        assertThat(scanner).isNotNull();
        assertThat(scanner).isInstanceOf(AnnotationScanner.class);
    }
    
    @Test
    @DisplayName("Should verify package structure is correct")
    void shouldVerifyPackageStructure() {
        // Verify all classes are in the correct enokdev package
        assertThat(GraphQLType.class.getPackageName()).startsWith("com.enokdev.graphql.autogen");
        assertThat(SchemaGenerationException.class.getPackageName()).startsWith("com.enokdev.graphql.autogen");
        assertThat(DefaultAnnotationScanner.class.getPackageName()).startsWith("com.enokdev.graphql.autogen");
        assertThat(SchemaGenerator.class.getPackageName()).startsWith("com.enokdev.graphql.autogen");
    }
}

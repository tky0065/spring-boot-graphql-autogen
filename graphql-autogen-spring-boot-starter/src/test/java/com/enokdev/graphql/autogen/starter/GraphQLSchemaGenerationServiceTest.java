
package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphQLSchemaGenerationServiceTest {

    @Mock
    private SchemaGenerator schemaGenerator;

    @Mock
    private AnnotationScanner annotationScanner;

    private GraphQLAutoGenProperties properties;

    private GraphQLSchemaGenerationService service;

    @BeforeEach
    void setUp() {
        properties = new GraphQLAutoGenProperties();
        service = new GraphQLSchemaGenerationService(schemaGenerator, annotationScanner, properties, new DefaultResourceLoader());
    }

    @Test
    void testGenerateSchema() throws IOException {
        Path tempDir = Files.createTempDirectory("schema-test");
        properties.setSchemaLocation(tempDir.toString());
        properties.setBasePackages(Collections.singletonList("com.example"));

        when(annotationScanner.scanForAnnotatedClasses(any())).thenReturn(Set.of(String.class));
        when(schemaGenerator.generateSchemaString(any())).thenReturn("type Query { hello: String }");

        service.generateSchema();

        Path schemaFile = tempDir.resolve("schema.graphqls");
        assertTrue(Files.exists(schemaFile));
        String content = Files.readString(schemaFile);
        assertEquals("type Query { hello: String }", content);
    }

    @Test
    void testGenerateSchemaWithNoAnnotatedClasses() {
        properties.setBasePackages(Collections.singletonList("com.example"));
        when(annotationScanner.scanForAnnotatedClasses(any())).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> service.generateSchema());
    }

    @Test
    void testGenerateSchemaWithIOException() throws IOException {
        Path tempDir = Files.createTempDirectory("schema-test");
        properties.setSchemaLocation(tempDir.toString());
        properties.setBasePackages(Collections.singletonList("com.example"));

        when(annotationScanner.scanForAnnotatedClasses(any())).thenReturn(Set.of(String.class));
        when(schemaGenerator.generateSchemaString(any())).thenReturn("schema");

        // Make the directory a file to cause an IOException
        Files.delete(tempDir);
        Files.createFile(tempDir);

        assertThrows(RuntimeException.class, () -> service.generateSchema());
    }
}

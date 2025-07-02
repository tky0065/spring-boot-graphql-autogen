package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.annotation.GraphQLController;
import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLQuery;
import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.generator.DefaultOperationResolver;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQLSchemaGenerationServiceIntegrationTest {

    private Path tempSchemaDir;

    @BeforeEach
    void setUp() throws IOException {
        tempSchemaDir = Files.createTempDirectory("graphql-schema-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempSchemaDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
    }

    @Test
    void testSchemaGenerationWithAnnotatedController() {
        // Given
        GraphQLAutoGenProperties properties = new GraphQLAutoGenProperties();
        properties.setEnabled(true);
        properties.setSchemaLocation(tempSchemaDir.toAbsolutePath().toString());
        properties.setBasePackages(List.of(TestController.class.getPackage().getName()));
        properties.setValidateSchema(false); // Désactiver la validation pour le test

        DefaultAnnotationScanner annotationScanner = new DefaultAnnotationScanner();
        DefaultTypeResolver typeResolver = new DefaultTypeResolver();
        DefaultFieldResolver fieldResolver = new DefaultFieldResolver(typeResolver);
        DefaultOperationResolver operationResolver = new DefaultOperationResolver(typeResolver);
        DefaultSchemaGenerator schemaGenerator = new DefaultSchemaGenerator(typeResolver, fieldResolver, operationResolver, annotationScanner);

        GraphQLSchemaGenerationService schemaGenerationService = new GraphQLSchemaGenerationService(
                schemaGenerator, annotationScanner, properties);

        // When
        schemaGenerationService.generateSchema();

        // Then
        Path generatedSchemaFile = Paths.get(tempSchemaDir.toAbsolutePath().toString(), "schema.graphqls");
        assertThat(Files.exists(generatedSchemaFile)).isTrue();
        try {
            String schemaContent = Files.readString(generatedSchemaFile);
            // Ne vérifie pas le contenu exact, vérifie juste que le schéma n'est pas vide
            assertThat(schemaContent).isNotEmpty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GraphQLController
    public static class TestController {
        @GraphQLQuery
        public TestType hello() {
            TestType result = new TestType();
            result.setValue("Hello World");
            return result;
        }
    }

    public static class TestType {
        private String value;

        @GraphQLField
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

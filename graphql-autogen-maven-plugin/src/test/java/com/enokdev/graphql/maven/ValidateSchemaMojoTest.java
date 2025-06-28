package com.enokdev.graphql.maven;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for ValidateSchemaMojo.
 *
 * @author GraphQL AutoGen
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ValidateSchemaMojoTest {

    @TempDir
    Path tempDir;

    @Mock
    private MavenProject project;

    private ValidateSchemaMojo mojo;

    @BeforeEach
    void setUp() {
        mojo = new ValidateSchemaMojo();
        setField(mojo, "project", project);
        setField(mojo, "schemaDirectory", tempDir.toFile());
        setField(mojo, "schemaFileName", "schema.graphqls");
    }

    @Test
    void shouldSkipValidationWhenSkipIsTrue() throws Exception {
        // Given
        setField(mojo, "skip", true);

        // When
        mojo.execute();

        // Then - No exception thrown
    }

    @Test
    void shouldFailWhenSchemaFileNotFound() {
        // Given
        setField(mojo, "failOnError", true);

        // When & Then
        assertThrows(Exception.class, () -> mojo.execute());
    }

    @Test
    void shouldWarnWhenSchemaFileNotFoundAndFailOnErrorIsFalse() throws Exception {
        // Given
        setField(mojo, "failOnError", false);

        // When & Then - Should not throw exception
        mojo.execute();
    }

    @Test
    void shouldValidateValidSchema() throws Exception {
        // Given
        String validSchema = """
            type Query {
                hello: String
            }
            
            type Book {
                id: ID!
                title: String!
                author: Author
            }
            
            type Author {
                id: ID!
                name: String!
                books: [Book!]!
            }
            """;
        
        Path schemaFile = tempDir.resolve("schema.graphqls");
        Files.write(schemaFile, validSchema.getBytes());

        // When & Then - Should not throw exception
        mojo.execute();
    }

    @Test
    void shouldFailOnInvalidSchema() throws IOException {
        // Given
        String invalidSchema = """
            type Query {
                hello: InvalidType
            }
            """;
        
        Path schemaFile = tempDir.resolve("schema.graphqls");
        Files.write(schemaFile, invalidSchema.getBytes());
        setField(mojo, "failOnError", true);

        // When & Then
        assertThrows(Exception.class, () -> mojo.execute());
    }

    @Test
    void shouldWarnOnInvalidSchemaWhenFailOnErrorIsFalse() throws Exception {
        // Given
        String invalidSchema = """
            type Query {
                hello: InvalidType
            }
            """;
        
        Path schemaFile = tempDir.resolve("schema.graphqls");
        Files.write(schemaFile, invalidSchema.getBytes());
        setField(mojo, "failOnError", false);

        // When & Then - Should not throw exception
        mojo.execute();
    }

    @Test
    void shouldValidateSchemaWithMutations() throws Exception {
        // Given
        String schemaWithMutations = """
            type Query {
                books: [Book!]!
                book(id: ID!): Book
            }
            
            type Mutation {
                createBook(input: CreateBookInput!): Book!
                updateBook(id: ID!, input: UpdateBookInput!): Book!
                deleteBook(id: ID!): Boolean!
            }
            
            type Book {
                id: ID!
                title: String!
                author: String!
            }
            
            input CreateBookInput {
                title: String!
                author: String!
            }
            
            input UpdateBookInput {
                title: String
                author: String
            }
            """;
        
        Path schemaFile = tempDir.resolve("schema.graphqls");
        Files.write(schemaFile, schemaWithMutations.getBytes());

        // When & Then - Should not throw exception
        mojo.execute();
    }

    @Test
    void shouldValidateSchemaWithSubscriptions() throws Exception {
        // Given
        String schemaWithSubscriptions = """
            type Query {
                books: [Book!]!
            }
            
            type Subscription {
                bookAdded: Book!
                bookUpdated: Book!
            }
            
            type Book {
                id: ID!
                title: String!
            }
            """;
        
        Path schemaFile = tempDir.resolve("schema.graphqls");
        Files.write(schemaFile, schemaWithSubscriptions.getBytes());

        // When & Then - Should not throw exception
        mojo.execute();
    }

    @Test
    void shouldFailOnSchemaWithoutQueryType() throws IOException {
        // Given
        String schemaWithoutQuery = """
            type Book {
                id: ID!
                title: String!
            }
            """;
        
        Path schemaFile = tempDir.resolve("schema.graphqls");
        Files.write(schemaFile, schemaWithoutQuery.getBytes());
        setField(mojo, "failOnError", true);

        // When & Then
        assertThrows(Exception.class, () -> mojo.execute());
    }

    /**
     * Helper method to set private fields using reflection.
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }
}

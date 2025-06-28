package com.enokdev.example.library;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;

/**
 * Integration test for the complete Library application with GraphQL AutoGen.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class LibraryApplicationIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    private final WebTestClient webTestClient;
    
    LibraryApplicationIntegrationTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }
    
    @Test
    @DisplayName("Should start application successfully")
    void shouldStartApplicationSuccessfully() {
        // Test passes if Spring Boot starts without errors
    }
    
    @Test
    @DisplayName("Should expose GraphQL endpoint")
    void shouldExposeGraphQLEndpoint() {
        String introspectionQuery = """
            {
                __schema {
                    types {
                        name
                        kind
                    }
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(introspectionQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.__schema.types").isNotEmpty();
    }
    
    @Test
    @DisplayName("Should expose GraphiQL playground")
    void shouldExposeGraphiQLPlayground() {
        webTestClient
            .get()
            .uri("/graphiql")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType("text/html;charset=UTF-8");
    }
    
    @Test
    @DisplayName("Should generate schema with Book types")
    void shouldGenerateSchemaWithBookTypes() {
        String typeQuery = """
            {
                __schema {
                    types {
                        name
                        fields {
                            name
                            type {
                                name
                            }
                        }
                    }
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(typeQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.__schema.types[?(@.name == 'Book')]").exists()
            .jsonPath("$.data.__schema.types[?(@.name == 'Author')]").exists()
            .jsonPath("$.data.__schema.types[?(@.name == 'BookStatus')]").exists();
    }
    
    @Test
    @DisplayName("Should execute book query successfully")
    void shouldExecuteBookQuerySuccessfully() {
        String bookQuery = """
            {
                book(id: "1") {
                    id
                    title
                    isbn
                    status
                    displayTitle
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(bookQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.book").isNotEmpty()
            .jsonPath("$.data.book.id").isEqualTo("1")
            .jsonPath("$.data.book.title").exists()
            .jsonPath("$.data.book.isbn").exists();
    }
    
    @Test
    @DisplayName("Should execute books search query with filters")
    void shouldExecuteBooksSearchQueryWithFilters() {
        String searchQuery = """
            {
                books(status: AVAILABLE, titleFilter: "Spring", limit: 5) {
                    id
                    title
                    status
                    author {
                        fullName
                        email
                    }
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(searchQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.books").isArray()
            .jsonPath("$.data.books").isNotEmpty();
    }
    
    @Test
    @DisplayName("Should execute create book mutation")
    void shouldExecuteCreateBookMutation() {
        String createMutation = """
            mutation {
                createBook(input: {
                    title: "Test Book"
                    description: "A test book"
                    isbn: "978-0-123456-78-9"
                    pageCount: 200
                    publicationYear: 2023
                    status: AVAILABLE
                    authorId: 1
                }) {
                    id
                    title
                    isbn
                    status
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(createMutation.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.createBook").isNotEmpty()
            .jsonPath("$.data.createBook.title").isEqualTo("Test Book")
            .jsonPath("$.data.createBook.isbn").isEqualTo("978-0-123456-78-9")
            .jsonPath("$.data.createBook.status").isEqualTo("AVAILABLE");
    }
    
    @Test
    @DisplayName("Should execute author operations")
    void shouldExecuteAuthorOperations() {
        // Test author query
        String authorQuery = """
            {
                authorFindById(id: "1") {
                    id
                    fullName
                    email
                    bookCount
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(authorQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.authorFindById").isNotEmpty()
            .jsonPath("$.data.authorFindById.id").isEqualTo("1")
            .jsonPath("$.data.authorFindById.fullName").exists();
    }
    
    @Test
    @DisplayName("Should handle GraphQL errors gracefully")
    void shouldHandleGraphQLErrorsGracefully() {
        String invalidQuery = """
            {
                nonExistentField {
                    id
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(invalidQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.errors").isNotEmpty()
            .jsonPath("$.errors[0].message").exists();
    }
    
    @Test
    @DisplayName("Should support enum values in queries")
    void shouldSupportEnumValuesInQueries() {
        String enumQuery = """
            {
                books(status: BORROWED) {
                    id
                    title
                    status
                }
            }
            """;
        
        webTestClient
            .post()
            .uri("/graphql")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "query": "%s"
                }
                """.formatted(enumQuery.replace("\n", "\\n")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.books").isArray();
    }
    
    @Test
    @DisplayName("Should expose H2 console for development")
    void shouldExposeH2ConsoleForDevelopment() {
        webTestClient
            .get()
            .uri("/h2-console")
            .exchange()
            .expectStatus().isOk();
    }
}

package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.generator.DefaultPaginationGenerator;
import com.enokdev.graphql.autogen.generator.PaginationGenerator;
import com.enokdev.graphql.autogen.testmodel.BookWithPagination;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLArgument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for pagination functionality.
 * Tests the complete pagination workflow from annotation to schema generation.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class PaginationIntegrationTest {

    private PaginationGenerator paginationGenerator;

    @BeforeEach
    void setUp() {
        paginationGenerator = new DefaultPaginationGenerator();
    }

    @Test
    void testCompletePaginationWorkflow() {
        // Test the complete workflow from annotation to schema types
        List<PaginationGenerator.PaginationConfiguration> configurations = 
            paginationGenerator.generatePaginationConfigurations(BookWithPagination.class);
        
        assertNotNull(configurations);
        assertEquals(3, configurations.size(), "Should detect 3 paginated methods");
        
        // Test each configuration generates proper types
        for (PaginationGenerator.PaginationConfiguration config : configurations) {
            // Generate Connection type
            GraphQLObjectType connectionType = paginationGenerator.generateConnectionType(config);
            assertNotNull(connectionType);
            assertTrue(connectionType.getName().endsWith("Connection"));
            
            // Generate Edge type
            GraphQLObjectType edgeType = paginationGenerator.generateEdgeType(config);
            assertNotNull(edgeType);
            assertTrue(edgeType.getName().endsWith("Edge"));
            
            // Generate arguments
            List<GraphQLArgument> arguments = paginationGenerator.generatePaginationArguments(config);
            assertNotNull(arguments);
            assertFalse(arguments.isEmpty());
        }
        
        System.out.println("✅ Pagination generation workflow completed successfully");
    }

    @Test
    void testRelayCursorPagination() throws Exception {
        var getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        var config = paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        assertNotNull(config);
        assertEquals("BookConnection", config.getConnectionName());
        assertEquals("BookEdge", config.getEdgeName());
        
        // Test Connection type
        var connectionType = paginationGenerator.generateConnectionType(config);
        assertNotNull(connectionType.getFieldDefinition("edges"));
        assertNotNull(connectionType.getFieldDefinition("pageInfo"));
        assertNotNull(connectionType.getFieldDefinition("totalCount"));
        
        // Test Edge type
        var edgeType = paginationGenerator.generateEdgeType(config);
        assertNotNull(edgeType.getFieldDefinition("node"));
        assertNotNull(edgeType.getFieldDefinition("cursor"));
        
        // Test arguments
        var arguments = paginationGenerator.generatePaginationArguments(config);
        assertEquals(4, arguments.size());
        assertTrue(arguments.stream().anyMatch(arg -> "first".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "after".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "last".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "before".equals(arg.getName())));
        
        System.out.println("✅ Relay cursor pagination validated");
    }

    @Test
    void testOffsetBasedPagination() throws Exception {
        var getReviewsMethod = BookWithPagination.class.getMethod("getReviews");
        var config = paginationGenerator.generatePaginationConfiguration(getReviewsMethod);
        
        assertNotNull(config);
        assertEquals("ReviewConnection", config.getConnectionName());
        
        // Test arguments
        var arguments = paginationGenerator.generatePaginationArguments(config);
        assertTrue(arguments.stream().anyMatch(arg -> "limit".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "offset".equals(arg.getName())));
        
        System.out.println("✅ Offset-based pagination validated");
    }

    @Test
    void testPageBasedPagination() throws Exception {
        var getAuthorsMethod = BookWithPagination.class.getMethod("getAuthors");
        var config = paginationGenerator.generatePaginationConfiguration(getAuthorsMethod);
        
        assertNotNull(config);
        assertEquals("AuthorConnection", config.getConnectionName());
        assertTrue(config.isGenerateSorting());
        
        // Test arguments include sorting
        var arguments = paginationGenerator.generatePaginationArguments(config);
        assertTrue(arguments.stream().anyMatch(arg -> "page".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "size".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "sortBy".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "sortDirection".equals(arg.getName())));
        
        System.out.println("✅ Page-based pagination with sorting validated");
    }

    @Test
    void testCustomPaginationWithFilters() throws Exception {
        var getCustomBooksMethod = BookWithPagination.class.getMethod("getCustomPaginatedBooks");
        var config = paginationGenerator.generatePaginationConfiguration(getCustomBooksMethod);
        
        assertNotNull(config);
        assertEquals("FilteredBookConnection", config.getConnectionName());
        assertTrue(config.isGenerateFilters());
        assertTrue(config.isGenerateSorting());
        
        // Test custom arguments
        assertEquals(2, config.getCustomArguments().length);
        assertEquals("category", config.getCustomArguments()[0]);
        assertEquals("minRating", config.getCustomArguments()[1]);
        
        // Test arguments include custom, filter, and sort arguments
        var arguments = paginationGenerator.generatePaginationArguments(config);
        assertTrue(arguments.stream().anyMatch(arg -> "category".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "minRating".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "filter".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "sortBy".equals(arg.getName())));
        
        System.out.println("✅ Custom pagination with filters and sorting validated");
    }

    @Test
    void testPageInfoTypeGeneration() {
        var pageInfoType = paginationGenerator.generatePageInfoType();
        
        assertNotNull(pageInfoType);
        assertEquals("PageInfo", pageInfoType.getName());
        
        // Verify all required PageInfo fields
        assertNotNull(pageInfoType.getFieldDefinition("hasNextPage"));
        assertNotNull(pageInfoType.getFieldDefinition("hasPreviousPage"));
        assertNotNull(pageInfoType.getFieldDefinition("startCursor"));
        assertNotNull(pageInfoType.getFieldDefinition("endCursor"));
        
        // PageInfo should be cached (same instance)
        var pageInfoType2 = paginationGenerator.generatePageInfoType();
        assertSame(pageInfoType, pageInfoType2);
        
        System.out.println("✅ PageInfo type generation and caching validated");
    }

    @Test
    void testConnectionTypeCaching() throws Exception {
        var getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        var config = paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        var connectionType1 = paginationGenerator.generateConnectionType(config);
        var connectionType2 = paginationGenerator.generateConnectionType(config);
        
        // Should return cached instance
        assertSame(connectionType1, connectionType2);
        
        System.out.println("✅ Connection type caching validated");
    }

    @Test
    void testEdgeTypeCaching() throws Exception {
        var getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        var config = paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        var edgeType1 = paginationGenerator.generateEdgeType(config);
        var edgeType2 = paginationGenerator.generateEdgeType(config);
        
        // Should return cached instance
        assertSame(edgeType1, edgeType2);
        
        System.out.println("✅ Edge type caching validated");
    }

    @Test
    void testDisabledPagination() throws Exception {
        var getArchivedBooksMethod = BookWithPagination.class.getMethod("getArchivedBooks");
        
        // Should not generate pagination for disabled methods
        assertFalse(paginationGenerator.shouldGeneratePagination(getArchivedBooksMethod));
        
        var config = paginationGenerator.generatePaginationConfiguration(getArchivedBooksMethod);
        assertNull(config);
        
        System.out.println("✅ Disabled pagination correctly ignored");
    }

    @Test
    void testNonPaginatedMethods() throws Exception {
        var getFeaturedBookMethod = BookWithPagination.class.getMethod("getFeaturedBook");
        
        // Should not generate pagination for non-annotated methods
        assertFalse(paginationGenerator.shouldGeneratePagination(getFeaturedBookMethod));
        
        var config = paginationGenerator.generatePaginationConfiguration(getFeaturedBookMethod);
        assertNull(config);
        
        System.out.println("✅ Non-paginated methods correctly ignored");
    }
}

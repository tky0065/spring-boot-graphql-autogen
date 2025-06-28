package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLPagination;
import com.enokdev.graphql.autogen.testmodel.BookWithPagination;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLArgument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for DefaultPaginationGenerator.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class DefaultPaginationGeneratorTest {

    private DefaultPaginationGenerator paginationGenerator;

    @BeforeEach
    void setUp() {
        paginationGenerator = new DefaultPaginationGenerator();
    }

    @Test
    void testShouldGeneratePagination() throws Exception {
        Method getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        assertTrue(paginationGenerator.shouldGeneratePagination(getBooksMethod));
        
        Method getTitleMethod = BookWithPagination.class.getMethod("getTitle");
        assertFalse(paginationGenerator.shouldGeneratePagination(getTitleMethod));
    }

    @Test
    void testGeneratePaginationConfiguration() throws Exception {
        Method getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        assertNotNull(config);
        assertEquals("BookConnection", config.getConnectionName());
        assertEquals("BookEdge", config.getEdgeName());
        assertEquals("Book", config.getNodeTypeName());
        assertEquals(GraphQLPagination.PaginationType.RELAY_CURSOR, config.getType());
        assertEquals(20, config.getPageSize());
        assertEquals(100, config.getMaxPageSize());
        assertTrue(config.isIncludeTotalCount());
        assertTrue(config.isIncludeEdges());
        assertTrue(config.isIncludePageInfo());
    }

    @Test
    void testGenerateConnectionType() throws Exception {
        Method getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        GraphQLObjectType connectionType = paginationGenerator.generateConnectionType(config);
        
        assertNotNull(connectionType);
        assertEquals("BookConnection", connectionType.getName());
        assertTrue(connectionType.getDescription().contains("connection"));
        
        // Check fields
        assertNotNull(connectionType.getFieldDefinition("edges"));
        assertNotNull(connectionType.getFieldDefinition("pageInfo"));
        assertNotNull(connectionType.getFieldDefinition("totalCount"));
    }

    @Test
    void testGenerateEdgeType() throws Exception {
        Method getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        GraphQLObjectType edgeType = paginationGenerator.generateEdgeType(config);
        
        assertNotNull(edgeType);
        assertEquals("BookEdge", edgeType.getName());
        assertTrue(edgeType.getDescription().contains("edge"));
        
        // Check fields
        assertNotNull(edgeType.getFieldDefinition("node"));
        assertNotNull(edgeType.getFieldDefinition("cursor"));
    }

    @Test
    void testGeneratePageInfoType() {
        GraphQLObjectType pageInfoType = paginationGenerator.generatePageInfoType();
        
        assertNotNull(pageInfoType);
        assertEquals("PageInfo", pageInfoType.getName());
        
        // Check fields
        assertNotNull(pageInfoType.getFieldDefinition("hasNextPage"));
        assertNotNull(pageInfoType.getFieldDefinition("hasPreviousPage"));
        assertNotNull(pageInfoType.getFieldDefinition("startCursor"));
        assertNotNull(pageInfoType.getFieldDefinition("endCursor"));
    }

    @Test
    void testGenerateRelayCursorArguments() throws Exception {
        Method getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        List<GraphQLArgument> arguments = paginationGenerator.generatePaginationArguments(config);
        
        assertNotNull(arguments);
        assertEquals(4, arguments.size());
        
        // Check standard Relay arguments
        assertTrue(arguments.stream().anyMatch(arg -> "first".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "after".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "last".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "before".equals(arg.getName())));
    }

    @Test
    void testGenerateOffsetBasedArguments() throws Exception {
        Method getReviewsMethod = BookWithPagination.class.getMethod("getReviews");
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getReviewsMethod);
        
        List<GraphQLArgument> arguments = paginationGenerator.generatePaginationArguments(config);
        
        assertNotNull(arguments);
        assertTrue(arguments.size() >= 2);
        
        // Check offset-based arguments
        assertTrue(arguments.stream().anyMatch(arg -> "limit".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "offset".equals(arg.getName())));
    }

    @Test
    void testGeneratePageBasedArguments() throws Exception {
        Method getAuthorsMethod = BookWithPagination.class.getMethod("getAuthors");
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getAuthorsMethod);
        
        List<GraphQLArgument> arguments = paginationGenerator.generatePaginationArguments(config);
        
        assertNotNull(arguments);
        assertTrue(arguments.size() >= 2);
        
        // Check page-based arguments
        assertTrue(arguments.stream().anyMatch(arg -> "page".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "size".equals(arg.getName())));
    }

    @Test
    void testGenerateConfigurationsForClass() {
        List<PaginationGenerator.PaginationConfiguration> configurations = 
            paginationGenerator.generatePaginationConfigurations(BookWithPagination.class);
        
        assertNotNull(configurations);
        assertEquals(3, configurations.size());
        
        // Verify different pagination types are detected
        assertTrue(configurations.stream().anyMatch(config -> 
            GraphQLPagination.PaginationType.RELAY_CURSOR.equals(config.getType())));
        assertTrue(configurations.stream().anyMatch(config -> 
            GraphQLPagination.PaginationType.OFFSET_BASED.equals(config.getType())));
        assertTrue(configurations.stream().anyMatch(config -> 
            GraphQLPagination.PaginationType.PAGE_BASED.equals(config.getType())));
    }

    @Test
    void testCacheConnectionTypes() throws Exception {
        Method getBooksMethod = BookWithPagination.class.getMethod("getBooks", Integer.class, String.class);
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getBooksMethod);
        
        GraphQLObjectType connectionType1 = paginationGenerator.generateConnectionType(config);
        GraphQLObjectType connectionType2 = paginationGenerator.generateConnectionType(config);
        
        // Should return the same cached instance
        assertSame(connectionType1, connectionType2);
    }

    @Test
    void testCustomArguments() throws Exception {
        Method getCustomPaginatedBooksMethod = BookWithPagination.class.getMethod("getCustomPaginatedBooks");
        PaginationGenerator.PaginationConfiguration config = 
            paginationGenerator.generatePaginationConfiguration(getCustomPaginatedBooksMethod);
        
        List<GraphQLArgument> arguments = paginationGenerator.generatePaginationArguments(config);
        
        // Should include custom arguments
        assertTrue(arguments.stream().anyMatch(arg -> "category".equals(arg.getName())));
        assertTrue(arguments.stream().anyMatch(arg -> "minRating".equals(arg.getName())));
    }
}

package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.testmodel.*;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for GraphQL interfaces and unions functionality.
 * Tests the complete flow from Java interfaces/classes to GraphQL schema generation.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class InterfaceAndUnionIntegrationTest {

    private DefaultTypeResolver typeResolver;
    private DefaultFieldResolver fieldResolver;

    @BeforeEach
    void setUp() {
        typeResolver = new DefaultTypeResolver();
        fieldResolver = new DefaultFieldResolver(typeResolver);
    }

    @Test
    void testGraphQLInterfaceGeneration() {
        // Generate GraphQL Interface Type for Node
        GraphQLType nodeType = typeResolver.resolveType(Node.class);
        
        assertNotNull(nodeType);
        assertTrue(nodeType instanceof GraphQLInterfaceType);
        
        GraphQLInterfaceType nodeInterface = (GraphQLInterfaceType) nodeType;
        assertEquals("Node", nodeInterface.getName());
        assertEquals("Common interface for identifiable entities", nodeInterface.getDescription());
        
        // Check that interface fields are properly resolved
        List<GraphQLFieldDefinition> interfaceFields = fieldResolver.resolveFields(Node.class);
        assertFalse(interfaceFields.isEmpty());
        
        // Should have id, createdAt, updatedAt fields
        assertTrue(interfaceFields.stream().anyMatch(f -> "id".equals(f.getName())));
        assertTrue(interfaceFields.stream().anyMatch(f -> "createdAt".equals(f.getName())));
        assertTrue(interfaceFields.stream().anyMatch(f -> "updatedAt".equals(f.getName())));
    }

    @Test
    void testSearchableInterfaceGeneration() {
        // Generate GraphQL Interface Type for Searchable
        GraphQLType searchableType = typeResolver.resolveType(Searchable.class);
        
        assertNotNull(searchableType);
        assertTrue(searchableType instanceof GraphQLInterfaceType);
        
        GraphQLInterfaceType searchableInterface = (GraphQLInterfaceType) searchableType;
        assertEquals("Searchable", searchableInterface.getName());
        assertEquals("Interface for searchable entities", searchableInterface.getDescription());
        
        // Check interface fields
        List<GraphQLFieldDefinition> interfaceFields = fieldResolver.resolveFields(Searchable.class);
        assertFalse(interfaceFields.isEmpty());
        
        // Should have searchScore and displayText fields
        assertTrue(interfaceFields.stream().anyMatch(f -> "searchScore".equals(f.getName())));
        assertTrue(interfaceFields.stream().anyMatch(f -> "displayText".equals(f.getName())));
    }

    @Test
    void testObjectTypeImplementsInterfaces() {
        // Generate GraphQL Object Type for BookWithInterfaces
        GraphQLType bookType = typeResolver.resolveType(BookWithInterfaces.class);
        
        assertNotNull(bookType);
        assertTrue(bookType instanceof GraphQLObjectType);
        
        GraphQLObjectType bookObjectType = (GraphQLObjectType) bookType;
        assertEquals("Book", bookObjectType.getName());
        assertEquals("A book that implements Node and Searchable interfaces", bookObjectType.getDescription());
        
        // Check that book implements the interfaces
        List<GraphQLInterfaceType> interfaces = bookObjectType.getInterfaces();
        assertEquals(2, interfaces.size());
        
        // Should implement Node and Searchable interfaces
        assertTrue(interfaces.stream().anyMatch(i -> "Node".equals(i.getName())));
        assertTrue(interfaces.stream().anyMatch(i -> "Searchable".equals(i.getName())));
    }

    @Test
    void testAuthorImplementsInterfaces() {
        // Generate GraphQL Object Type for Author
        GraphQLType authorType = typeResolver.resolveType(Author.class);
        
        assertNotNull(authorType);
        assertTrue(authorType instanceof GraphQLObjectType);
        
        GraphQLObjectType authorObjectType = (GraphQLObjectType) authorType;
        assertEquals("Author", authorObjectType.getName());
        
        // Check that author implements the interfaces
        List<GraphQLInterfaceType> interfaces = authorObjectType.getInterfaces();
        assertEquals(2, interfaces.size());
        
        // Should implement Node and Searchable interfaces
        assertTrue(interfaces.stream().anyMatch(i -> "Node".equals(i.getName())));
        assertTrue(interfaces.stream().anyMatch(i -> "Searchable".equals(i.getName())));
    }

    @Test
    void testGraphQLUnionGeneration() {
        // Generate GraphQL Union Type for SearchResult
        GraphQLType searchResultType = typeResolver.resolveType(SearchResult.class);
        
        assertNotNull(searchResultType);
        assertTrue(searchResultType instanceof GraphQLUnionType);
        
        GraphQLUnionType searchResultUnion = (GraphQLUnionType) searchResultType;
        assertEquals("SearchResult", searchResultUnion.getName());
        assertEquals("A search result that can be a book or an author", searchResultUnion.getDescription());
        
        // Check union possible types
        List<GraphQLObjectType> possibleTypes = searchResultUnion.getTypes();
        assertEquals(2, possibleTypes.size());
        
        // Should contain Book and Author types
        assertTrue(possibleTypes.stream().anyMatch(t -> "Book".equals(t.getName())));
        assertTrue(possibleTypes.stream().anyMatch(t -> "Author".equals(t.getName())));
    }

    @Test
    void testInterfaceFieldsResolvedCorrectly() {
        // Test that interface fields are properly resolved with descriptions
        List<GraphQLFieldDefinition> nodeFields = fieldResolver.resolveFields(Node.class);
        
        // Find the id field
        GraphQLFieldDefinition idField = nodeFields.stream()
            .filter(f -> "id".equals(f.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(idField);
        assertEquals("The unique identifier", idField.getDescription());
        assertTrue(idField.getType() instanceof GraphQLNonNull);
        
        // Find the createdAt field
        GraphQLFieldDefinition createdAtField = nodeFields.stream()
            .filter(f -> "createdAt".equals(f.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(createdAtField);
        assertEquals("Creation timestamp", createdAtField.getDescription());
        assertTrue(createdAtField.getType() instanceof GraphQLNonNull);
        
        // Find the updatedAt field (should be nullable)
        GraphQLFieldDefinition updatedAtField = nodeFields.stream()
            .filter(f -> "updatedAt".equals(f.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(updatedAtField);
        assertEquals("Last update timestamp", updatedAtField.getDescription());
        assertFalse(updatedAtField.getType() instanceof GraphQLNonNull);
    }

    @Test
    void testCompleteInterfaceAndUnionWorkflow() {
        // This test validates the complete workflow:
        // 1. Create interfaces
        // 2. Create types that implement interfaces
        // 3. Create unions with those types
        
        // Step 1: Create interfaces
        GraphQLInterfaceType nodeInterface = (GraphQLInterfaceType) typeResolver.resolveType(Node.class);
        GraphQLInterfaceType searchableInterface = (GraphQLInterfaceType) typeResolver.resolveType(Searchable.class);
        
        assertNotNull(nodeInterface);
        assertNotNull(searchableInterface);
        
        // Step 2: Create implementing types
        GraphQLObjectType bookType = (GraphQLObjectType) typeResolver.resolveType(BookWithInterfaces.class);
        GraphQLObjectType authorType = (GraphQLObjectType) typeResolver.resolveType(Author.class);
        
        assertNotNull(bookType);
        assertNotNull(authorType);
        
        // Verify implementations
        assertTrue(bookType.getInterfaces().contains(nodeInterface));
        assertTrue(bookType.getInterfaces().contains(searchableInterface));
        assertTrue(authorType.getInterfaces().contains(nodeInterface));
        assertTrue(authorType.getInterfaces().contains(searchableInterface));
        
        // Step 3: Create union
        GraphQLUnionType searchResultUnion = (GraphQLUnionType) typeResolver.resolveType(SearchResult.class);
        
        assertNotNull(searchResultUnion);
        assertTrue(searchResultUnion.getTypes().contains(bookType));
        assertTrue(searchResultUnion.getTypes().contains(authorType));
        
        // This validates that the complete GraphQL schema with interfaces and unions
        // can be generated successfully
        assertTrue(true, "Complete interface and union workflow succeeded");
    }
}

package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or method to support GraphQL pagination.
 * Automatically generates Connection and Edge types following the Relay specification.
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @GraphQLType
 * public class Author {
 *     @GraphQLField
 *     @GraphQLPagination(
 *         connectionName = "BookConnection",
 *         edgeName = "BookEdge",
 *         pageSize = 20
 *     )
 *     public List<Book> getBooks(
 *         @GraphQLArgument(name = "first") Integer first,
 *         @GraphQLArgument(name = "after") String after,
 *         @GraphQLArgument(name = "last") Integer last,
 *         @GraphQLArgument(name = "before") String before
 *     ) {
 *         return bookService.findByAuthorId(this.getId(), first, after, last, before);
 *     }
 * }
 * }
 * </pre>
 * 
 * <p>This will generate:</p>
 * <pre>
 * type BookConnection {
 *   edges: [BookEdge!]!
 *   pageInfo: PageInfo!
 *   totalCount: Int!
 * }
 * 
 * type BookEdge {
 *   node: Book!
 *   cursor: String!
 * }
 * 
 * type PageInfo {
 *   hasNextPage: Boolean!
 *   hasPreviousPage: Boolean!
 *   startCursor: String
 *   endCursor: String
 * }
 * </pre>
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLPagination {
    
    /**
     * The type of pagination to use.
     * Default: RELAY_CURSOR for Relay-style cursor-based pagination
     * 
     * @return the pagination type
     */
    PaginationType type() default PaginationType.RELAY_CURSOR;
    
    /**
     * The name of the Connection type.
     * If empty, will be generated from the element type (e.g., BookConnection).
     * 
     * @return the connection type name
     */
    String connectionName() default "";
    
    /**
     * The name of the Edge type.
     * If empty, will be generated from the element type (e.g., BookEdge).
     * 
     * @return the edge type name
     */
    String edgeName() default "";
    
    /**
     * The default page size if not specified in the query.
     * Default: 20
     * 
     * @return the default page size
     */
    int pageSize() default 20;
    
    /**
     * The maximum page size allowed.
     * Default: 100
     * 
     * @return the maximum page size
     */
    int maxPageSize() default 100;
    
    /**
     * Whether to include totalCount in the Connection.
     * Default: true
     * 
     * @return true if totalCount should be included
     */
    boolean includeTotalCount() default true;
    
    /**
     * Whether to include edges in the Connection.
     * Default: true
     * 
     * @return true if edges should be included
     */
    boolean includeEdges() default true;
    
    /**
     * Whether to include pageInfo in the Connection.
     * Default: true
     * 
     * @return true if pageInfo should be included
     */
    boolean includePageInfo() default true;
    
    /**
     * The cursor encoding strategy.
     * Default: BASE64 encoding of the ID
     * 
     * @return the cursor encoding strategy
     */
    CursorStrategy cursorStrategy() default CursorStrategy.BASE64_ID;
    
    /**
     * Custom arguments to add to the pagination field.
     * These will be added in addition to the standard first/after/last/before arguments.
     * 
     * @return array of custom argument names
     */
    String[] customArguments() default {};
    
    /**
     * Whether to generate filter arguments automatically.
     * If true, will add filter arguments based on the entity fields.
     * Default: false
     * 
     * @return true if filter arguments should be generated
     */
    boolean generateFilters() default false;
    
    /**
     * Whether to generate sorting arguments automatically.
     * If true, will add sorting arguments for common fields.
     * Default: false
     * 
     * @return true if sort arguments should be generated
     */
    boolean generateSorting() default false;
    
    /**
     * Whether this pagination should be enabled.
     * Default: true
     * 
     * @return true if pagination should be enabled
     */
    boolean enabled() default true;
    
    /**
     * Types of pagination strategies.
     */
    enum PaginationType {
        /**
         * Relay-style cursor-based pagination with first/after/last/before arguments.
         */
        RELAY_CURSOR,
        
        /**
         * Offset-based pagination with limit/offset arguments.
         */
        OFFSET_BASED,
        
        /**
         * Page-based pagination with page/size arguments.
         */
        PAGE_BASED,
        
        /**
         * Custom pagination implementation.
         */
        CUSTOM
    }
    
    /**
     * Strategies for encoding cursors.
     */
    enum CursorStrategy {
        /**
         * Base64 encode the entity ID.
         */
        BASE64_ID,
        
        /**
         * Use the entity ID directly as cursor.
         */
        PLAIN_ID,
        
        /**
         * Base64 encode a combination of multiple fields.
         */
        BASE64_COMPOSITE,
        
        /**
         * Use a timestamp-based cursor.
         */
        TIMESTAMP,
        
        /**
         * Custom cursor generation strategy.
         */
        CUSTOM
    }
}

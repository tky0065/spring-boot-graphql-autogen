package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or method to use a DataLoader for batch loading.
 * DataLoaders help solve the N+1 query problem by batching multiple requests into a single operation.
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @GraphQLType
 * public class Book {
 *     @GraphQLField
 *     @GraphQLDataLoader(batchSize = 100, cachingEnabled = true)
 *     private Author author;
 *     
 *     @GraphQLField
 *     @GraphQLDataLoader(keyProperty = "authorId")
 *     public Author getAuthor() {
 *         // This will be batched automatically
 *         return authorService.findById(this.authorId);
 *     }
 * }
 * 
 * @GraphQLType
 * public class Author {
 *     @GraphQLField
 *     @GraphQLDataLoader(batchSize = 50)
 *     private List<Book> books;
 * }
 * }
 * </pre>
 * 
 * <p>This will automatically generate DataLoader code like:</p>
 * <pre>
 * DataLoader&lt;Long, Author&gt; authorDataLoader = DataLoader.newDataLoader(
 *     authorIds -> authorService.findByIds(authorIds),
 *     DataLoaderOptions.newOptions().setBatchingEnabled(true).setCachingEnabled(true)
 * );
 * </pre>
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLDataLoader {
    
    /**
     * The name of the DataLoader.
     * If empty, will be generated from the field/method name.
     * 
     * @return the DataLoader name
     */
    String name() default "";
    
    /**
     * The batch size for the DataLoader.
     * Default: 100
     * 
     * @return the maximum batch size
     */
    int batchSize() default 100;
    
    /**
     * Whether caching is enabled for this DataLoader.
     * Default: true
     * 
     * @return true if caching should be enabled
     */
    boolean cachingEnabled() default true;
    
    /**
     * Whether batching is enabled for this DataLoader.
     * Default: true
     * 
     * @return true if batching should be enabled
     */
    boolean batchingEnabled() default true;
    
    /**
     * The property name to use as the key for batching.
     * If empty, will try to infer from the relationship.
     * 
     * <p>Examples:</p>
     * <ul>
     *   <li>"authorId" - for loading authors by their ID</li>
     *   <li>"categoryId" - for loading categories</li>
     *   <li>"id" - for loading by primary key</li>
     * </ul>
     * 
     * @return the key property name
     */
    String keyProperty() default "";
    
    /**
     * The service method to use for batch loading.
     * If empty, will try to infer from conventions.
     * 
     * <p>Examples:</p>
     * <ul>
     *   <li>"findByIds" - standard batch find method</li>
     *   <li>"loadAuthorsByIds" - custom batch method</li>
     *   <li>"getBatchedData" - generic batch method</li>
     * </ul>
     * 
     * @return the batch load method name
     */
    String batchLoadMethod() default "";
    
    /**
     * The service class that contains the batch loading method.
     * If empty, will try to infer from the entity type.
     * 
     * @return the service class
     */
    Class<?> serviceClass() default Object.class;
    
    /**
     * Maximum time to wait for a batch to be executed (in milliseconds).
     * Default: 50ms
     * 
     * @return the batch timeout in milliseconds
     */
    int batchTimeout() default 50;
    
    /**
     * Whether to enable statistics collection for this DataLoader.
     * Useful for monitoring performance in production.
     * Default: false
     * 
     * @return true if statistics should be collected
     */
    boolean statisticsEnabled() default false;
    
    /**
     * Custom cache key generation strategy.
     * Default: uses the key property value as cache key
     * 
     * @return the cache key generation strategy
     */
    CacheKeyStrategy cacheKeyStrategy() default CacheKeyStrategy.PROPERTY_VALUE;
    
    /**
     * Whether this DataLoader should be enabled.
     * Useful for disabling DataLoaders in certain environments.
     * Default: true
     * 
     * @return true if this DataLoader should be enabled
     */
    boolean enabled() default true;
    
    /**
     * Strategies for generating cache keys.
     */
    enum CacheKeyStrategy {
        /**
         * Use the property value directly as cache key.
         */
        PROPERTY_VALUE,
        
        /**
         * Use a hash of the property value as cache key.
         */
        PROPERTY_HASH,
        
        /**
         * Use a custom method to generate cache key.
         */
        CUSTOM_METHOD
    }
}

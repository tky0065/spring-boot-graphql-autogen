package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Java class to be converted to a GraphQL union.
 * GraphQL unions allow you to define a type that could be one of several types.
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @GraphQLUnion(
 *     name = "SearchResult", 
 *     types = {Book.class, Author.class, Publisher.class},
 *     description = "A search result that can be a book, author, or publisher"
 * )
 * public class SearchResult {
 *     // This class can be empty or contain common fields
 *     // The union will be generated based on the types array
 * }
 * }
 * </pre>
 * 
 * <p>This will generate:</p>
 * <pre>
 * union SearchResult = Book | Author | Publisher
 * </pre>
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLUnion {
    
    /**
     * The name of the GraphQL union.
     * If empty, the Java class name will be used.
     * 
     * @return the union name
     */
    String name() default "";
    
    /**
     * Description of the GraphQL union.
     * This will be included in the generated schema documentation.
     * If empty, JavaDoc comments will be used if available.
     * 
     * @return the union description
     */
    String description() default "";
    
    /**
     * The types that this union can represent.
     * All types must be annotated with @GraphQLType.
     * 
     * @return array of classes that are part of this union
     */
    Class<?>[] types();
    
    /**
     * Whether this union should be included in schema generation.
     * 
     * @return true if enabled, false otherwise
     */
    boolean enabled() default true;
}

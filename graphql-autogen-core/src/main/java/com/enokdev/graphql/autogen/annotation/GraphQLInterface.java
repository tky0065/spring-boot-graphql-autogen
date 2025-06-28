package com.enokdev.graphql.autogen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Java interface or abstract class to be converted to a GraphQL interface.
 * GraphQL interfaces allow you to define a set of fields that multiple types can implement.
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @GraphQLInterface(name = "Node")
 * public interface Identifiable {
 *     @GraphQLField
 *     String getId();
 *     
 *     @GraphQLField
 *     String getCreatedAt();
 * }
 * 
 * @GraphQLType
 * public class Book implements Identifiable {
 *     private String id;
 *     private String createdAt;
 *     private String title;
 *     
 *     @Override
 *     public String getId() {
 *         return id;
 *     }
 *     
 *     @Override
 *     public String getCreatedAt() {
 *         return createdAt;
 *     }
 *     
 *     @GraphQLField
 *     public String getTitle() {
 *         return title;
 *     }
 * }
 * }
 * </pre>
 * 
 * <p>This will generate:</p>
 * <pre>
 * interface Node {
 *   id: ID!
 *   createdAt: String!
 * }
 * 
 * type Book implements Node {
 *   id: ID!
 *   createdAt: String!
 *   title: String!
 * }
 * </pre>
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLInterface {
    
    /**
     * The name of the GraphQL interface.
     * If empty, the Java interface/class name will be used.
     * 
     * @return the interface name
     */
    String name() default "";
    
    /**
     * Description of the GraphQL interface.
     * This will be included in the generated schema documentation.
     * If empty, JavaDoc comments will be used if available.
     * 
     * @return the interface description
     */
    String description() default "";
    
    /**
     * Whether this interface should be included in schema generation.
     * 
     * @return true if enabled, false otherwise
     */
    boolean enabled() default true;
}

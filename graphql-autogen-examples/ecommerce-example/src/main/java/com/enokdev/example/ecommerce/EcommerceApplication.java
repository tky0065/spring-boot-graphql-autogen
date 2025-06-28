package com.enokdev.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * E-commerce example application demonstrating GraphQL AutoGen capabilities.
 * 
 * This example shows:
 * - Complex entity relationships
 * - DataLoaders for performance optimization
 * - Pagination with filtering and sorting
 * - Input types for mutations
 * - Interface and union types
 * - JavaDoc documentation extraction
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
        
        System.out.println("""
            🚀 E-commerce GraphQL API is running!
            
            📊 GraphQL Playground: http://localhost:8080/graphiql
            📋 Schema endpoint: http://localhost:8080/graphql/schema
            🔍 GraphQL endpoint: http://localhost:8080/graphql
            
            ✨ Features demonstrated:
            • Auto-generated schema from JPA entities
            • DataLoaders for N+1 query optimization
            • Relay-style cursor pagination
            • Input types for mutations
            • Interface and union types
            • JavaDoc descriptions
            
            📚 Example queries available in: /graphql/examples.graphql
            """);
    }
}

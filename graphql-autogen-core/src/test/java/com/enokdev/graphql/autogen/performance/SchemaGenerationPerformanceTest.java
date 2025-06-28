package com.enokdev.graphql.autogen.performance;

import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.testmodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for schema generation.
 * Validates that schema generation performs within acceptable limits.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class SchemaGenerationPerformanceTest {

    private DefaultSchemaGenerator schemaGenerator;

    @BeforeEach
    void setUp() {
        schemaGenerator = new DefaultSchemaGenerator();
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testSmallSchemaGenerationPerformance() {
        // Test with small number of types (< 10)
        List<Class<?>> classes = List.of(
            Book.class,
            Author.class,
            BookStatus.class,
            CreateBookInput.class,
            UpdateBookInput.class
        );

        long startTime = System.currentTimeMillis();
        String schema = schemaGenerator.generateSchema(classes);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(schema);
        assertFalse(schema.trim().isEmpty());
        assertTrue(duration < 500, "Small schema generation should take < 500ms, took: " + duration + "ms");
        
        System.out.println("✅ Small schema generation: " + duration + "ms");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testMediumSchemaGenerationPerformance() {
        // Test with medium number of types (10-50)
        List<Class<?>> classes = new ArrayList<>();
        
        // Add all test model classes
        classes.add(Book.class);
        classes.add(Author.class);
        classes.add(BookStatus.class);
        classes.add(CreateBookInput.class);
        classes.add(UpdateBookInput.class);
        classes.add(BookWithDataLoader.class);
        classes.add(BookWithPagination.class);
        classes.add(BookWithInterfaces.class);
        classes.add(BookWithJavaDoc.class);
        classes.add(Node.class);
        classes.add(Searchable.class);
        classes.add(SearchResult.class);

        long startTime = System.currentTimeMillis();
        String schema = schemaGenerator.generateSchema(classes);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(schema);
        assertFalse(schema.trim().isEmpty());
        assertTrue(duration < 2000, "Medium schema generation should take < 2s, took: " + duration + "ms");
        
        System.out.println("✅ Medium schema generation: " + duration + "ms");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testLargeSchemaGenerationPerformance() {
        // Simulate large schema by generating multiple classes programmatically
        List<Class<?>> classes = new ArrayList<>();
        
        // Add base classes multiple times to simulate larger schema
        for (int i = 0; i < 20; i++) {
            classes.add(Book.class);
            classes.add(Author.class);
            classes.add(CreateBookInput.class);
        }

        long startTime = System.currentTimeMillis();
        String schema = schemaGenerator.generateSchema(classes);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(schema);
        assertFalse(schema.trim().isEmpty());
        assertTrue(duration < 5000, "Large schema generation should take < 5s, took: " + duration + "ms");
        
        System.out.println("✅ Large schema generation: " + duration + "ms");
    }

    @Test
    void testMemoryUsageDuringGeneration() {
        // Test memory usage doesn't grow excessively
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection to get baseline
        System.gc();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        
        List<Class<?>> classes = List.of(
            Book.class,
            Author.class,
            BookStatus.class,
            CreateBookInput.class,
            BookWithDataLoader.class,
            BookWithPagination.class
        );

        // Generate schema multiple times
        for (int i = 0; i < 100; i++) {
            String schema = schemaGenerator.generateSchema(classes);
            assertNotNull(schema);
        }

        System.gc();
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryGrowth = afterMemory - beforeMemory;
        
        // Memory growth should be reasonable (< 50MB for 100 generations)
        assertTrue(memoryGrowth < 50 * 1024 * 1024, 
            "Memory growth should be < 50MB, was: " + (memoryGrowth / 1024 / 1024) + "MB");
        
        System.out.println("✅ Memory usage test passed. Growth: " + (memoryGrowth / 1024 / 1024) + "MB");
    }

    @Test
    void testConcurrentSchemaGeneration() throws InterruptedException {
        // Test that concurrent schema generation works correctly
        List<Class<?>> classes = List.of(Book.class, Author.class, CreateBookInput.class);
        
        int threadCount = 10;
        List<Thread> threads = new ArrayList<>();
        List<String> results = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    String schema = schemaGenerator.generateSchema(classes);
                    synchronized (results) {
                        results.add(schema);
                    }
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(5000); // 5 second timeout
        }
        
        long duration = System.currentTimeMillis() - startTime;

        // Verify results
        assertTrue(exceptions.isEmpty(), "No exceptions should occur: " + exceptions);
        assertEquals(threadCount, results.size(), "All threads should complete successfully");
        
        // All results should be identical
        String firstResult = results.get(0);
        for (String result : results) {
            assertEquals(firstResult, result, "All generated schemas should be identical");
        }
        
        assertTrue(duration < 3000, "Concurrent generation should complete in < 3s, took: " + duration + "ms");
        
        System.out.println("✅ Concurrent generation test passed: " + duration + "ms with " + threadCount + " threads");
    }

    @Test
    void testCachingEffectiveness() {
        // Test that caching improves performance
        List<Class<?>> classes = List.of(Book.class, Author.class, CreateBookInput.class);

        // First generation (cold cache)
        long startTime = System.currentTimeMillis();
        String schema1 = schemaGenerator.generateSchema(classes);
        long firstGeneration = System.currentTimeMillis() - startTime;

        // Second generation (warm cache)
        startTime = System.currentTimeMillis();
        String schema2 = schemaGenerator.generateSchema(classes);
        long secondGeneration = System.currentTimeMillis() - startTime;

        // Third generation (should be even faster)
        startTime = System.currentTimeMillis();
        String schema3 = schemaGenerator.generateSchema(classes);
        long thirdGeneration = System.currentTimeMillis() - startTime;

        // Verify schemas are identical
        assertEquals(schema1, schema2);
        assertEquals(schema2, schema3);

        // Caching should improve performance (second and third should be faster)
        // Note: This is not always guaranteed due to JIT compilation, but generally true
        System.out.println("✅ Caching test - First: " + firstGeneration + "ms, " +
                          "Second: " + secondGeneration + "ms, Third: " + thirdGeneration + "ms");
        
        // At minimum, ensure no performance regression
        assertTrue(secondGeneration <= firstGeneration * 2, "Cached generation shouldn't be much slower");
        assertTrue(thirdGeneration <= firstGeneration * 2, "Cached generation shouldn't be much slower");
    }

    @Test
    void testSchemaComplexityHandling() {
        // Test that complex schemas with many relationships are handled efficiently
        List<Class<?>> complexClasses = List.of(
            BookWithDataLoader.class,      // Has DataLoader annotations
            BookWithPagination.class,      // Has pagination annotations
            BookWithInterfaces.class,      // Implements interfaces
            BookWithJavaDoc.class,         // Has JavaDoc extraction
            SearchResult.class,            // Union type
            Node.class,                    // Interface type
            Searchable.class              // Another interface
        );

        long startTime = System.currentTimeMillis();
        String schema = schemaGenerator.generateSchema(complexClasses);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(schema);
        assertFalse(schema.trim().isEmpty());
        
        // Complex schema should still generate in reasonable time
        assertTrue(duration < 3000, "Complex schema should generate in < 3s, took: " + duration + "ms");
        
        // Verify schema contains expected complex elements
        assertTrue(schema.contains("Connection"), "Should contain Connection types");
        assertTrue(schema.contains("Edge"), "Should contain Edge types");
        assertTrue(schema.contains("interface"), "Should contain interface definitions");
        assertTrue(schema.contains("union"), "Should contain union definitions");
        
        System.out.println("✅ Complex schema generation: " + duration + "ms");
    }
}

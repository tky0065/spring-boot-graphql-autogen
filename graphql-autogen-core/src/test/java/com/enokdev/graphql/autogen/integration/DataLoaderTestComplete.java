package com.enokdev.graphql.autogen.integration;

import com.enokdev.graphql.autogen.generator.DataLoaderGenerator;
import com.enokdev.graphql.autogen.generator.DefaultDataLoaderGeneratorComplete;
import com.enokdev.graphql.autogen.testmodel.BookWithDataLoader;
import com.enokdev.graphql.autogen.testmodel.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test for DataLoader functionality.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class DataLoaderTestComplete {

    private DataLoaderGenerator dataLoaderGenerator;

    @BeforeEach
    void setUp() {
        dataLoaderGenerator = new DefaultDataLoaderGeneratorComplete();
    }

    @Test
    void testCompleteDataLoaderWorkflow() {
        // Test the complete workflow from annotation to generated code
        List<DataLoaderGenerator.DataLoaderConfiguration> configurations = 
            dataLoaderGenerator.generateDataLoaders(BookWithDataLoader.class);
        
        assertNotNull(configurations);
        assertEquals(4, configurations.size());
        
        // Generate Spring configuration for all DataLoaders
        String springConfig = dataLoaderGenerator.generateSpringConfiguration(configurations);
        
        assertNotNull(springConfig);
        assertTrue(springConfig.contains("@Configuration"));
        assertTrue(springConfig.contains("DataLoader"));
        
        // Verify each DataLoader is properly configured
        for (DataLoaderGenerator.DataLoaderConfiguration config : configurations) {
            String code = dataLoaderGenerator.generateDataLoaderCode(config);
            assertNotNull(code);
            assertTrue(code.contains("@Bean"));
            assertTrue(code.contains(config.getName()));
        }
        
        System.out.println("✅ DataLoader generation workflow completed successfully");
    }

    @Test
    void testDataLoaderDetection() throws Exception {
        Method getAuthorMethod = BookWithDataLoader.class.getMethod("getAuthor");
        assertTrue(dataLoaderGenerator.shouldGenerateDataLoader(getAuthorMethod));
        
        Method getTitleMethod = BookWithDataLoader.class.getMethod("getTitle");
        assertFalse(dataLoaderGenerator.shouldGenerateDataLoader(getTitleMethod));
    }

    @Test
    void testConfigurationGeneration() throws Exception {
        Method getAuthorMethod = BookWithDataLoader.class.getMethod("getAuthor");
        DataLoaderGenerator.DataLoaderConfiguration config = 
            dataLoaderGenerator.generateDataLoader(getAuthorMethod);
        
        assertNotNull(config);
        assertEquals("authorDataLoader", config.getName());
        assertEquals("authorId", config.getKeyProperty());
        assertEquals(100, config.getBatchSize());
        assertTrue(config.isCachingEnabled());
    }
}

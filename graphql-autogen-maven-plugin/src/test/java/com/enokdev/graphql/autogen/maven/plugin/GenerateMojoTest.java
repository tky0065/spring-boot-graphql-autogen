package com.enokdev.graphql.autogen.maven.plugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GenerateMojo.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.1
 */
@ExtendWith(MockitoExtension.class)
class GenerateMojoTest {

    @Test
    void testMojoConfiguration() {
        GenerateMojo mojo = new GenerateMojo();
        
        // Test setters
        mojo.setBasePackages(Arrays.asList("com.example"));
        mojo.setOutputDirectory(new File("/tmp/output"));
        mojo.setSchemaFileName("test-schema.graphqls");
        mojo.setSkipGeneration(true);
        
        // Basic configuration test - just ensuring no exceptions are thrown
        assertDoesNotThrow(() -> {
            // This tests that the mojo can be configured without issues
            assertNotNull(mojo);
        });
    }

    @Test
    void testDefaultConfiguration() {
        GenerateMojo mojo = new GenerateMojo();
        
        // Test that the mojo can be instantiated with default configuration
        assertNotNull(mojo);
    }
}
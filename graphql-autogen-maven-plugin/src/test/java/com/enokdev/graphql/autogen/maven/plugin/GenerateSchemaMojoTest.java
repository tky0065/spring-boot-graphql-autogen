package com.enokdev.graphql.autogen.maven.plugin;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateSchemaMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @TempDir
    Path tempDir;

    @Test
    public void testMojoExecution() throws Exception {
        File pom = new File(getClass().getResource("/unit/basic-test/pom.xml").getFile());
        assertNotNull(pom);
        assertTrue(pom.exists());

        GenerateSchemaMojo mojo = (GenerateSchemaMojo) rule.lookupMojo("generate-schema", pom);
        assertNotNull(mojo);

        // Set parameters for the Mojo
        mojo.basePackages = List.of("com.example.demo"); // Replace with a package that has GraphQL annotations
        mojo.schemaLocation = tempDir.toFile();
        mojo.schemaFileName = "test-schema.graphqls";

        mojo.execute();

        File generatedSchema = new File(tempDir.toFile(), "test-schema.graphqls");
        assertTrue(generatedSchema.exists());
        assertTrue(generatedSchema.length() > 0);
    }
}

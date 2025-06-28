package com.enokdev.graphql.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for GenerateSchemaMojo.
 */
class GenerateSchemaMojoTest extends AbstractMojoTestCase {

    @TempDir
    private Path tempDir;

    private GenerateSchemaMojo mojo;
    private File outputDirectory;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        
        mojo = new GenerateSchemaMojo();
        outputDirectory = tempDir.resolve("target/generated-sources/graphql").toFile();
        
        // Setup basic configuration
        setVariableValueToObject(mojo, "basePackages", List.of("com.example"));
        setVariableValueToObject(mojo, "outputDirectory", outputDirectory);
        setVariableValueToObject(mojo, "schemaFileName", "schema.graphqls");
        setVariableValueToObject(mojo, "generateInputs", true);
        setVariableValueToObject(mojo, "namingStrategy", "PASCAL_CASE");
        setVariableValueToObject(mojo, "failOnError", true);
        setVariableValueToObject(mojo, "skip", false);
        
        // Mock MavenProject
        MavenProject project = new MavenProject();
        project.setGroupId("com.example");
        project.setArtifactId("test-project");
        project.setVersion("1.0.0");
        project.getBuild().setOutputDirectory(tempDir.resolve("target/classes").toString());
        
        setVariableValueToObject(mojo, "project", project);
    }

    @Test
    void testExecuteWithValidConfiguration() throws Exception {
        // Given
        Files.createDirectories(outputDirectory.toPath());
        
        // When
        mojo.execute();
        
        // Then
        File schemaFile = new File(outputDirectory, "schema.graphqls");
        assertThat(schemaFile).exists();
    }

    @Test
    void testExecuteWithSkipEnabled() throws Exception {
        // Given
        setVariableValueToObject(mojo, "skip", true);
        
        // When
        mojo.execute();
        
        // Then
        File schemaFile = new File(outputDirectory, "schema.graphqls");
        assertThat(schemaFile).doesNotExist();
    }

    @Test
    void testExecuteCreatesOutputDirectory() throws Exception {
        // Given
        assertThat(outputDirectory).doesNotExist();
        
        // When
        mojo.execute();
        
        // Then
        assertThat(outputDirectory).exists();
        assertThat(outputDirectory).isDirectory();
    }

    @Test
    void testExecuteWithCustomSchemaFileName() throws Exception {
        // Given
        String customFileName = "custom-schema.graphqls";
        setVariableValueToObject(mojo, "schemaFileName", customFileName);
        
        // When
        mojo.execute();
        
        // Then
        File schemaFile = new File(outputDirectory, customFileName);
        assertThat(schemaFile).exists();
    }

    @Test
    void testExecuteWithInvalidNamingStrategy() {
        // Given
        setVariableValueToObject(mojo, "namingStrategy", "INVALID_STRATEGY");
        
        // When & Then
        assertThatThrownBy(() -> mojo.execute())
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Invalid naming strategy");
    }

    @Test
    void testExecuteWithEmptyBasePackages() {
        // Given
        setVariableValueToObject(mojo, "basePackages", List.of());
        
        // When
        assertThatCode(() -> mojo.execute()).doesNotThrowAnyException();
        
        // Should use project groupId as default
    }

    @Test
    void testExecuteWithGenerateInputsDisabled() throws Exception {
        // Given
        setVariableValueToObject(mojo, "generateInputs", false);
        
        // When
        mojo.execute();
        
        // Then
        File schemaFile = new File(outputDirectory, "schema.graphqls");
        assertThat(schemaFile).exists();
        
        String content = Files.readString(schemaFile.toPath());
        // Verify that input types are not generated
        assertThat(content).doesNotContain("input ");
    }
}

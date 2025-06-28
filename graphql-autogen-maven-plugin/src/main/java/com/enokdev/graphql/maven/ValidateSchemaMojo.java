package com.enokdev.graphql.gradle;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for GraphQLAutoGenPlugin.
 */
class GraphQLAutoGenPluginTest {

    private Project project;
    private GraphQLAutoGenPlugin plugin;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder().build();
        plugin = new GraphQLAutoGenPlugin();
    }

    @Test
    void testPluginApplication() {
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);

        // Then
        assertThat(project.getPlugins().hasPlugin(GraphQLAutoGenPlugin.class)).isTrue();
        assertThat(project.getPlugins().hasPlugin(JavaPlugin.class)).isTrue();
    }

    @Test
    void testExtensionCreated() {
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);

        // Then
        GraphQLAutoGenExtension extension = project.getExtensions()
            .findByType(GraphQLAutoGenExtension.class);
        
        assertThat(extension).isNotNull();
    }

    @Test
    void testTasksRegistered() {
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);

        // Then
        Task generateTask = project.getTasks().findByName(GraphQLAutoGenPlugin.GENERATE_SCHEMA_TASK_NAME);
        Task validateTask = project.getTasks().findByName(GraphQLAutoGenPlugin.VALIDATE_SCHEMA_TASK_NAME);
        Task cleanTask = project.getTasks().findByName(GraphQLAutoGenPlugin.CLEAN_SCHEMA_TASK_NAME);

        assertThat(generateTask).isNotNull();
        assertThat(validateTask).isNotNull();
        assertThat(cleanTask).isNotNull();
        
        // Check task types
        assertThat(generateTask).isInstanceOf(GenerateSchemaTask.class);
        assertThat(validateTask).isInstanceOf(ValidateSchemaTask.class);
        assertThat(cleanTask).isInstanceOf(CleanSchemaTask.class);
    }

    @Test
    void testTaskGroups() {
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);

        // Then
        Task generateTask = project.getTasks().findByName(GraphQLAutoGenPlugin.GENERATE_SCHEMA_TASK_NAME);
        Task validateTask = project.getTasks().findByName(GraphQLAutoGenPlugin.VALIDATE_SCHEMA_TASK_NAME);
        Task cleanTask = project.getTasks().findByName(GraphQLAutoGenPlugin.CLEAN_SCHEMA_TASK_NAME);

        assertThat(generateTask.getGroup()).isEqualTo(GraphQLAutoGenPlugin.TASK_GROUP);
        assertThat(validateTask.getGroup()).isEqualTo(GraphQLAutoGenPlugin.TASK_GROUP);
        assertThat(cleanTask.getGroup()).isEqualTo(GraphQLAutoGenPlugin.TASK_GROUP);
    }

    @Test
    void testExtensionDefaults() {
        // Given
        project.setGroup("com.example");
        
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);

        // Then
        GraphQLAutoGenExtension extension = project.getExtensions()
            .getByType(GraphQLAutoGenExtension.class);

        assertThat(extension.getSchemaFileName().get()).isEqualTo("schema.graphqls");
        assertThat(extension.getGenerateInputs().get()).isTrue();
        assertThat(extension.getNamingStrategy().get()).isEqualTo("PASCAL_CASE");
        assertThat(extension.getFailOnError().get()).isTrue();
        assertThat(extension.getSkip().get()).isFalse();
        assertThat(extension.getIncludeInheritedFields().get()).isTrue();
        assertThat(extension.getMaxScanDepth().get()).isEqualTo(10);
    }

    @Test
    void testTaskDependencies() {
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);

        // Then
        Task validateTask = project.getTasks().findByName(GraphQLAutoGenPlugin.VALIDATE_SCHEMA_TASK_NAME);
        Task generateTask = project.getTasks().findByName(GraphQLAutoGenPlugin.GENERATE_SCHEMA_TASK_NAME);

        // Validate task should depend on generate task
        assertThat(validateTask.getDependsOn()).contains(generateTask);
    }

    @Test
    void testExtensionConfiguration() {
        // When
        project.getPlugins().apply(GraphQLAutoGenPlugin.class);
        
        GraphQLAutoGenExtension extension = project.getExtensions()
            .getByType(GraphQLAutoGenExtension.class);
        
        // Configure extension
        extension.basePackages("com.example.test");
        extension.schemaFileName("test-schema.graphqls");
        extension.generateInputs(false);
        extension.namingStrategy("CAMEL_CASE");

        // Then
        assertThat(extension.getBasePackages().get()).containsExactly("com.example.test");
        assertThat(extension.getSchemaFileName().get()).isEqualTo("test-schema.graphqls");
        assertThat(extension.getGenerateInputs().get()).isFalse();
        assertThat(extension.getNamingStrategy().get()).isEqualTo("CAMEL_CASE");
    }
}

package com.enokdev.graphql.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;

/**
 * Gradle plugin for GraphQL AutoGen.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class GraphQLAutoGenPlugin implements Plugin<Project> {

    public static final String EXTENSION_NAME = "graphqlAutoGen";
    public static final String TASK_GROUP = "GraphQL AutoGen";
    
    // Task names
    public static final String GENERATE_SCHEMA_TASK_NAME = "generateGraphQLSchema";
    public static final String VALIDATE_SCHEMA_TASK_NAME = "validateGraphQLSchema";
    public static final String CLEAN_SCHEMA_TASK_NAME = "cleanGraphQLSchema";

    @Override
    public void apply(Project project) {
        // Apply Java plugin if not already applied
        project.getPlugins().apply(JavaPlugin.class);
        
        // Create extension for configuration
        GraphQLAutoGenExtension extension = project.getExtensions()
            .create(EXTENSION_NAME, GraphQLAutoGenExtension.class);
        
        // Configure default values
        configureExtensionDefaults(project, extension);
        
        // Register tasks
        registerTasks(project, extension);
        
        // Configure task dependencies
        configureDependencies(project);
        
        project.getLogger().info("GraphQL AutoGen plugin applied to project: {}", project.getName());
    }

    private void configureExtensionDefaults(Project project, GraphQLAutoGenExtension extension) {
        // Set default output directory
        extension.getOutputDirectory().convention(
            project.getLayout().getBuildDirectory().dir("generated/sources/graphql")
        );
        
        // Set default schema file name
        extension.getSchemaFileName().convention("schema.graphqls");
        
        // Set default generation options
        extension.getGenerateInputs().convention(true);
        extension.getNamingStrategy().convention("PASCAL_CASE");
        extension.getFailOnError().convention(true);
        extension.getSkip().convention(false);
        extension.getIncludeInheritedFields().convention(true);
        extension.getMaxScanDepth().convention(10);
        
        // Set default base packages from project group
        if (project.getGroup() != null) {
            extension.getBasePackages().convention(
                project.provider(() -> java.util.List.of(project.getGroup().toString()))
            );
        }
    }

    private void registerTasks(Project project, GraphQLAutoGenExtension extension) {
        // Register generate schema task
        TaskProvider<GenerateSchemaTask> generateTask = project.getTasks()
            .register(GENERATE_SCHEMA_TASK_NAME, GenerateSchemaTask.class, task -> {
                task.setDescription("Generate GraphQL schema from annotated Java classes");
                task.setGroup(TASK_GROUP);
                
                // Configure task inputs from extension
                task.getBasePackages().set(extension.getBasePackages());
                task.getExcludePackages().set(extension.getExcludePackages());
                task.getOutputDirectory().set(extension.getOutputDirectory());
                task.getSchemaFileName().set(extension.getSchemaFileName());
                task.getGenerateInputs().set(extension.getGenerateInputs());
                task.getNamingStrategy().set(extension.getNamingStrategy());
                task.getFailOnError().set(extension.getFailOnError());
                task.getSkip().set(extension.getSkip());
                task.getIncludeInheritedFields().set(extension.getIncludeInheritedFields());
                task.getMaxScanDepth().set(extension.getMaxScanDepth());
                
                // Set classpath
                task.getClasspath().from(project.getConfigurations().getByName("compileClasspath"));
                task.getClasspath().from(project.getTasks().getByName("compileJava").getOutputs());
            });

        // Register validate schema task
        TaskProvider<ValidateSchemaTask> validateTask = project.getTasks()
            .register(VALIDATE_SCHEMA_TASK_NAME, ValidateSchemaTask.class, task -> {
                task.setDescription("Validate the generated GraphQL schema");
                task.setGroup(TASK_GROUP);
                
                // Configure task inputs
                task.getSchemaDirectory().set(extension.getOutputDirectory());
                task.getSchemaFileName().set(extension.getSchemaFileName());
                task.getFailOnValidationError().convention(true);
                task.getSkipValidation().convention(false);
                task.getValidateNotEmpty().convention(true);
                task.getMinTypes().convention(1);
                
                // Depend on generate task
                task.dependsOn(generateTask);
            });

        // Register clean schema task
        TaskProvider<CleanSchemaTask> cleanTask = project.getTasks()
            .register(CLEAN_SCHEMA_TASK_NAME, CleanSchemaTask.class, task -> {
                task.setDescription("Clean generated GraphQL schema files");
                task.setGroup(TASK_GROUP);
                
                // Configure task inputs
                task.getOutputDirectory().set(extension.getOutputDirectory());
                task.getCleanOnlySchemaFiles().convention(true);
                task.getSkipClean().convention(false);
            });

        project.getLogger().debug("Registered GraphQL AutoGen tasks: {}, {}, {}", 
            GENERATE_SCHEMA_TASK_NAME, VALIDATE_SCHEMA_TASK_NAME, CLEAN_SCHEMA_TASK_NAME);
    }

    private void configureDependencies(Project project) {
        // Make generate task run before compileJava if needed
        project.getTasks().named("compileJava").configure(task -> {
            // Only if user wants to include generated files in compilation
            // This can be controlled via extension if needed
        });
        
        // Make clean task run as part of main clean
        project.getTasks().named("clean").configure(task -> {
            task.dependsOn(CLEAN_SCHEMA_TASK_NAME);
        });
        
        // Make check task include validation
        project.getTasks().named("check").configure(task -> {
            task.dependsOn(VALIDATE_SCHEMA_TASK_NAME);
        });
    }
}

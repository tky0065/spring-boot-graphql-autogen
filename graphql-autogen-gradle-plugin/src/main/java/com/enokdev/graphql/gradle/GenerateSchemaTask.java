package com.enokdev.graphql.gradle;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.generator.DefaultOperationResolver;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Gradle task to generate GraphQL schema from Java classes.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public abstract class GenerateSchemaTask extends DefaultTask {

    /**
     * Base packages to scan for GraphQL annotations.
     */
    @Input
    public abstract ListProperty<String> getBasePackages();

    /**
     * Packages to exclude from scanning.
     */
    @Input
    @Optional
    public abstract ListProperty<String> getExcludePackages();

    /**
     * Output directory for the generated schema file.
     */
    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    /**
     * Name of the generated schema file.
     */
    @Input
    public abstract Property<String> getSchemaFileName();

    /**
     * Whether to generate input types automatically.
     */
    @Input
    public abstract Property<Boolean> getGenerateInputs();

    /**
     * Naming strategy for generated types.
     */
    @Input
    public abstract Property<String> getNamingStrategy();

    /**
     * Whether to fail build on schema generation errors.
     */
    @Input
    public abstract Property<Boolean> getFailOnError();

    /**
     * Skip schema generation.
     */
    @Input
    public abstract Property<Boolean> getSkip();

    /**
     * Whether to include inherited fields.
     */
    @Input
    public abstract Property<Boolean> getIncludeInheritedFields();

    /**
     * Maximum scanning depth for packages.
     */
    @Input
    public abstract Property<Integer> getMaxScanDepth();

    /**
     * Classpath for scanning classes.
     */
    @InputFiles
    @Classpath
    public abstract ConfigurableFileCollection getClasspath();

    @TaskAction
    public void generateSchema() {
        if (getSkip().get()) {
            getLogger().info("GraphQL schema generation skipped");
            return;
        }

        getLogger().info("Starting GraphQL schema generation...");

        try {
            validateConfiguration();
            setupClasspath();
            performGeneration();
            getLogger().info("GraphQL schema generation completed successfully");
        } catch (Exception e) {
            String message = "Failed to generate GraphQL schema: " + e.getMessage();
            if (getFailOnError().get()) {
                throw new TaskExecutionException(this, new RuntimeException(message, e));
            } else {
                getLogger().warn(message, e);
            }
        }
    }

    private void validateConfiguration() {
        List<String> basePackages = getBasePackages().get();
        if (basePackages.isEmpty()) {
            throw new IllegalArgumentException("No base packages specified for scanning");
        }

        String namingStrategy = getNamingStrategy().get();
        try {
            GraphQLAutoGenConfig.NamingStrategy.valueOf(namingStrategy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid naming strategy: " + namingStrategy + 
                ". Valid values: CAMEL_CASE, PASCAL_CASE, SNAKE_CASE"
            );
        }
    }

    private void setupClasspath() throws Exception {
        Set<File> classpathFiles = getClasspath().getFiles();
        List<URL> urls = new ArrayList<>();

        for (File file : classpathFiles) {
            urls.add(file.toURI().toURL());
        }

        // Set up class loader
        URLClassLoader classLoader = new URLClassLoader(
            urls.toArray(new URL[0]),
            Thread.currentThread().getContextClassLoader()
        );

        Thread.currentThread().setContextClassLoader(classLoader);

        getLogger().debug("Setup classpath with {} elements", urls.size());
    }

    private void performGeneration() throws Exception {
        List<String> basePackages = getBasePackages().get();
        List<String> excludePackages = getExcludePackages().getOrElse(List.of());

        getLogger().info("Scanning packages: {}", basePackages);
        if (!excludePackages.isEmpty()) {
            getLogger().info("Excluding packages: {}", excludePackages);
        }

        // Create configuration
        GraphQLAutoGenConfig config = createConfiguration();

        // Create components
        DefaultAnnotationScanner scanner = new DefaultAnnotationScanner();
        DefaultTypeResolver typeResolver = new DefaultTypeResolver();
        DefaultFieldResolver fieldResolver = new DefaultFieldResolver();
        DefaultOperationResolver operationResolver = new DefaultOperationResolver();

        // Create schema generator
        SchemaGenerator schemaGenerator = new DefaultSchemaGenerator();

        // Scan classes
        List<Class<?>> scannedClasses = new ArrayList<>();
        for (String basePackage : basePackages) {
            scannedClasses.addAll(scanner.scanForAnnotatedClasses(basePackage));
        }

        getLogger().info("Found {} classes to process", scannedClasses.size());

        // Generate schema
        String schemaContent = schemaGenerator.generateSchema(scannedClasses);

        // Write to file
        writeSchemaToFile(schemaContent);

        File schemaFile = getOutputDirectory().file(getSchemaFileName().get()).get().getAsFile();
        getLogger().info("Schema written to: {}", schemaFile.getAbsolutePath());
    }

    private GraphQLAutoGenConfig createConfiguration() {
        GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
        config.setGenerateInputs(getGenerateInputs().get());
        config.setIncludeInheritedFields(getIncludeInheritedFields().get());
        config.setMaxScanDepth(getMaxScanDepth().get());

        // Set naming strategy
        config.setNamingStrategy(
            GraphQLAutoGenConfig.NamingStrategy.valueOf(getNamingStrategy().get().toUpperCase())
        );

        return config;
    }

    private void writeSchemaToFile(String schemaContent) throws Exception {
        // Create output directory if it doesn't exist
        Path outputPath = getOutputDirectory().get().getAsFile().toPath();
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
            getLogger().debug("Created output directory: {}", outputPath);
        }

        // Write schema file
        Path schemaFile = outputPath.resolve(getSchemaFileName().get());
        Files.writeString(schemaFile, schemaContent);

        getLogger().debug("Schema content length: {} characters", schemaContent.length());
    }
}

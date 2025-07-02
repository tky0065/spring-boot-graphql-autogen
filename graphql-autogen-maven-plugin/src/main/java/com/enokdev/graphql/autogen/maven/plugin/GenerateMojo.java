package com.enokdev.graphql.autogen.maven.plugin;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.generator.TypeResolver;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import graphql.schema.GraphQLSchema;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates GraphQL schema from annotated classes.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.1
 */
@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    threadSafe = true
)
public class GenerateMojo extends AbstractMojo {

    /**
     * The Maven project instance.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Base packages to scan for GraphQL annotations.
     * If empty, all packages in the classpath will be scanned.
     */
    @Parameter(property = "graphql.basePackages")
    private List<String> basePackages = new ArrayList<>();

    /**
     * Output directory for generated schema files.
     */
    @Parameter(
        property = "graphql.outputDirectory",
        defaultValue = "${project.build.directory}/generated-sources/graphql"
    )
    private File outputDirectory;

    /**
     * Name of the generated schema file.
     */
    @Parameter(property = "graphql.schemaFileName", defaultValue = "schema.graphqls")
    private String schemaFileName;

    /**
     * Whether to include GraphQL introspection types in the generated schema.
     */
    @Parameter(property = "graphql.includeIntrospection", defaultValue = "true")
    private boolean includeIntrospection;

    /**
     * Whether to skip schema generation.
     */
    @Parameter(property = "graphql.skip", defaultValue = "false")
    private boolean skipGeneration;

    /**
     * Encoding for the generated schema file.
     */
    @Parameter(property = "graphql.encoding", defaultValue = "UTF-8")
    private String encoding;

    /**
     * Naming strategy for GraphQL types and fields.
     */
    @Parameter(property = "graphql.namingStrategy", defaultValue = "CAMEL_CASE")
    private String namingStrategy;

    /**
     * Whether to generate input types automatically.
     */
    @Parameter(property = "graphql.generateInputTypes", defaultValue = "true")
    private boolean generateInputTypes;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipGeneration) {
            getLog().info("GraphQL schema generation skipped");
            return;
        }

        getLog().info("Starting GraphQL schema generation...");

        try {
            // Create output directory if it doesn't exist
            createOutputDirectory();

            // Configure GraphQL AutoGen
            GraphQLAutoGenConfig config = createConfiguration();

            // Scan for annotated classes
            AnnotationScanner scanner = new DefaultAnnotationScanner();
            // Scan for annotated classes using the correct method
            Set<Class<?>> annotatedClasses = scanner.scanForAnnotatedClasses(basePackages);

            if (annotatedClasses.isEmpty()) {
                getLog().warn("No GraphQL annotated classes found in specified packages: " + basePackages);
                return;
            }

            getLog().info("Found " + annotatedClasses.size() + " annotated classes");

            // Initialiser le générateur de schéma avec les paramètres corrects
            TypeResolver typeResolver = new DefaultTypeResolver();
            SchemaGenerator generator = new DefaultSchemaGenerator(typeResolver, null, null, null, config);

            // Générer le schéma GraphQL
            GraphQLSchema schema = generator.generateSchema(new ArrayList<>(annotatedClasses));

            // Convertir le schéma en chaîne de caractères
            String schemaContent = schema.toString();

            // Write schema to file
            writeSchemaToFile(schemaContent);

            getLog().info("GraphQL schema generation completed successfully");

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate GraphQL schema", e);
        }
    }

    private void createOutputDirectory() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Failed to create output directory: " + outputDirectory);
            }
        }
    }

    private GraphQLAutoGenConfig createConfiguration() {
        GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
        config.setBasePackages(basePackages);
        config.setEnabled(true);
        config.setGenerateInputs(generateInputTypes);
        
        try {
            config.setNamingStrategy(GraphQLAutoGenConfig.NamingStrategy.valueOf(namingStrategy));
        } catch (IllegalArgumentException e) {
            getLog().warn("Invalid naming strategy: " + namingStrategy + ", using default CAMEL_CASE");
            config.setNamingStrategy(GraphQLAutoGenConfig.NamingStrategy.CAMEL_CASE);
        }

        return config;
    }

    private void writeSchemaToFile(String schemaContent) throws IOException {
        Path outputPath = Paths.get(outputDirectory.getAbsolutePath(), schemaFileName);
        
        Files.write(outputPath, schemaContent.getBytes(encoding), 
                   StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        getLog().info("Schema written to: " + outputPath);
        getLog().info("Schema size: " + schemaContent.length() + " characters");
    }

    // Getters and setters for testing
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }

    public void setSkipGeneration(boolean skipGeneration) {
        this.skipGeneration = skipGeneration;
    }
}
package com.enokdev.graphql.maven;

import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.generator.DefaultOperationResolver;
import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal to generate GraphQL schema from annotated Java classes.
 * 
 * @author GraphQL AutoGen
 * @since 1.0.0
 */
@Mojo(
    name = "generate-schema",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    threadSafe = true
)
public class GenerateSchemaMojo extends AbstractMojo {

    private static final Logger logger = LoggerFactory.getLogger(GenerateSchemaMojo.class);

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Base packages to scan for GraphQL annotations.
     */
    @Parameter(property = "graphql.autogen.basePackages", required = true)
    private List<String> basePackages;

    /**
     * Output directory for the generated schema file.
     */
    @Parameter(
        property = "graphql.autogen.outputDirectory", 
        defaultValue = "${project.build.directory}/generated-sources/graphql"
    )
    private File outputDirectory;

    /**
     * Name of the generated schema file.
     */
    @Parameter(property = "graphql.autogen.schemaFileName", defaultValue = "schema.graphqls")
    private String schemaFileName;

    /**
     * Enable or disable input type generation.
     */
    @Parameter(property = "graphql.autogen.generateInputs", defaultValue = "true")
    private boolean generateInputs;

    /**
     * Naming strategy for generated types.
     */
    @Parameter(property = "graphql.autogen.namingStrategy", defaultValue = "DEFAULT")
    private String namingStrategy;

    /**
     * Custom type mappings (Java class -> GraphQL type).
     */
    @Parameter(property = "graphql.autogen.typeMappings")
    private List<String> typeMappings;

    /**
     * Skip plugin execution.
     */
    @Parameter(property = "graphql.autogen.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Verbose logging.
     */
    @Parameter(property = "graphql.autogen.verbose", defaultValue = "false")
    private boolean verbose;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("GraphQL AutoGen schema generation skipped");
            return;
        }

        validateParameters();
        
        getLog().info("Starting GraphQL schema generation...");
        if (verbose) {
            getLog().info("Base packages: " + basePackages);
            getLog().info("Output directory: " + outputDirectory.getAbsolutePath());
            getLog().info("Schema file name: " + schemaFileName);
        }

        try {
            // Create output directory if it doesn't exist
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Failed to create output directory: " + outputDirectory);
            }

            // Setup classpath for scanning
            ClassLoader classLoader = createProjectClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);

            // Create configuration
            GraphQLAutoGenConfig config = createConfiguration();

            // Create components
            DefaultAnnotationScanner scanner = new DefaultAnnotationScanner();
            DefaultTypeResolver typeResolver = new DefaultTypeResolver();
            DefaultFieldResolver fieldResolver = new DefaultFieldResolver();
            DefaultOperationResolver operationResolver = new DefaultOperationResolver();
            
            DefaultSchemaGenerator generator = new DefaultSchemaGenerator();
            
            // Scan for annotated classes
            List<Class<?>> annotatedClasses = new ArrayList<>();
            for (String basePackage : basePackages) {
                annotatedClasses.addAll(scanner.scanForAnnotatedClasses(basePackage));
            }

            // Generate schema
            String schema = generator.generateSchema(annotatedClasses);

            // Write schema to file
            Path schemaFile = Paths.get(outputDirectory.getAbsolutePath(), schemaFileName);
            Files.write(schemaFile, schema.getBytes());

            getLog().info("GraphQL schema generated successfully: " + schemaFile.toAbsolutePath());
            
            if (verbose) {
                getLog().info("Schema content:\n" + schema);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate GraphQL schema", e);
        }
    }

    private void validateParameters() throws MojoExecutionException {
        if (basePackages == null || basePackages.isEmpty()) {
            throw new MojoExecutionException("basePackages parameter is required");
        }

        for (String basePackage : basePackages) {
            if (basePackage == null || basePackage.trim().isEmpty()) {
                throw new MojoExecutionException("basePackage cannot be null or empty");
            }
        }

        if (outputDirectory == null) {
            throw new MojoExecutionException("outputDirectory parameter is required");
        }

        if (schemaFileName == null || schemaFileName.trim().isEmpty()) {
            throw new MojoExecutionException("schemaFileName parameter is required");
        }
    }

    private ClassLoader createProjectClassLoader() throws MojoExecutionException {
        try {
            List<URL> urls = new ArrayList<>();
            
            // Add project output directory
            String outputDir = project.getBuild().getOutputDirectory();
            if (outputDir != null) {
                urls.add(new File(outputDir).toURI().toURL());
            }

            // Add project dependencies
            for (Object element : project.getCompileClasspathElements()) {
                urls.add(new File((String) element).toURI().toURL());
            }

            return new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
            
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to create project class loader", e);
        }
    }

    private GraphQLAutoGenConfig createConfiguration() {
        GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
        
        config.setBasePackages(basePackages);
        config.setGenerateInputs(generateInputs);
        config.setNamingStrategy(parseNamingStrategy(namingStrategy));
        
        if (typeMappings != null) {
            for (String mapping : typeMappings) {
                // Parse mapping in format "java.util.UUID=ID"
                String[] parts = mapping.split("=");
                if (parts.length == 2) {
                    config.addTypeMapping(parts[0].trim(), parts[1].trim());
                }
            }
        }

        return config;
    }

    private GraphQLAutoGenConfig.NamingStrategy parseNamingStrategy(String strategy) {
        try {
            return GraphQLAutoGenConfig.NamingStrategy.valueOf(strategy.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLog().warn("Invalid naming strategy: " + strategy + ", using DEFAULT");
            return GraphQLAutoGenConfig.NamingStrategy.DEFAULT;
        }
    }
}

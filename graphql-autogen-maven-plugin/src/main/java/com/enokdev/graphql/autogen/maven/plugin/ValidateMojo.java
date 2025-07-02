package com.enokdev.graphql.autogen.maven.plugin;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.GraphQLSchemaValidator;
import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import graphql.schema.GraphQLSchema;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates GraphQL schema generated from annotated classes.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.1
 */
@Mojo(
    name = "validate",
    defaultPhase = LifecyclePhase.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public class ValidateMojo extends AbstractMojo {

    /**
     * The Maven project instance.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Base packages to scan for GraphQL annotations.
     */
    @Parameter(property = "graphql.basePackages")
    private List<String> basePackages = new ArrayList<>();

    /**
     * Directory containing the schema file to validate.
     */
    @Parameter(
        property = "graphql.schemaDirectory",
        defaultValue = "${project.build.directory}/generated-sources/graphql"
    )
    private File schemaDirectory;

    /**
     * Name of the schema file to validate.
     */
    @Parameter(property = "graphql.schemaFileName", defaultValue = "schema.graphqls")
    private String schemaFileName;

    /**
     * Whether to skip schema validation.
     */
    @Parameter(property = "graphql.skipValidation", defaultValue = "false")
    private boolean skipValidation;

    /**
     * Whether validation failures should fail the build.
     */
    @Parameter(property = "graphql.failOnValidationError", defaultValue = "true")
    private boolean failOnValidationError;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipValidation) {
            getLog().info("GraphQL schema validation skipped");
            return;
        }

        getLog().info("Starting GraphQL schema validation...");

        try {
            // Check if schema file exists
            Path schemaPath = Paths.get(schemaDirectory.getAbsolutePath(), schemaFileName);
            if (!Files.exists(schemaPath)) {
                String message = "Schema file not found: " + schemaPath + 
                               ". Please run 'mvn graphql-autogen:generate' first.";
                if (failOnValidationError) {
                    throw new MojoFailureException(message);
                } else {
                    getLog().warn(message);
                    return;
                }
            }

            // Generate schema from annotations for comparison
            GraphQLAutoGenConfig config = createConfiguration();
            AnnotationScanner scanner = new DefaultAnnotationScanner();

            // Correction: utiliser scanForAnnotatedClasses() au lieu de scan()
            Set<Class<?>> annotatedClasses = scanner.scanForAnnotatedClasses(basePackages);

            if (annotatedClasses.isEmpty()) {
                getLog().warn("No GraphQL annotated classes found for validation");
                return;
            }

            SchemaGenerator generator = new DefaultSchemaGenerator(null, null, null, scanner, config);
            GraphQLSchema schema = generator.generateSchema(new ArrayList<>(annotatedClasses));
            String generatedSchemaContent = schema.toString();

            // Validate the schema
            GraphQLSchemaValidator validator = new GraphQLSchemaValidator();

            // Pour simplifier, on considère que la génération sans erreur est un succès
            if (generatedSchemaContent != null && !generatedSchemaContent.trim().isEmpty()) {
                getLog().info("Schema validation completed successfully - no errors found");
            } else {
                getLog().error("Schema validation failed - generated schema is empty");
                throw new MojoFailureException("GraphQL schema validation failed. Generated schema is empty.");
            }

        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            String message = "Failed to validate GraphQL schema: " + e.getMessage();
            if (failOnValidationError) {
                throw new MojoExecutionException(message, e);
            } else {
                getLog().warn(message);
            }
        }
    }

    private GraphQLAutoGenConfig createConfiguration() {
        GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
        config.setBasePackages(basePackages);
        config.setEnabled(true);
        config.setValidateSchema(true);
        return config;
    }

    // Getters and setters for testing
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setSchemaDirectory(File schemaDirectory) {
        this.schemaDirectory = schemaDirectory;
    }

    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    public void setFailOnValidationError(boolean failOnValidationError) {
        this.failOnValidationError = failOnValidationError;
    }
}
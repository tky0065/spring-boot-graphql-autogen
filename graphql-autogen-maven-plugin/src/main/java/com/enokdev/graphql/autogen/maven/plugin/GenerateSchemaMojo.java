package com.enokdev.graphql.autogen.maven.plugin;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Goal that generates GraphQL schema from annotated Java classes.
 */
@Mojo(
    name = "generate-schema",
    defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class GenerateSchemaMojo extends AbstractMojo {

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
        property = "graphql.autogen.schemaLocation",
        defaultValue = "${project.build.directory}/generated-resources/graphql"
    )
    private File schemaLocation;

    /**
     * Name of the generated schema file.
     */
    @Parameter(
        property = "graphql.autogen.schemaFileName",
        defaultValue = "schema.graphqls"
    )
    private String schemaFileName;

    /**
     * Whether to enable GraphQL AutoGen auto-configuration.
     */
    @Parameter(
        property = "graphql.autogen.enabled",
        defaultValue = "true"
    )
    private boolean enabled;

    /**
     * Whether to automatically generate input types.
     */
    @Parameter(
        property = "graphql.autogen.generateInputs",
        defaultValue = "true"
    )
    private boolean generateInputs;

    /**
     * Whether to generate subscriptions.
     */
    @Parameter(
        property = "graphql.autogen.generateSubscriptions",
        defaultValue = "true"
    )
    private boolean generateSubscriptions;

    /**
     * Custom type mappings from Java types to GraphQL scalar names.
     */
    @Parameter(property = "graphql.autogen.typeMapping")
    private Map<String, String> typeMapping;

    @Override
    public void execute() {
        if (!enabled) {
            getLog().info("GraphQL schema generation is disabled.");
            return;
        }

        getLog().info("Starting GraphQL schema generation...");

        // Validate parameters
        if (basePackages == null || basePackages.isEmpty()) {
            throw new org.apache.maven.plugin.MojoExecutionException("Parameter 'basePackages' is required and cannot be empty.");
        }
        if (schemaLocation == null) {
            throw new org.apache.maven.plugin.MojoExecutionException("Parameter 'schemaLocation' is required.");
        }
        if (schemaFileName == null || schemaFileName.trim().isEmpty()) {
            throw new org.apache.maven.plugin.MojoExecutionException("Parameter 'schemaFileName' is required and cannot be empty.");
        }

        try {
            // Add project's compile classpath to the current classloader
            List<URL> classpathUrls = new ArrayList<>();
            for (String element : project.getCompileClasspathElements()) {
                classpathUrls.add(new File(element).toURI().toURL());
            }
            URLClassLoader classLoader = new URLClassLoader(
                classpathUrls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader()
            );
            Thread.currentThread().setContextClassLoader(classLoader);

            GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
            config.setBasePackages(basePackages);
            config.setGenerateInputs(generateInputs);
            config.setGenerateSubscriptions(generateSubscriptions);
            if (typeMapping != null) {
                config.setTypeMapping(typeMapping);
            }

            DefaultTypeResolver typeResolver = new DefaultTypeResolver();
            SchemaGenerator schemaGenerator = new DefaultSchemaGenerator(typeResolver, null, null, null, config);
            DefaultAnnotationScanner annotationScanner = new DefaultAnnotationScanner();

            Set<Class<?>> annotatedClasses = annotationScanner.scan(basePackages);

            if (annotatedClasses.isEmpty()) {
                getLog().warn("No GraphQL annotated classes found in specified base packages. Schema generation skipped.");
                return;
            }

            String schemaContent = schemaGenerator.generateSchemaString(new ArrayList<>(annotatedClasses));

            Path outputPath = Paths.get(schemaLocation.toURI());
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            Path schemaFile = outputPath.resolve(schemaFileName);
            Files.writeString(schemaFile, schemaContent);

            getLog().info("GraphQL schema generated successfully to: " + schemaFile.toAbsolutePath());

        } catch (Exception e) {
            getLog().error("Failed to generate GraphQL schema", e);
            throw new org.apache.maven.plugin.MojoExecutionException("Failed to generate GraphQL schema", e);
        }
    }
}

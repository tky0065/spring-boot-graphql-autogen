package com.enokdev.graphql.autogen.maven.plugin;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import graphql.schema.GraphQLSchema;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
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
import java.util.HashSet;
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

    // Ajout des méthodes setters pour les tests
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setSchemaLocation(File schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (!enabled) {
            getLog().info("GraphQL schema generation is disabled.");
            return;
        }

        getLog().info("Starting GraphQL schema generation...");

        // Validate parameters
        if (basePackages == null || basePackages.isEmpty()) {
            throw new MojoExecutionException("Parameter 'basePackages' is required and cannot be empty.");
        }
        if (schemaLocation == null) {
            throw new MojoExecutionException("Parameter 'schemaLocation' is required.");
        }
        if (schemaFileName == null || schemaFileName.trim().isEmpty()) {
            throw new MojoExecutionException("Parameter 'schemaFileName' is required and cannot be empty.");
        }

        try {
            // Add project's compile classpath to the current classloader
            List<URL> classpathUrls = new ArrayList<>();
            try {
                // Utilisation d'une méthode plus sûre pour récupérer les éléments du classpath
                @SuppressWarnings("unchecked")
                List<String> elements = project.getCompileClasspathElements();
                for (String element : elements) {
                    try {
                        URL url = new File(element).toURI().toURL();
                        classpathUrls.add(url);
                        getLog().debug("Added to classpath: " + url);
                    } catch (Exception e) {
                        getLog().debug("Could not convert classpath element to URL: " + element, e);
                    }
                }
            } catch (Exception e) {
                getLog().warn("Could not retrieve project classpath, using current classpath", e);
            }

            // Création d'un ClassLoader parent-last pour éviter les conflits de versions
            URLClassLoader classLoader = new URLClassLoader(
                classpathUrls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader()
            );
            Thread.currentThread().setContextClassLoader(classLoader);

            GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
            config.setBasePackages(basePackages);
            config.setGenerateInputs(generateInputs);
            if (typeMapping != null) {
                for (Map.Entry<String, String> entry : typeMapping.entrySet()) {
                    config.addTypeMapping(entry.getKey(), entry.getValue());
                }
            }

            DefaultTypeResolver typeResolver = new DefaultTypeResolver();
            SchemaGenerator schemaGenerator = new DefaultSchemaGenerator(typeResolver, null, null, null, config);
            DefaultAnnotationScanner annotationScanner = new DefaultAnnotationScanner();

            // Correction: utiliser scanForAnnotatedClasses() au lieu de scan()
            Set<Class<?>> annotatedClasses = annotationScanner.scanForAnnotatedClasses(basePackages);

            if (annotatedClasses.isEmpty()) {
                getLog().warn("No GraphQL annotated classes found in specified base packages. Schema generation skipped.");
                return;
            }

            // Génération du schéma et conversion en String
            GraphQLSchema schema = schemaGenerator.generateSchema(new ArrayList<>(annotatedClasses));
            String schemaContent = schema.toString();

            Path outputPath = Paths.get(schemaLocation.toURI());
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            Path schemaFile = outputPath.resolve(schemaFileName);
            Files.writeString(schemaFile, schemaContent);

            getLog().info("GraphQL schema generated successfully to: " + schemaFile.toAbsolutePath());

        } catch (Exception e) {
            getLog().error("Failed to generate GraphQL schema", e);
            throw new MojoExecutionException("Failed to generate GraphQL schema", e);
        }
    }
}

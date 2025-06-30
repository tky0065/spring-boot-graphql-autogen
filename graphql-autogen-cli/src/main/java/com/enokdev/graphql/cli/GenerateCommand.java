package com.enokdev.graphql.cli;

import com.enokdev.graphql.autogen.cli.CLILogger;
import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.generator.DefaultFieldResolver;
import com.enokdev.graphql.autogen.generator.DefaultOperationResolver;
import com.enokdev.graphql.autogen.generator.DefaultTypeResolver;
import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.generator.DefaultSchemaGenerator;
import com.enokdev.graphql.autogen.scanner.DefaultAnnotationScanner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * CLI command to generate GraphQL schema.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Command(
    name = "generate",
    description = "Generate GraphQL schema from Java classes",
    aliases = {"gen", "g"}
)
public class GenerateCommand implements Callable<Integer> {

    @ParentCommand
    private GraphQLAutoGenCLI parent;

    @Option(
        names = {"-p", "--packages"},
        description = "Base packages to scan (comma-separated)",
        required = true
    )
    private List<String> basePackages;

    @Option(
        names = {"-e", "--exclude"},
        description = "Packages to exclude from scanning (comma-separated)"
    )
    private List<String> excludePackages = new ArrayList<>();

    @Option(
        names = {"-o", "--output"},
        description = "Output file for the generated schema",
        defaultValue = "schema.graphqls"
    )
    private String outputFile;

    @Option(
        names = {"-c", "--classpath"},
        description = "Additional classpath entries (comma-separated)"
    )
    private List<String> classpathEntries = new ArrayList<>();

    @Option(
        names = {"--generate-inputs"},
        description = "Generate input types automatically",
        defaultValue = "true"
    )
    private boolean generateInputs;

    @Option(
        names = {"--naming-strategy"},
        description = "Naming strategy: PASCAL_CASE, CAMEL_CASE, SNAKE_CASE",
        defaultValue = "PASCAL_CASE"
    )
    private String namingStrategy;

    @Option(
        names = {"--include-inherited"},
        description = "Include inherited fields",
        defaultValue = "true"
    )
    private boolean includeInheritedFields;

    @Option(
        names = {"--max-depth"},
        description = "Maximum scanning depth",
        defaultValue = "10"
    )
    private int maxScanDepth;

    @Option(
        names = {"--config"},
        description = "Configuration file (JSON or YAML)"
    )
    private String configFile;

    @Option(
        names = {"--dry-run"},
        description = "Show what would be generated without writing files"
    )
    private boolean dryRun;

    @Override
    public Integer call() throws Exception {
        try {
            CLILogger logger = new CLILogger();
            logger.setVerbose(parent.isVerbose());
            
            logger.info("🚀 Starting GraphQL schema generation...");
            
            // Load configuration from file if specified
            if (configFile != null) {
                loadConfigFromFile();
            }
            
            // Validate configuration
            validateConfiguration();
            
            // Setup classpath
            setupClasspath(logger);
            
            // Generate schema
            String schemaContent = generateSchema(logger);
            
            if (dryRun) {
                logger.info("📄 Generated schema (dry run):");
                System.out.println(schemaContent);
            } else {
                writeSchemaToFile(schemaContent, logger);
                logger.success("✅ Schema generated successfully: " + outputFile);
            }
            
            return 0;
            
        } catch (Exception e) {
            CLILogger logger = new CLILogger();
            logger.setVerbose(parent.isVerbose());
            logger.error("❌ Schema generation failed: " + e.getMessage());
            
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            
            return 1;
        }
    }

    private void loadConfigFromFile() throws Exception {
        Path configPath = Paths.get(configFile);
        if (!Files.exists(configPath)) {
            throw new IllegalArgumentException("Configuration file not found: " + configFile);
        }

        CLIConfigLoader configLoader = new CLIConfigLoader();
        CLIConfig config = configLoader.loadConfig(configPath);
        
        // Override CLI options with config file values
        if (config.getBasePackages() != null && basePackages.isEmpty()) {
            basePackages = config.getBasePackages();
        }
        if (config.getExcludePackages() != null) {
            excludePackages.addAll(config.getExcludePackages());
        }
        if (config.getOutputFile() != null) {
            outputFile = config.getOutputFile();
        }
        if (config.getClasspathEntries() != null) {
            classpathEntries.addAll(config.getClasspathEntries());
        }
        if (config.getNamingStrategy() != null) {
            namingStrategy = config.getNamingStrategy();
        }
        
        generateInputs = config.isGenerateInputs();
        includeInheritedFields = config.isIncludeInheritedFields();
        maxScanDepth = config.getMaxScanDepth();
    }

    private void validateConfiguration() {
        if (basePackages == null || basePackages.isEmpty()) {
            throw new IllegalArgumentException("Base packages must be specified");
        }

        try {
            GraphQLAutoGenConfig.NamingStrategy.valueOf(namingStrategy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid naming strategy: " + namingStrategy + 
                ". Valid values: PASCAL_CASE, CAMEL_CASE, SNAKE_CASE"
            );
        }
    }

    private void setupClasspath(CLILogger logger) throws Exception {
        if (classpathEntries.isEmpty()) {
            // Use current classpath
            String classPath = System.getProperty("java.class.path");
            if (classPath != null) {
                classpathEntries.addAll(List.of(classPath.split(File.pathSeparator)));
            }
        }

        List<URL> urls = new ArrayList<>();
        for (String entry : classpathEntries) {
            Path path = Paths.get(entry);
            if (Files.exists(path)) {
                urls.add(path.toUri().toURL());
                logger.debug("Added to classpath: " + path);
            } else {
                logger.warn("Classpath entry not found: " + entry);
            }
        }

        if (urls.isEmpty()) {
            logger.warn("⚠️  No valid classpath entries found");
        } else {
            URLClassLoader classLoader = new URLClassLoader(
                urls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader()
            );
            Thread.currentThread().setContextClassLoader(classLoader);
            logger.debug("Setup classpath with " + urls.size() + " entries");
        }
    }

    private String generateSchema(CLILogger logger) throws Exception {
        logger.info("📦 Scanning packages: " + String.join(", ", basePackages));
        
        if (!excludePackages.isEmpty()) {
            logger.info("🚫 Excluding packages: " + String.join(", ", excludePackages));
        }

        // Create configuration
        GraphQLAutoGenConfig config = createConfiguration();

        // Create components
        DefaultAnnotationScanner scanner = new DefaultAnnotationScanner();
        DefaultTypeResolver typeResolver = new DefaultTypeResolver(config);
        DefaultFieldResolver fieldResolver = new DefaultFieldResolver(typeResolver);
        DefaultOperationResolver operationResolver = new DefaultOperationResolver(typeResolver);

        // Create schema generator
        SchemaGenerator schemaGenerator = new DefaultSchemaGenerator(
            typeResolver,
            fieldResolver,
            operationResolver,
            scanner,
            config
        );

        // Scan classes
        Set<Class<?>> scannedClasses = scanner.scanForAnnotatedClasses(basePackages);

        logger.info("🔍 Found " + scannedClasses.size() + " classes to process");

        if (scannedClasses.isEmpty()) {
            logger.warn("⚠️  No annotated classes found. Make sure your packages contain @GraphQLType annotations.");
        }

        // Generate schema
        String schemaContent = schemaGenerator.generateSchemaString(new ArrayList<>(scannedClasses));

        if (schemaContent != null) {
            logger.debug("Generated schema length: " + schemaContent.length() + " characters");
        }
        
        return schemaContent != null ? schemaContent : "";
    }

    private GraphQLAutoGenConfig createConfiguration() {
        GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();
        config.setGenerateInputs(generateInputs);
        
        config.setNamingStrategy(
            GraphQLAutoGenConfig.NamingStrategy.valueOf(namingStrategy.toUpperCase())
        );
        
        return config;
    }

    private void writeSchemaToFile(String schemaContent, CLILogger logger) throws Exception {
        Path outputPath = Paths.get(outputFile);
        
        // Create parent directories if they don't exist
        Path parentDir = outputPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            logger.debug("Created directory: " + parentDir);
        }
        
        // Write schema file
        Files.writeString(outputPath, schemaContent);
        
        long fileSize = Files.size(outputPath);
        logger.info("📄 Schema written: " + fileSize + " bytes");
    }
}

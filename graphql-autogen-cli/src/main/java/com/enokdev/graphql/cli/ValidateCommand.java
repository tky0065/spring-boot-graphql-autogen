package com.enokdev.graphql.cli;

import com.enokdev.graphql.autogen.cli.CLILogger;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * CLI command to validate GraphQL schema.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Command(
    name = "validate",
    description = "Validate GraphQL schema file",
    aliases = {"val", "v"}
)
public class ValidateCommand implements Callable<Integer> {

    @ParentCommand
    private GraphQLAutoGenCLI parent;

    @Option(
        names = {"-f", "--file"},
        description = "Schema file to validate",
        defaultValue = "schema.graphqls"
    )
    private String schemaFile;

    @Option(
        names = {"--min-types"},
        description = "Minimum number of types expected",
        defaultValue = "1"
    )
    private int minTypes;

    @Option(
        names = {"--check-query"},
        description = "Check that Query type exists",
        defaultValue = "true"
    )
    private boolean checkQuery;

    @Override
    public Integer call() throws Exception {
        try {
            CLILogger logger = new CLILogger();
            logger.setVerbose(parent.isVerbose());
            
            logger.info("🔍 Validating GraphQL schema: " + schemaFile);
            
            validateSchemaFile(logger);
            
            logger.success("✅ Schema validation passed!");
            return 0;
            
        } catch (Exception e) {
            CLILogger logger = new CLILogger();
            logger.setVerbose(parent.isVerbose());
            logger.error("❌ Schema validation failed: " + e.getMessage());
            
            if (parent.isVerbose()) {
                e.printStackTrace();
            }
            
            return 1;
        }
    }

    private void validateSchemaFile(CLILogger logger) throws Exception {
        Path schemaPath = Paths.get(schemaFile);
        
        if (!Files.exists(schemaPath)) {
            throw new IllegalArgumentException("Schema file not found: " + schemaFile);
        }

        String schemaContent = Files.readString(schemaPath);
        
        if (schemaContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Schema file is empty");
        }

        logger.debug("Schema file size: " + schemaContent.length() + " characters");

        // Parse schema syntax
        validateSchemaSyntax(schemaContent, logger);
        
        // Validate schema content
        validateSchemaContent(schemaContent, logger);
    }

    private void validateSchemaSyntax(String schemaContent, CLILogger logger) throws Exception {
        try {
            SchemaParser schemaParser = new SchemaParser();
            TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaContent);
            
            logger.debug("Schema parsed successfully with " + typeRegistry.types().size() + " type definitions");
            
            // Try to build the schema
            RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().build();
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            GraphQLSchema schema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
            
            logger.info("📊 Schema contains " + schema.getAllTypesAsList().size() + " types");
            
        } catch (Exception e) {
            throw new RuntimeException("Schema syntax validation failed: " + e.getMessage(), e);
        }
    }

    private void validateSchemaContent(String schemaContent, CLILogger logger) throws Exception {
        // Count types
        long typeCount = schemaContent.lines()
            .filter(line -> line.trim().startsWith("type ") && !line.contains("{"))
            .count();
            
        if (typeCount < minTypes) {
            throw new RuntimeException(
                "Schema contains too few types. Expected at least " + minTypes + 
                ", found " + typeCount
            );
        }
        
        logger.info("📈 Found " + typeCount + " type definitions");
        
        // Check for Query type
        if (checkQuery && !schemaContent.contains("type Query")) {
            logger.warn("⚠️  Schema does not contain a Query type");
        }
        
        // Check for common issues
        validateCommonIssues(schemaContent, logger);
    }

    private void validateCommonIssues(String schemaContent, CLILogger logger) throws Exception {
        String[] lines = schemaContent.split("\n");
        java.util.Set<String> typeNames = new java.util.HashSet<>();
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("type ") || line.startsWith("input ") || 
                line.startsWith("enum ") || line.startsWith("interface ") ||
                line.startsWith("union ")) {
                
                String typeName = extractTypeName(line);
                if (typeName != null) {
                    if (typeNames.contains(typeName)) {
                        throw new RuntimeException("Duplicate type definition found: " + typeName);
                    }
                    typeNames.add(typeName);
                }
            }
        }
        
        logger.debug("No duplicate type definitions found");
    }

    private String extractTypeName(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            return parts[1].replace("{", "").trim();
        }
        return null;
    }
}

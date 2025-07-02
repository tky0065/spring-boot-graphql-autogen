package com.enokdev.graphql.autogen.maven.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for GraphQL AutoGen Maven Plugin.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.1
 */
public class GraphQLMavenPluginConfig {

    /**
     * Base packages to scan for GraphQL annotations.
     */
    private List<String> basePackages = new ArrayList<>();

    /**
     * Output directory for generated files.
     */
    private String outputDirectory;

    /**
     * Schema file name.
     */
    private String schemaFileName = "schema.graphqls";

    /**
     * Whether to include introspection types.
     */
    private boolean includeIntrospection = true;

    /**
     * File encoding.
     */
    private String encoding = "UTF-8";

    /**
     * Naming strategy for GraphQL types.
     */
    private String namingStrategy = "CAMEL_CASE";

    /**
     * Whether to generate input types.
     */
    private boolean generateInputTypes = true;

    /**
     * Custom type mappings.
     */
    private Map<String, String> typeMapping = new HashMap<>();

    /**
     * Maximum query depth allowed.
     */
    private int maxDepth = 15;

    /**
     * Maximum query complexity allowed.
     */
    private int maxComplexity = 200;

    /**
     * Whether to skip generation.
     */
    private boolean skip = false;

    /**
     * Default constructor.
     */
    public GraphQLMavenPluginConfig() {}

    // Getters and setters

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages != null ? basePackages : new ArrayList<>();
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getSchemaFileName() {
        return schemaFileName;
    }

    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }

    public boolean isIncludeIntrospection() {
        return includeIntrospection;
    }

    public void setIncludeIntrospection(boolean includeIntrospection) {
        this.includeIntrospection = includeIntrospection;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getNamingStrategy() {
        return namingStrategy;
    }

    public void setNamingStrategy(String namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public boolean isGenerateInputTypes() {
        return generateInputTypes;
    }

    public void setGenerateInputTypes(boolean generateInputTypes) {
        this.generateInputTypes = generateInputTypes;
    }

    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void setTypeMapping(Map<String, String> typeMapping) {
        this.typeMapping = typeMapping != null ? typeMapping : new HashMap<>();
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxComplexity() {
        return maxComplexity;
    }

    public void setMaxComplexity(int maxComplexity) {
        this.maxComplexity = maxComplexity;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    @Override
    public String toString() {
        return "GraphQLMavenPluginConfig{" +
                "basePackages=" + basePackages +
                ", outputDirectory='" + outputDirectory + '\'' +
                ", schemaFileName='" + schemaFileName + '\'' +
                ", includeIntrospection=" + includeIntrospection +
                ", encoding='" + encoding + '\'' +
                ", namingStrategy='" + namingStrategy + '\'' +
                ", generateInputTypes=" + generateInputTypes +
                ", typeMapping=" + typeMapping +
                ", maxDepth=" + maxDepth +
                ", maxComplexity=" + maxComplexity +
                ", skip=" + skip +
                '}';
    }
}
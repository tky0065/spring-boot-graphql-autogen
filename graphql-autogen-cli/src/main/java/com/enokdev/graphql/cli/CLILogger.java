package com.enokdev.graphql.cli;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Configuration class for CLI settings from file.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class CLIConfig {

    @JsonProperty("basePackages")
    private List<String> basePackages;

    @JsonProperty("excludePackages")
    private List<String> excludePackages;

    @JsonProperty("outputFile")
    private String outputFile;

    @JsonProperty("classpathEntries")
    private List<String> classpathEntries;

    @JsonProperty("generateInputs")
    private boolean generateInputs = true;

    @JsonProperty("namingStrategy")
    private String namingStrategy = "PASCAL_CASE";

    @JsonProperty("includeInheritedFields")
    private boolean includeInheritedFields = true;

    @JsonProperty("maxScanDepth")
    private int maxScanDepth = 10;

    @JsonProperty("failOnError")
    private boolean failOnError = true;

    // Getters and setters
    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public List<String> getClasspathEntries() {
        return classpathEntries;
    }

    public void setClasspathEntries(List<String> classpathEntries) {
        this.classpathEntries = classpathEntries;
    }

    public boolean isGenerateInputs() {
        return generateInputs;
    }

    public void setGenerateInputs(boolean generateInputs) {
        this.generateInputs = generateInputs;
    }

    public String getNamingStrategy() {
        return namingStrategy;
    }

    public void setNamingStrategy(String namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public boolean isIncludeInheritedFields() {
        return includeInheritedFields;
    }

    public void setIncludeInheritedFields(boolean includeInheritedFields) {
        this.includeInheritedFields = includeInheritedFields;
    }

    public int getMaxScanDepth() {
        return maxScanDepth;
    }

    public void setMaxScanDepth(int maxScanDepth) {
        this.maxScanDepth = maxScanDepth;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
}

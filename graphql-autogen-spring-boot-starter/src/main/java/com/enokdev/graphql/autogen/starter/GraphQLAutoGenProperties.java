package com.enokdev.graphql.autogen.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for GraphQL schema auto-generation.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "spring.graphql.autogen")
public class GraphQLAutoGenProperties {

    /**
     * Whether GraphQL schema auto-generation is enabled.
     */
    private boolean enabled = true;

    /**
     * Base packages to scan for GraphQL annotated classes.
     */
    private List<String> basePackages = new ArrayList<>();

    /**
     * Packages to exclude from scanning.
     */
    private List<String> excludePackages = new ArrayList<>();

    /**
     * Output location for the generated schema.
     * Can be a file path or classpath location (prefixed with "classpath:").
     */
    private String schemaLocation = "classpath:graphql";

    /**
     * Whether to format the generated schema for better readability.
     */
    private boolean formatSchema = true;

    /**
     * Whether to sort types and fields in the generated schema.
     */
    private boolean sortSchema = false;

    /**
     * Whether to validate the generated schema.
     */
    private boolean validateSchema = true;

    /**
     * Whether to regenerate schema on application restart.
     */
    private boolean regenerateOnRestart = true;

    /**
     * Whether to generate schema on startup.
     */
    private boolean generateOnStartup = true;

    /**
     * Custom type mappings from Java types to GraphQL types.
     * Keys should be fully qualified Java class names, values should be GraphQL type names.
     */
    private Map<String, String> typeMapping = new HashMap<>();

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public boolean isFormatSchema() {
        return formatSchema;
    }

    public void setFormatSchema(boolean formatSchema) {
        this.formatSchema = formatSchema;
    }

    public boolean isSortSchema() {
        return sortSchema;
    }

    public void setSortSchema(boolean sortSchema) {
        this.sortSchema = sortSchema;
    }

    public boolean isValidateSchema() {
        return validateSchema;
    }

    public void setValidateSchema(boolean validateSchema) {
        this.validateSchema = validateSchema;
    }

    public boolean isRegenerateOnRestart() {
        return regenerateOnRestart;
    }

    public void setRegenerateOnRestart(boolean regenerateOnRestart) {
        this.regenerateOnRestart = regenerateOnRestart;
    }

    public boolean isGenerateOnStartup() {
        return generateOnStartup;
    }

    public void setGenerateOnStartup(boolean generateOnStartup) {
        this.generateOnStartup = generateOnStartup;
    }

    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void setTypeMapping(Map<String, String> typeMapping) {
        this.typeMapping = typeMapping;
    }
}

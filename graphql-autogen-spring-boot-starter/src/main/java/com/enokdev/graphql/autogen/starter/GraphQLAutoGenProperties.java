package com.enokdev.graphql.autogen.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for GraphQL Auto-Generator.
 * These properties can be configured in application.yml or application.properties.
 * 
 * <p>Example configuration:</p>
 * <pre>
 * spring:
 *   graphql:
 *     autogen:
 *       enabled: true
 *       base-packages:
 *         - "com.example.model"
 *         - "com.example.controller"
 *       schema-location: "classpath:graphql/"
 *       naming-strategy: "CAMEL_CASE"
 *       generate-inputs: true
 *       type-mapping:
 *         LocalDateTime: "DateTime"
 *         BigDecimal: "Decimal"
 * </pre>
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.graphql.autogen")
public class GraphQLAutoGenProperties {

    /**
     * Whether GraphQL auto-generation is enabled.
     * Default: true
     */
    private boolean enabled = true;

    /**
     * Base packages to scan for GraphQL annotations.
     * If empty, the main application package will be used.
     */
    private List<String> basePackages = new ArrayList<>();

    /**
     * Location where the generated schema.graphqls file should be written.
     * Default: "classpath:graphql/"
     */
    private String schemaLocation = "classpath:graphql/";

    /**
     * Naming strategy for GraphQL types and fields.
     * Possible values: CAMEL_CASE, PASCAL_CASE, SNAKE_CASE
     * Default: CAMEL_CASE
     */
    private NamingStrategy namingStrategy = NamingStrategy.CAMEL_CASE;

    /**
     * Whether to automatically generate Input types (CreateXXXInput, UpdateXXXInput).
     * Default: true
     */
    private boolean generateInputs = true;

    /**
     * Whether to automatically generate payload types for mutations.
     * Default: true
     */
    private boolean generatePayloads = true;

    /**
     * Custom type mappings from Java types to GraphQL scalar names.
     * Example: LocalDateTime -> DateTime, BigDecimal -> Decimal
     */
    private Map<String, String> typeMapping = new HashMap<>();

    /**
     * Packages to exclude from scanning.
     * Useful for excluding test packages or third-party libraries.
     */
    private List<String> excludePackages = new ArrayList<>();

    /**
     * Whether to include JavaDoc comments as GraphQL descriptions.
     * Default: true
     */
    private boolean includeJavaDoc = true;

    /**
     * Whether to format the generated schema.graphqls file.
     * Default: true
     */
    private boolean formatSchema = true;

    /**
     * Whether to sort types and fields alphabetically in the generated schema.
     * Default: true
     */
    private boolean sortSchema = true;

    /**
     * Mode for schema generation.
     * STARTUP: Generate at application startup
     * BUILD_TIME: Generate during build (requires plugin)
     * Default: STARTUP
     */
    private GenerationMode generationMode = GenerationMode.STARTUP;

    /**
     * Whether to validate the generated schema for consistency.
     * Default: true
     */
    private boolean validateSchema = true;

    public enum NamingStrategy {
        CAMEL_CASE,
        PASCAL_CASE,
        SNAKE_CASE
    }

    public enum GenerationMode {
        STARTUP,
        BUILD_TIME
    }

    // Constructors
    public GraphQLAutoGenProperties() {
        // Set default type mappings
        typeMapping.put("java.time.LocalDateTime", "DateTime");
        typeMapping.put("java.time.LocalDate", "Date");
        typeMapping.put("java.time.LocalTime", "Time");
        typeMapping.put("java.math.BigDecimal", "Decimal");
        typeMapping.put("java.util.UUID", "ID");
    }

    // Getters and setters
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

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    public void setNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public boolean isGenerateInputs() {
        return generateInputs;
    }

    public void setGenerateInputs(boolean generateInputs) {
        this.generateInputs = generateInputs;
    }

    public boolean isGeneratePayloads() {
        return generatePayloads;
    }

    public void setGeneratePayloads(boolean generatePayloads) {
        this.generatePayloads = generatePayloads;
    }

    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void setTypeMapping(Map<String, String> typeMapping) {
        this.typeMapping = typeMapping;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public boolean isIncludeJavaDoc() {
        return includeJavaDoc;
    }

    public void setIncludeJavaDoc(boolean includeJavaDoc) {
        this.includeJavaDoc = includeJavaDoc;
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

    public GenerationMode getGenerationMode() {
        return generationMode;
    }

    public void setGenerationMode(GenerationMode generationMode) {
        this.generationMode = generationMode;
    }

    public boolean isValidateSchema() {
        return validateSchema;
    }

    public void setValidateSchema(boolean validateSchema) {
        this.validateSchema = validateSchema;
    }

    @Override
    public String toString() {
        return "GraphQLAutoGenProperties{" +
                "enabled=" + enabled +
                ", basePackages=" + basePackages +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", namingStrategy=" + namingStrategy +
                ", generateInputs=" + generateInputs +
                ", generatePayloads=" + generatePayloads +
                ", typeMapping=" + typeMapping +
                ", excludePackages=" + excludePackages +
                ", includeJavaDoc=" + includeJavaDoc +
                ", formatSchema=" + formatSchema +
                ", sortSchema=" + sortSchema +
                ", generationMode=" + generationMode +
                ", validateSchema=" + validateSchema +
                '}';
    }
}

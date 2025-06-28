package com.enokdev.graphql.autogen.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for GraphQL AutoGen.
 * Contains all settings needed for schema generation.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class GraphQLAutoGenConfig {

    private List<String> basePackages = new ArrayList<>();
    private boolean generateInputs = true;
    private boolean generateDataLoaders = true;
    private boolean generatePagination = true;
    private NamingStrategy namingStrategy = NamingStrategy.DEFAULT;
    private Map<String, String> typeMappings = new HashMap<>();
    private String schemaLocation = "graphql/";
    private boolean enabled = true;
    private boolean includeJavaDoc = true;
    private boolean generateInterfaces = true;
    private boolean generateUnions = true;
    private boolean validateSchema = true;
    private int maxDepth = 10;
    private int maxComplexity = 1000;

    // Constructors
    public GraphQLAutoGenConfig() {}

    public GraphQLAutoGenConfig(List<String> basePackages) {
        this.basePackages = new ArrayList<>(basePackages);
    }

    // Base packages
    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages != null ? new ArrayList<>(basePackages) : new ArrayList<>();
    }

    public void addBasePackage(String basePackage) {
        if (basePackage != null && !basePackage.trim().isEmpty()) {
            this.basePackages.add(basePackage.trim());
        }
    }

    // Generation flags
    public boolean isGenerateInputs() {
        return generateInputs;
    }

    public void setGenerateInputs(boolean generateInputs) {
        this.generateInputs = generateInputs;
    }

    public boolean isGenerateDataLoaders() {
        return generateDataLoaders;
    }

    public void setGenerateDataLoaders(boolean generateDataLoaders) {
        this.generateDataLoaders = generateDataLoaders;
    }

    public boolean isGeneratePagination() {
        return generatePagination;
    }

    public void setGeneratePagination(boolean generatePagination) {
        this.generatePagination = generatePagination;
    }

    public boolean isGenerateInterfaces() {
        return generateInterfaces;
    }

    public void setGenerateInterfaces(boolean generateInterfaces) {
        this.generateInterfaces = generateInterfaces;
    }

    public boolean isGenerateUnions() {
        return generateUnions;
    }

    public void setGenerateUnions(boolean generateUnions) {
        this.generateUnions = generateUnions;
    }

    // Naming strategy
    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    public void setNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy != null ? namingStrategy : NamingStrategy.DEFAULT;
    }

    // Type mappings
    public Map<String, String> getTypeMappings() {
        return typeMappings;
    }

    public void setTypeMappings(Map<String, String> typeMappings) {
        this.typeMappings = typeMappings != null ? new HashMap<>(typeMappings) : new HashMap<>();
    }

    public void addTypeMapping(String javaType, String graphqlType) {
        if (javaType != null && graphqlType != null) {
            this.typeMappings.put(javaType.trim(), graphqlType.trim());
        }
    }

    public String getGraphQLType(String javaType) {
        return typeMappings.get(javaType);
    }

    // Schema location
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation != null ? schemaLocation : "graphql/";
    }

    // General settings
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIncludeJavaDoc() {
        return includeJavaDoc;
    }

    public void setIncludeJavaDoc(boolean includeJavaDoc) {
        this.includeJavaDoc = includeJavaDoc;
    }

    public boolean isValidateSchema() {
        return validateSchema;
    }

    public void setValidateSchema(boolean validateSchema) {
        this.validateSchema = validateSchema;
    }

    // Query limits
    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = Math.max(1, maxDepth);
    }

    public int getMaxComplexity() {
        return maxComplexity;
    }

    public void setMaxComplexity(int maxComplexity) {
        this.maxComplexity = Math.max(1, maxComplexity);
    }

    // Validation
    public void validate() {
        if (basePackages == null || basePackages.isEmpty()) {
            throw new IllegalArgumentException("At least one base package must be specified");
        }

        for (String basePackage : basePackages) {
            if (basePackage == null || basePackage.trim().isEmpty()) {
                throw new IllegalArgumentException("Base package cannot be null or empty");
            }
        }

        if (maxDepth < 1) {
            throw new IllegalArgumentException("Max depth must be at least 1");
        }

        if (maxComplexity < 1) {
            throw new IllegalArgumentException("Max complexity must be at least 1");
        }
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GraphQLAutoGenConfig config = new GraphQLAutoGenConfig();

        public Builder basePackages(List<String> basePackages) {
            config.setBasePackages(basePackages);
            return this;
        }

        public Builder addBasePackage(String basePackage) {
            config.addBasePackage(basePackage);
            return this;
        }

        public Builder generateInputs(boolean generateInputs) {
            config.setGenerateInputs(generateInputs);
            return this;
        }

        public Builder generateDataLoaders(boolean generateDataLoaders) {
            config.setGenerateDataLoaders(generateDataLoaders);
            return this;
        }

        public Builder generatePagination(boolean generatePagination) {
            config.setGeneratePagination(generatePagination);
            return this;
        }

        public Builder namingStrategy(NamingStrategy namingStrategy) {
            config.setNamingStrategy(namingStrategy);
            return this;
        }

        public Builder typeMappings(Map<String, String> typeMappings) {
            config.setTypeMappings(typeMappings);
            return this;
        }

        public Builder addTypeMapping(String javaType, String graphqlType) {
            config.addTypeMapping(javaType, graphqlType);
            return this;
        }

        public Builder schemaLocation(String schemaLocation) {
            config.setSchemaLocation(schemaLocation);
            return this;
        }

        public Builder enabled(boolean enabled) {
            config.setEnabled(enabled);
            return this;
        }

        public Builder includeJavaDoc(boolean includeJavaDoc) {
            config.setIncludeJavaDoc(includeJavaDoc);
            return this;
        }

        public Builder maxDepth(int maxDepth) {
            config.setMaxDepth(maxDepth);
            return this;
        }

        public Builder maxComplexity(int maxComplexity) {
            config.setMaxComplexity(maxComplexity);
            return this;
        }

        public GraphQLAutoGenConfig build() {
            config.validate();
            return config;
        }
    }

    // Naming strategies
    public enum NamingStrategy {
        /**
         * Use class names as-is.
         */
        DEFAULT,
        
        /**
         * Convert PascalCase to camelCase.
         */
        CAMEL_CASE,
        
        /**
         * Convert to snake_case.
         */
        SNAKE_CASE,
        
        /**
         * Convert to kebab-case.
         */
        KEBAB_CASE,
        
        /**
         * Convert to UPPER_CASE.
         */
        UPPER_CASE,
        
        /**
         * Use custom naming strategy.
         */
        CUSTOM
    }

    @Override
    public String toString() {
        return "GraphQLAutoGenConfig{" +
                "basePackages=" + basePackages +
                ", generateInputs=" + generateInputs +
                ", generateDataLoaders=" + generateDataLoaders +
                ", generatePagination=" + generatePagination +
                ", namingStrategy=" + namingStrategy +
                ", typeMappings=" + typeMappings.size() + " mappings" +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}

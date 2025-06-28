package com.enokdev.graphql.autogen.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for GraphQL AutoGen.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.graphql.autogen")
public class GraphQLAutoGenProperties {
    
    /**
     * Whether GraphQL AutoGen is enabled.
     */
    private boolean enabled = true;
    
    /**
     * Base packages to scan for GraphQL annotations.
     */
    private List<String> basePackages = new ArrayList<>();
    
    /**
     * Naming strategy for GraphQL types and fields.
     */
    private NamingStrategy namingStrategy = NamingStrategy.CAMEL_CASE;
    
    /**
     * Whether to automatically generate input types.
     */
    private boolean generateInputs = true;
    
    /**
     * Whether to automatically generate payload types for mutations.
     */
    private boolean generatePayloads = false;
    
    /**
     * Whether to generate subscriptions.
     */
    private boolean generateSubscriptions = true;
    
    /**
     * Custom type mappings from Java types to GraphQL scalar names.
     */
    private Map<String, String> typeMapping = new HashMap<>();
    
    /**
     * Schema generation configuration.
     */
    private Schema schema = new Schema();
    
    /**
     * Performance optimization settings.
     */
    private Performance performance = new Performance();
    
    /**
     * Validation settings.
     */
    private Validation validation = new Validation();
    
    /**
     * Introspection and debugging settings.
     */
    private Introspection introspection = new Introspection();
    
    // Constructors
    public GraphQLAutoGenProperties() {
        // Set default type mappings
        typeMapping.put("java.time.LocalDateTime", "DateTime");
        typeMapping.put("java.time.LocalDate", "Date");
        typeMapping.put("java.time.LocalTime", "Time");
        typeMapping.put("java.math.BigDecimal", "Decimal");
        typeMapping.put("java.util.UUID", "ID");
    }
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<String> getBasePackages() { return basePackages; }
    public void setBasePackages(List<String> basePackages) { this.basePackages = basePackages; }
    
    public NamingStrategy getNamingStrategy() { return namingStrategy; }
    public void setNamingStrategy(NamingStrategy namingStrategy) { this.namingStrategy = namingStrategy; }
    
    public boolean isGenerateInputs() { return generateInputs; }
    public void setGenerateInputs(boolean generateInputs) { this.generateInputs = generateInputs; }
    
    public boolean isGeneratePayloads() { return generatePayloads; }
    public void setGeneratePayloads(boolean generatePayloads) { this.generatePayloads = generatePayloads; }
    
    public boolean isGenerateSubscriptions() { return generateSubscriptions; }
    public void setGenerateSubscriptions(boolean generateSubscriptions) { this.generateSubscriptions = generateSubscriptions; }
    
    public Map<String, String> getTypeMapping() { return typeMapping; }
    public void setTypeMapping(Map<String, String> typeMapping) { this.typeMapping = typeMapping; }
    
    public Schema getSchema() { return schema; }
    public void setSchema(Schema schema) { this.schema = schema; }
    
    public Performance getPerformance() { return performance; }
    public void setPerformance(Performance performance) { this.performance = performance; }
    
    public Validation getValidation() { return validation; }
    public void setValidation(Validation validation) { this.validation = validation; }
    
    public Introspection getIntrospection() { return introspection; }
    public void setIntrospection(Introspection introspection) { this.introspection = introspection; }
    
    // Nested configuration classes
    
    public static class Schema {
        /**
         * Location where to generate the schema file.
         */
        private String location = "classpath:graphql/";
        
        /**
         * Name of the generated schema file.
         */
        private String fileName = "schema.graphqls";
        
        /**
         * Whether to generate schema file at startup.
         */
        private boolean generateAtStartup = true;
        
        /**
         * Whether to auto-reload schema when annotations change.
         */
        private boolean autoReload = false;
        
        // Getters and setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public boolean isGenerateAtStartup() { return generateAtStartup; }
        public void setGenerateAtStartup(boolean generateAtStartup) { this.generateAtStartup = generateAtStartup; }
        
        public boolean isAutoReload() { return autoReload; }
        public void setAutoReload(boolean autoReload) { this.autoReload = autoReload; }
    }
    
    public static class Performance {
        /**
         * Whether to enable DataLoader for batch loading.
         */
        private boolean enableDataloader = true;
        
        /**
         * Default batch size for DataLoaders.
         */
        private int batchSize = 100;
        
        /**
         * Cache size for type resolution.
         */
        private int cacheSize = 1000;
        
        /**
         * Whether to enable async processing.
         */
        private boolean enableAsync = true;
        
        // Getters and setters
        public boolean isEnableDataloader() { return enableDataloader; }
        public void setEnableDataloader(boolean enableDataloader) { this.enableDataloader = enableDataloader; }
        
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
        
        public int getCacheSize() { return cacheSize; }
        public void setCacheSize(int cacheSize) { this.cacheSize = cacheSize; }
        
        public boolean isEnableAsync() { return enableAsync; }
        public void setEnableAsync(boolean enableAsync) { this.enableAsync = enableAsync; }
    }
    
    public static class Validation {
        /**
         * Whether to enable Bean Validation integration.
         */
        private boolean enableBeanValidation = true;
        
        /**
         * Whether to fail fast on validation errors.
         */
        private boolean failOnValidationError = true;
        
        /**
         * Whether to validate GraphQL schema at startup.
         */
        private boolean validateSchemaAtStartup = true;
        
        // Getters and setters
        public boolean isEnableBeanValidation() { return enableBeanValidation; }
        public void setEnableBeanValidation(boolean enableBeanValidation) { this.enableBeanValidation = enableBeanValidation; }
        
        public boolean isFailOnValidationError() { return failOnValidationError; }
        public void setFailOnValidationError(boolean failOnValidationError) { this.failOnValidationError = failOnValidationError; }
        
        public boolean isValidateSchemaAtStartup() { return validateSchemaAtStartup; }
        public void setValidateSchemaAtStartup(boolean validateSchemaAtStartup) { this.validateSchemaAtStartup = validateSchemaAtStartup; }
    }
    
    public static class Introspection {
        /**
         * Whether to enable GraphQL introspection.
         * Should be disabled in production.
         */
        private boolean enabled = true;
        
        /**
         * Whether to enable GraphQL playground/GraphiQL.
         * Should be disabled in production.
         */
        private boolean playground = true;
        
        /**
         * Path for GraphQL playground.
         */
        private String playgroundPath = "/graphiql";
        
        // Getters and setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public boolean isPlayground() { return playground; }
        public void setPlayground(boolean playground) { this.playground = playground; }
        
        public String getPlaygroundPath() { return playgroundPath; }
        public void setPlaygroundPath(String playgroundPath) { this.playgroundPath = playgroundPath; }
    }
    
    /**
     * Naming strategy for GraphQL elements.
     */
    public enum NamingStrategy {
        /**
         * camelCase naming (default).
         */
        CAMEL_CASE,
        
        /**
         * snake_case naming.
         */
        SNAKE_CASE,
        
        /**
         * PascalCase naming.
         */
        PASCAL_CASE,
        
        /**
         * Keep original Java naming.
         */
        UNCHANGED
    }
}

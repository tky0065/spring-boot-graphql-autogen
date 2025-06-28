package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLDataLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface for generating DataLoader configurations and code.
 * DataLoaders help solve the N+1 query problem by batching multiple requests.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface DataLoaderGenerator {
    
    /**
     * Generates DataLoader configuration for a class with annotated fields/methods.
     * 
     * @param clazz the class to scan for DataLoader annotations
     * @return list of DataLoader configurations
     */
    List<DataLoaderConfiguration> generateDataLoaders(Class<?> clazz);
    
    /**
     * Generates DataLoader configuration for a specific field.
     * 
     * @param field the field annotated with @GraphQLDataLoader
     * @return DataLoader configuration or null if not applicable
     */
    DataLoaderConfiguration generateDataLoader(Field field);
    
    /**
     * Generates DataLoader configuration for a specific method.
     * 
     * @param method the method annotated with @GraphQLDataLoader
     * @return DataLoader configuration or null if not applicable
     */
    DataLoaderConfiguration generateDataLoader(Method method);
    
    /**
     * Checks if a field should have a DataLoader generated.
     * 
     * @param field the field to check
     * @return true if DataLoader should be generated
     */
    boolean shouldGenerateDataLoader(Field field);
    
    /**
     * Checks if a method should have a DataLoader generated.
     * 
     * @param method the method to check
     * @return true if DataLoader should be generated
     */
    boolean shouldGenerateDataLoader(Method method);
    
    /**
     * Generates the Java code for a DataLoader registration.
     * 
     * @param configuration the DataLoader configuration
     * @return Java code string for DataLoader registration
     */
    String generateDataLoaderCode(DataLoaderConfiguration configuration);
    
    /**
     * Generates the Spring Boot configuration for DataLoader beans.
     * 
     * @param configurations list of DataLoader configurations
     * @return Spring configuration class code
     */
    String generateSpringConfiguration(List<DataLoaderConfiguration> configurations);
    
    /**
     * Configuration class for a DataLoader.
     */
    class DataLoaderConfiguration {
        private String name;
        private String keyProperty;
        private Class<?> keyType;
        private Class<?> valueType;
        private Class<?> serviceClass;
        private String batchLoadMethod;
        private int batchSize;
        private boolean cachingEnabled;
        private boolean batchingEnabled;
        private int batchTimeout;
        private boolean statisticsEnabled;
        private GraphQLDataLoader.CacheKeyStrategy cacheKeyStrategy;
        private Field sourceField;
        private Method sourceMethod;
        
        // Constructors
        public DataLoaderConfiguration() {}
        
        public DataLoaderConfiguration(String name, Class<?> keyType, Class<?> valueType) {
            this.name = name;
            this.keyType = keyType;
            this.valueType = valueType;
        }
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getKeyProperty() {
            return keyProperty;
        }
        
        public void setKeyProperty(String keyProperty) {
            this.keyProperty = keyProperty;
        }
        
        public Class<?> getKeyType() {
            return keyType;
        }
        
        public void setKeyType(Class<?> keyType) {
            this.keyType = keyType;
        }
        
        public Class<?> getValueType() {
            return valueType;
        }
        
        public void setValueType(Class<?> valueType) {
            this.valueType = valueType;
        }
        
        public Class<?> getServiceClass() {
            return serviceClass;
        }
        
        public void setServiceClass(Class<?> serviceClass) {
            this.serviceClass = serviceClass;
        }
        
        public String getBatchLoadMethod() {
            return batchLoadMethod;
        }
        
        public void setBatchLoadMethod(String batchLoadMethod) {
            this.batchLoadMethod = batchLoadMethod;
        }
        
        public int getBatchSize() {
            return batchSize;
        }
        
        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
        
        public boolean isCachingEnabled() {
            return cachingEnabled;
        }
        
        public void setCachingEnabled(boolean cachingEnabled) {
            this.cachingEnabled = cachingEnabled;
        }
        
        public boolean isBatchingEnabled() {
            return batchingEnabled;
        }
        
        public void setBatchingEnabled(boolean batchingEnabled) {
            this.batchingEnabled = batchingEnabled;
        }
        
        public int getBatchTimeout() {
            return batchTimeout;
        }
        
        public void setBatchTimeout(int batchTimeout) {
            this.batchTimeout = batchTimeout;
        }
        
        public boolean isStatisticsEnabled() {
            return statisticsEnabled;
        }
        
        public void setStatisticsEnabled(boolean statisticsEnabled) {
            this.statisticsEnabled = statisticsEnabled;
        }
        
        public GraphQLDataLoader.CacheKeyStrategy getCacheKeyStrategy() {
            return cacheKeyStrategy;
        }
        
        public void setCacheKeyStrategy(GraphQLDataLoader.CacheKeyStrategy cacheKeyStrategy) {
            this.cacheKeyStrategy = cacheKeyStrategy;
        }
        
        public Field getSourceField() {
            return sourceField;
        }
        
        public void setSourceField(Field sourceField) {
            this.sourceField = sourceField;
        }
        
        public Method getSourceMethod() {
            return sourceMethod;
        }
        
        public void setSourceMethod(Method sourceMethod) {
            this.sourceMethod = sourceMethod;
        }
        
        @Override
        public String toString() {
            return "DataLoaderConfiguration{" +
                    "name='" + name + '\'' +
                    ", keyType=" + keyType +
                    ", valueType=" + valueType +
                    ", batchSize=" + batchSize +
                    ", cachingEnabled=" + cachingEnabled +
                    '}';
        }
    }
}

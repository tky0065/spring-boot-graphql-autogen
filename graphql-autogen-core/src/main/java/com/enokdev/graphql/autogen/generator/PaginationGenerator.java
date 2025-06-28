package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLPagination;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Interface for generating GraphQL pagination types and field definitions.
 * Supports various pagination strategies including Relay cursor-based and offset-based.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface PaginationGenerator {
    
    /**
     * Generates pagination configuration for a class with annotated fields/methods.
     * 
     * @param clazz the class to scan for pagination annotations
     * @return list of pagination configurations
     */
    List<PaginationConfiguration> generatePaginationConfigurations(Class<?> clazz);
    
    /**
     * Generates pagination configuration for a specific method.
     * 
     * @param method the method annotated with @GraphQLPagination
     * @return pagination configuration or null if not applicable
     */
    PaginationConfiguration generatePaginationConfiguration(Method method);
    
    /**
     * Generates pagination configuration for a specific field.
     * 
     * @param field the field annotated with @GraphQLPagination
     * @return pagination configuration or null if not applicable
     */
    PaginationConfiguration generatePaginationConfiguration(Field field);
    
    /**
     * Generates the Connection type for Relay pagination.
     * 
     * @param configuration the pagination configuration
     * @return GraphQL Connection type
     */
    GraphQLObjectType generateConnectionType(PaginationConfiguration configuration);
    
    /**
     * Generates the Edge type for Relay pagination.
     * 
     * @param configuration the pagination configuration
     * @return GraphQL Edge type
     */
    GraphQLObjectType generateEdgeType(PaginationConfiguration configuration);
    
    /**
     * Generates the PageInfo type for Relay pagination.
     * 
     * @return GraphQL PageInfo type
     */
    GraphQLObjectType generatePageInfoType();
    
    /**
     * Generates pagination arguments for a field.
     * 
     * @param configuration the pagination configuration
     * @return list of GraphQL arguments for pagination
     */
    List<GraphQLArgument> generatePaginationArguments(PaginationConfiguration configuration);
    
    /**
     * Generates a paginated field definition.
     * 
     * @param configuration the pagination configuration
     * @param originalField the original field definition
     * @return modified field definition with pagination
     */
    GraphQLFieldDefinition generatePaginatedField(
        PaginationConfiguration configuration, 
        GraphQLFieldDefinition originalField
    );
    
    /**
     * Checks if a method should have pagination generated.
     * 
     * @param method the method to check
     * @return true if pagination should be generated
     */
    boolean shouldGeneratePagination(Method method);
    
    /**
     * Checks if a field should have pagination generated.
     * 
     * @param field the field to check
     * @return true if pagination should be generated
     */
    boolean shouldGeneratePagination(Field field);
    
    /**
     * Configuration class for pagination.
     */
    class PaginationConfiguration {
        private String connectionName;
        private String edgeName;
        private String nodeTypeName;
        private Class<?> nodeType;
        private GraphQLPagination.PaginationType type;
        private int pageSize;
        private int maxPageSize;
        private boolean includeTotalCount;
        private boolean includeEdges;
        private boolean includePageInfo;
        private GraphQLPagination.CursorStrategy cursorStrategy;
        private String[] customArguments;
        private boolean generateFilters;
        private boolean generateSorting;
        private Method sourceMethod;
        private Field sourceField;
        
        // Constructors
        public PaginationConfiguration() {}
        
        public PaginationConfiguration(String connectionName, String nodeTypeName, Class<?> nodeType) {
            this.connectionName = connectionName;
            this.nodeTypeName = nodeTypeName;
            this.nodeType = nodeType;
        }
        
        // Getters and setters
        public String getConnectionName() {
            return connectionName;
        }
        
        public void setConnectionName(String connectionName) {
            this.connectionName = connectionName;
        }
        
        public String getEdgeName() {
            return edgeName;
        }
        
        public void setEdgeName(String edgeName) {
            this.edgeName = edgeName;
        }
        
        public String getNodeTypeName() {
            return nodeTypeName;
        }
        
        public void setNodeTypeName(String nodeTypeName) {
            this.nodeTypeName = nodeTypeName;
        }
        
        public Class<?> getNodeType() {
            return nodeType;
        }
        
        public void setNodeType(Class<?> nodeType) {
            this.nodeType = nodeType;
        }
        
        public GraphQLPagination.PaginationType getType() {
            return type;
        }
        
        public void setType(GraphQLPagination.PaginationType type) {
            this.type = type;
        }
        
        public int getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        
        public int getMaxPageSize() {
            return maxPageSize;
        }
        
        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
        
        public boolean isIncludeTotalCount() {
            return includeTotalCount;
        }
        
        public void setIncludeTotalCount(boolean includeTotalCount) {
            this.includeTotalCount = includeTotalCount;
        }
        
        public boolean isIncludeEdges() {
            return includeEdges;
        }
        
        public void setIncludeEdges(boolean includeEdges) {
            this.includeEdges = includeEdges;
        }
        
        public boolean isIncludePageInfo() {
            return includePageInfo;
        }
        
        public void setIncludePageInfo(boolean includePageInfo) {
            this.includePageInfo = includePageInfo;
        }
        
        public GraphQLPagination.CursorStrategy getCursorStrategy() {
            return cursorStrategy;
        }
        
        public void setCursorStrategy(GraphQLPagination.CursorStrategy cursorStrategy) {
            this.cursorStrategy = cursorStrategy;
        }
        
        public String[] getCustomArguments() {
            return customArguments;
        }
        
        public void setCustomArguments(String[] customArguments) {
            this.customArguments = customArguments;
        }
        
        public boolean isGenerateFilters() {
            return generateFilters;
        }
        
        public void setGenerateFilters(boolean generateFilters) {
            this.generateFilters = generateFilters;
        }
        
        public boolean isGenerateSorting() {
            return generateSorting;
        }
        
        public void setGenerateSorting(boolean generateSorting) {
            this.generateSorting = generateSorting;
        }
        
        public Method getSourceMethod() {
            return sourceMethod;
        }
        
        public void setSourceMethod(Method sourceMethod) {
            this.sourceMethod = sourceMethod;
        }
        
        public Field getSourceField() {
            return sourceField;
        }
        
        public void setSourceField(Field sourceField) {
            this.sourceField = sourceField;
        }
        
        @Override
        public String toString() {
            return "PaginationConfiguration{" +
                    "connectionName='" + connectionName + '\'' +
                    ", nodeTypeName='" + nodeTypeName + '\'' +
                    ", type=" + type +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }
}

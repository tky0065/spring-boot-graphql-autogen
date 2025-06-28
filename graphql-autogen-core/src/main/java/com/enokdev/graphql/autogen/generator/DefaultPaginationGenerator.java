package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLPagination;
import com.enokdev.graphql.autogen.annotation.GraphQLType;
import graphql.Scalars;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Default implementation of PaginationGenerator.
 * Generates pagination types and configurations following the Relay specification.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultPaginationGenerator implements PaginationGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultPaginationGenerator.class);
    
    // Cache for generated types to avoid duplicates
    private final Map<String, GraphQLObjectType> connectionTypeCache = new HashMap<>();
    private final Map<String, GraphQLObjectType> edgeTypeCache = new HashMap<>();
    private GraphQLObjectType pageInfoType;
    
    @Override
    public List<PaginationConfiguration> generatePaginationConfigurations(Class<?> clazz) {
        log.debug("Generating pagination configurations for class: {}", clazz.getName());
        
        List<PaginationConfiguration> configurations = new ArrayList<>();
        
        // Process methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (shouldGeneratePagination(method)) {
                PaginationConfiguration config = generatePaginationConfiguration(method);
                if (config != null) {
                    configurations.add(config);
                }
            }
        }
        
        // Process fields
        for (Field field : clazz.getDeclaredFields()) {
            if (shouldGeneratePagination(field)) {
                PaginationConfiguration config = generatePaginationConfiguration(field);
                if (config != null) {
                    configurations.add(config);
                }
            }
        }
        
        log.debug("Generated {} pagination configurations for class {}", configurations.size(), clazz.getName());
        return configurations;
    }
    
    @Override
    public PaginationConfiguration generatePaginationConfiguration(Method method) {
        if (!shouldGeneratePagination(method)) {
            return null;
        }
        
        try {
            GraphQLPagination annotation = method.getAnnotation(GraphQLPagination.class);
            
            PaginationConfiguration config = new PaginationConfiguration();
            config.setSourceMethod(method);
            
            // Determine node type from return type
            Class<?> nodeType = determineNodeType(method);
            config.setNodeType(nodeType);
            config.setNodeTypeName(getNodeTypeName(nodeType));
            
            // Generate names
            String connectionName = annotation.connectionName().isEmpty() ? 
                generateConnectionName(nodeType) : annotation.connectionName();
            config.setConnectionName(connectionName);
            
            String edgeName = annotation.edgeName().isEmpty() ? 
                generateEdgeName(nodeType) : annotation.edgeName();
            config.setEdgeName(edgeName);
            
            // Set properties from annotation
            config.setType(annotation.type());
            config.setPageSize(annotation.pageSize());
            config.setMaxPageSize(annotation.maxPageSize());
            config.setIncludeTotalCount(annotation.includeTotalCount());
            config.setIncludeEdges(annotation.includeEdges());
            config.setIncludePageInfo(annotation.includePageInfo());
            config.setCursorStrategy(annotation.cursorStrategy());
            config.setCustomArguments(annotation.customArguments());
            config.setGenerateFilters(annotation.generateFilters());
            config.setGenerateSorting(annotation.generateSorting());
            
            log.debug("Generated pagination config: {}", config);
            return config;
            
        } catch (Exception e) {
            log.error("Error generating pagination for method {}: {}", method.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public PaginationConfiguration generatePaginationConfiguration(Field field) {
        if (!shouldGeneratePagination(field)) {
            return null;
        }
        
        try {
            GraphQLPagination annotation = field.getAnnotation(GraphQLPagination.class);
            
            PaginationConfiguration config = new PaginationConfiguration();
            config.setSourceField(field);
            
            // Determine node type from field type
            Class<?> nodeType = determineNodeType(field);
            config.setNodeType(nodeType);
            config.setNodeTypeName(getNodeTypeName(nodeType));
            
            // Generate names
            String connectionName = annotation.connectionName().isEmpty() ? 
                generateConnectionName(nodeType) : annotation.connectionName();
            config.setConnectionName(connectionName);
            
            String edgeName = annotation.edgeName().isEmpty() ? 
                generateEdgeName(nodeType) : annotation.edgeName();
            config.setEdgeName(edgeName);
            
            // Set properties from annotation
            config.setType(annotation.type());
            config.setPageSize(annotation.pageSize());
            config.setMaxPageSize(annotation.maxPageSize());
            config.setIncludeTotalCount(annotation.includeTotalCount());
            config.setIncludeEdges(annotation.includeEdges());
            config.setIncludePageInfo(annotation.includePageInfo());
            config.setCursorStrategy(annotation.cursorStrategy());
            config.setCustomArguments(annotation.customArguments());
            config.setGenerateFilters(annotation.generateFilters());
            config.setGenerateSorting(annotation.generateSorting());
            
            log.debug("Generated pagination config: {}", config);
            return config;
            
        } catch (Exception e) {
            log.error("Error generating pagination for field {}: {}", field.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public GraphQLObjectType generateConnectionType(PaginationConfiguration configuration) {
        String connectionName = configuration.getConnectionName();
        
        // Return cached type if already generated
        if (connectionTypeCache.containsKey(connectionName)) {
            return connectionTypeCache.get(connectionName);
        }
        
        GraphQLObjectType.Builder connectionBuilder = GraphQLObjectType.newObject()
            .name(connectionName)
            .description("A connection to a list of " + configuration.getNodeTypeName() + " items");
        
        // Add edges field
        if (configuration.isIncludeEdges()) {
            GraphQLObjectType edgeType = generateEdgeType(configuration);
            connectionBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("edges")
                .description("Information to aid in pagination")
                .type(GraphQLNonNull.nonNull(GraphQLList.list(GraphQLNonNull.nonNull(edgeType))))
                .build());
        }
        
        // Add pageInfo field
        if (configuration.isIncludePageInfo()) {
            connectionBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("pageInfo")
                .description("Information to aid in pagination")
                .type(GraphQLNonNull.nonNull(generatePageInfoType()))
                .build());
        }
        
        // Add totalCount field
        if (configuration.isIncludeTotalCount()) {
            connectionBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("totalCount")
                .description("The total number of items available")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLInt))
                .build());
        }
        
        GraphQLObjectType connectionType = connectionBuilder.build();
        connectionTypeCache.put(connectionName, connectionType);
        
        log.debug("Generated connection type: {}", connectionName);
        return connectionType;
    }
    
    @Override
    public GraphQLObjectType generateEdgeType(PaginationConfiguration configuration) {
        String edgeName = configuration.getEdgeName();
        
        // Return cached type if already generated
        if (edgeTypeCache.containsKey(edgeName)) {
            return edgeTypeCache.get(edgeName);
        }
        
        // For now, we'll create a placeholder. In a real implementation,
        // we'd need access to the TypeResolver to get the actual node type
        GraphQLObjectType edgeType = GraphQLObjectType.newObject()
            .name(edgeName)
            .description("An edge in a connection from an object to another object of type " + configuration.getNodeTypeName())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("node")
                .description("The item at the end of the edge")
                .type(GraphQLTypeReference.typeRef(configuration.getNodeTypeName()))
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("cursor")
                .description("A cursor for use in pagination")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
                .build())
            .build();
        
        edgeTypeCache.put(edgeName, edgeType);
        
        log.debug("Generated edge type: {}", edgeName);
        return edgeType;
    }
    
    @Override
    public GraphQLObjectType generatePageInfoType() {
        if (pageInfoType != null) {
            return pageInfoType;
        }
        
        pageInfoType = GraphQLObjectType.newObject()
            .name("PageInfo")
            .description("Information about pagination in a connection")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("hasNextPage")
                .description("When paginating forwards, are there more items?")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLBoolean))
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("hasPreviousPage")
                .description("When paginating backwards, are there more items?")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLBoolean))
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("startCursor")
                .description("When paginating backwards, the cursor to continue")
                .type(Scalars.GraphQLString)
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("endCursor")
                .description("When paginating forwards, the cursor to continue")
                .type(Scalars.GraphQLString)
                .build())
            .build();
        
        log.debug("Generated PageInfo type");
        return pageInfoType;
    }
    
    @Override
    public List<GraphQLArgument> generatePaginationArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        switch (configuration.getType()) {
            case RELAY_CURSOR:
                arguments.addAll(generateRelayCursorArguments(configuration));
                break;
            case OFFSET_BASED:
                arguments.addAll(generateOffsetBasedArguments(configuration));
                break;
            case PAGE_BASED:
                arguments.addAll(generatePageBasedArguments(configuration));
                break;
            case CUSTOM:
                // Custom arguments would be handled separately
                break;
        }
        
        // Add custom arguments
        for (String customArg : configuration.getCustomArguments()) {
            arguments.add(GraphQLArgument.newArgument()
                .name(customArg)
                .type(Scalars.GraphQLString)
                .build());
        }
        
        // Add filter arguments if enabled
        if (configuration.isGenerateFilters()) {
            arguments.addAll(generateFilterArguments(configuration));
        }
        
        // Add sort arguments if enabled
        if (configuration.isGenerateSorting()) {
            arguments.addAll(generateSortArguments(configuration));
        }
        
        return arguments;
    }
    
    @Override
    public GraphQLFieldDefinition generatePaginatedField(
            PaginationConfiguration configuration, 
            GraphQLFieldDefinition originalField) {
        
        // Generate the connection type
        GraphQLObjectType connectionType = generateConnectionType(configuration);
        
        // Generate pagination arguments
        List<GraphQLArgument> paginationArgs = generatePaginationArguments(configuration);
        
        // Build the new field with pagination
        GraphQLFieldDefinition.Builder fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .name(originalField.getName())
            .description(originalField.getDescription())
            .type(connectionType);
        
        // Add original arguments
        fieldBuilder.arguments(originalField.getArguments());
        
        // Add pagination arguments
        fieldBuilder.arguments(paginationArgs);
        
        return fieldBuilder.build();
    }
    
    @Override
    public boolean shouldGeneratePagination(Method method) {
        // Must have @GraphQLPagination annotation
        if (!method.isAnnotationPresent(GraphQLPagination.class)) {
            return false;
        }
        
        // Must be enabled
        GraphQLPagination annotation = method.getAnnotation(GraphQLPagination.class);
        if (!annotation.enabled()) {
            return false;
        }
        
        // Should also have @GraphQLField or be in a GraphQL type
        return method.isAnnotationPresent(GraphQLField.class) || 
               method.getDeclaringClass().isAnnotationPresent(GraphQLType.class);
    }
    
    @Override
    public boolean shouldGeneratePagination(Field field) {
        // Must have @GraphQLPagination annotation
        if (!field.isAnnotationPresent(GraphQLPagination.class)) {
            return false;
        }
        
        // Must be enabled
        GraphQLPagination annotation = field.getAnnotation(GraphQLPagination.class);
        if (!annotation.enabled()) {
            return false;
        }
        
        // Should also have @GraphQLField or be in a GraphQL type
        return field.isAnnotationPresent(GraphQLField.class) || 
               field.getDeclaringClass().isAnnotationPresent(GraphQLType.class);
    }
    
    // Helper methods
    
    private Class<?> determineNodeType(Method method) {
        Type returnType = method.getGenericReturnType();
        return extractCollectionElementType(returnType);
    }
    
    private Class<?> determineNodeType(Field field) {
        Type fieldType = field.getGenericType();
        return extractCollectionElementType(fieldType);
    }
    
    private Class<?> extractCollectionElementType(Type type) {
        if (type instanceof ParameterizedType paramType) {
            Type rawType = paramType.getRawType();
            if (rawType instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass)) {
                Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                    return (Class<?>) typeArgs[0];
                }
            }
        }
        
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        
        return Object.class;
    }
    
    private String getNodeTypeName(Class<?> nodeType) {
        if (nodeType.isAnnotationPresent(GraphQLType.class)) {
            GraphQLType annotation = nodeType.getAnnotation(GraphQLType.class);
            if (!annotation.name().isEmpty()) {
                return annotation.name();
            }
        }
        return nodeType.getSimpleName();
    }
    
    private String generateConnectionName(Class<?> nodeType) {
        return getNodeTypeName(nodeType) + "Connection";
    }
    
    private String generateEdgeName(Class<?> nodeType) {
        return getNodeTypeName(nodeType) + "Edge";
    }
    
    private List<GraphQLArgument> generateRelayCursorArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        arguments.add(GraphQLArgument.newArgument()
            .name("first")
            .description("Returns the first n elements from the list")
            .type(Scalars.GraphQLInt)
            .build());
        
        arguments.add(GraphQLArgument.newArgument()
            .name("after")
            .description("Returns the elements in the list that come after the specified cursor")
            .type(Scalars.GraphQLString)
            .build());
        
        arguments.add(GraphQLArgument.newArgument()
            .name("last")
            .description("Returns the last n elements from the list")
            .type(Scalars.GraphQLInt)
            .build());
        
        arguments.add(GraphQLArgument.newArgument()
            .name("before")
            .description("Returns the elements in the list that come before the specified cursor")
            .type(Scalars.GraphQLString)
            .build());
        
        return arguments;
    }
    
    private List<GraphQLArgument> generateOffsetBasedArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        arguments.add(GraphQLArgument.newArgument()
            .name("limit")
            .description("Maximum number of items to return")
            .type(Scalars.GraphQLInt)
            .defaultValue(configuration.getPageSize())
            .build());
        
        arguments.add(GraphQLArgument.newArgument()
            .name("offset")
            .description("Number of items to skip")
            .type(Scalars.GraphQLInt)
            .defaultValue(0)
            .build());
        
        return arguments;
    }
    
    private List<GraphQLArgument> generatePageBasedArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        arguments.add(GraphQLArgument.newArgument()
            .name("page")
            .description("Page number (0-based)")
            .type(Scalars.GraphQLInt)
            .defaultValue(0)
            .build());
        
        arguments.add(GraphQLArgument.newArgument()
            .name("size")
            .description("Number of items per page")
            .type(Scalars.GraphQLInt)
            .defaultValue(configuration.getPageSize())
            .build());
        
        return arguments;
    }
    
    private List<GraphQLArgument> generateFilterArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        // Add a generic filter argument for now
        // In a real implementation, this would analyze the node type fields
        arguments.add(GraphQLArgument.newArgument()
            .name("filter")
            .description("Filter criteria")
            .type(Scalars.GraphQLString)
            .build());
        
        return arguments;
    }
    
    private List<GraphQLArgument> generateSortArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        // Add sorting arguments
        arguments.add(GraphQLArgument.newArgument()
            .name("sortBy")
            .description("Field to sort by")
            .type(Scalars.GraphQLString)
            .build());
        
        arguments.add(GraphQLArgument.newArgument()
            .name("sortDirection")
            .description("Sort direction (ASC or DESC)")
            .type(Scalars.GraphQLString)
            .defaultValue("ASC")
            .build());
        
        return arguments;
    }
}

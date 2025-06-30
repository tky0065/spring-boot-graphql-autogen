package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLPagination;
import graphql.Scalars;
import graphql.language.IntValue;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of PaginationGenerator for creating GraphQL pagination fields.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultPaginationGenerator implements PaginationGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultPaginationGenerator.class);
    
    @Override
    public List<PaginationConfiguration> generatePaginationConfigurations(Class<?> clazz) {
        List<PaginationConfiguration> configurations = new ArrayList<>();
        
        // Scan methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (shouldGeneratePagination(method)) {
                PaginationConfiguration config = generatePaginationConfiguration(method);
                if (config != null) {
                    configurations.add(config);
                }
            }
        }
        
        // Scan fields
        for (Field field : clazz.getDeclaredFields()) {
            if (shouldGeneratePagination(field)) {
                PaginationConfiguration config = generatePaginationConfiguration(field);
                if (config != null) {
                    configurations.add(config);
                }
            }
        }
        
        return configurations;
    }
    
    @Override
    public PaginationConfiguration generatePaginationConfiguration(Method method) {
        if (!method.isAnnotationPresent(GraphQLPagination.class)) {
            return null;
        }
        
        GraphQLPagination annotation = method.getAnnotation(GraphQLPagination.class);
        
        // Determine node type from method return type
        Class<?> nodeType = determineNodeType(method.getGenericReturnType());
        if (nodeType == null) {
            log.warn("Could not determine node type for method: {}", method.getName());
            return null;
        }
        
        String connectionName = annotation.connectionName().isEmpty() 
            ? nodeType.getSimpleName() + "Connection" 
            : annotation.connectionName();
            
        String edgeName = annotation.edgeName().isEmpty() 
            ? nodeType.getSimpleName() + "Edge" 
            : annotation.edgeName();
        
        PaginationConfiguration config = new PaginationConfiguration(connectionName, nodeType.getSimpleName(), nodeType);
        config.setEdgeName(edgeName);
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
        config.setSourceMethod(method);
        
        return config;
    }
    
    @Override
    public PaginationConfiguration generatePaginationConfiguration(Field field) {
        if (!field.isAnnotationPresent(GraphQLPagination.class)) {
            return null;
        }
        
        GraphQLPagination annotation = field.getAnnotation(GraphQLPagination.class);
        
        // Determine node type from field type
        Class<?> nodeType = determineNodeType(field.getGenericType());
        if (nodeType == null) {
            log.warn("Could not determine node type for field: {}", field.getName());
            return null;
        }
        
        String connectionName = annotation.connectionName().isEmpty() 
            ? nodeType.getSimpleName() + "Connection" 
            : annotation.connectionName();
            
        String edgeName = annotation.edgeName().isEmpty() 
            ? nodeType.getSimpleName() + "Edge" 
            : annotation.edgeName();
        
        PaginationConfiguration config = new PaginationConfiguration(connectionName, nodeType.getSimpleName(), nodeType);
        config.setEdgeName(edgeName);
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
        config.setSourceField(field);
        
        return config;
    }
    
    @Override
    public GraphQLObjectType generateConnectionType(PaginationConfiguration configuration) {
        GraphQLObjectType.Builder builder = GraphQLObjectType.newObject()
            .name(configuration.getConnectionName())
            .description("Connection type for " + configuration.getNodeTypeName());
        
        if (configuration.isIncludeEdges()) {
            GraphQLObjectType edgeType = generateEdgeType(configuration);
            builder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("edges")
                .type(GraphQLList.list(edgeType))
                .description("List of edges")
                .build());
        }
        
        if (configuration.isIncludePageInfo()) {
            GraphQLObjectType pageInfoType = generatePageInfoType();
            builder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("pageInfo")
                .type(GraphQLNonNull.nonNull(pageInfoType))
                .description("Information about pagination")
                .build());
        }
        
        if (configuration.isIncludeTotalCount()) {
            builder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("totalCount")
                .type(Scalars.GraphQLInt)
                .description("Total number of items")
                .build());
        }
        
        return builder.build();
    }
    
    @Override
    public GraphQLObjectType generateEdgeType(PaginationConfiguration configuration) {
        return GraphQLObjectType.newObject()
            .name(configuration.getEdgeName())
            .description("Edge type for " + configuration.getNodeTypeName())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("node")
                .type(GraphQLTypeReference.typeRef(configuration.getNodeTypeName()))
                .description("The item at the end of the edge")
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("cursor")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
                .description("Cursor for this edge")
                .build())
            .build();
    }
    
    @Override
    public GraphQLObjectType generatePageInfoType() {
        return GraphQLObjectType.newObject()
            .name("PageInfo")
            .description("Information about pagination")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("hasNextPage")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLBoolean))
                .description("Whether there are more items available")
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("hasPreviousPage")
                .type(GraphQLNonNull.nonNull(Scalars.GraphQLBoolean))
                .description("Whether there are previous items available")
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("startCursor")
                .type(Scalars.GraphQLString)
                .description("Cursor of the first item")
                .build())
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("endCursor")
                .type(Scalars.GraphQLString)
                .description("Cursor of the last item")
                .build())
            .build();
    }
    
    @Override
    public List<GraphQLArgument> generatePaginationArguments(PaginationConfiguration configuration) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        
        switch (configuration.getType()) {
            case RELAY_CURSOR:
                arguments.add(GraphQLArgument.newArgument()
                    .name("first")
                    .type(Scalars.GraphQLInt)
                    .description("Number of items to return from the beginning")
                    .build());
                arguments.add(GraphQLArgument.newArgument()
                    .name("after")
                    .type(Scalars.GraphQLString)
                    .description("Cursor for forward pagination")
                    .build());
                arguments.add(GraphQLArgument.newArgument()
                    .name("last")
                    .type(Scalars.GraphQLInt)
                    .description("Number of items to return from the end")
                    .build());
                arguments.add(GraphQLArgument.newArgument()
                    .name("before")
                    .type(Scalars.GraphQLString)
                    .description("Cursor for backward pagination")
                    .build());
                break;
                
            case OFFSET_BASED:
                arguments.add(GraphQLArgument.newArgument()
                    .name("limit")
                    .type(Scalars.GraphQLInt)
                    .defaultValueLiteral(IntValue.newIntValue(BigInteger.valueOf(configuration.getPageSize())).build())
                    .description("Maximum number of items to return")
                    .build());
                arguments.add(GraphQLArgument.newArgument()
                    .name("offset")
                    .type(Scalars.GraphQLInt)
                    .defaultValueLiteral(IntValue.newIntValue(BigInteger.valueOf(0)).build())
                    .description("Number of items to skip")
                    .build());
                break;
                
            case PAGE_BASED:
                arguments.add(GraphQLArgument.newArgument()
                    .name("page")
                    .type(Scalars.GraphQLInt)
                    .defaultValueLiteral(IntValue.newIntValue(BigInteger.valueOf(1)).build())
                    .description("Page number (1-based)")
                    .build());
                arguments.add(GraphQLArgument.newArgument()
                    .name("size")
                    .type(Scalars.GraphQLInt)
                    .defaultValueLiteral(IntValue.newIntValue(BigInteger.valueOf(configuration.getPageSize())).build())
                    .description("Number of items per page")
                    .build());
                break;
        }
        
        return arguments;
    }
    
    @Override
    public GraphQLFieldDefinition generatePaginatedField(PaginationConfiguration configuration, GraphQLFieldDefinition originalField) {
        List<GraphQLArgument> paginationArgs = generatePaginationArguments(configuration);
        GraphQLObjectType connectionType = generateConnectionType(configuration);
        
        return GraphQLFieldDefinition.newFieldDefinition()
            .name(originalField.getName())
            .type(connectionType)
            .arguments(paginationArgs)
            .description(originalField.getDescription())
            .build();
    }
    
    @Override
    public boolean shouldGeneratePagination(Method method) {
        return method.isAnnotationPresent(GraphQLPagination.class) && 
               method.getAnnotation(GraphQLPagination.class).enabled();
    }
    
    @Override
    public boolean shouldGeneratePagination(Field field) {
        return field.isAnnotationPresent(GraphQLPagination.class) && 
               field.getAnnotation(GraphQLPagination.class).enabled();
    }
    
    private Class<?> determineNodeType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        return null;
    }
}

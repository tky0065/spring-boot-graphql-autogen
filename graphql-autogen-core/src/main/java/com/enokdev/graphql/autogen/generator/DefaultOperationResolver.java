package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of OperationResolver for converting controller methods to GraphQL operations.
 */
@Component
public class DefaultOperationResolver implements OperationResolver {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultOperationResolver.class);
    private final TypeResolver typeResolver;
    
    public DefaultOperationResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveQueries(Class<?> controllerClass) {
        List<GraphQLFieldDefinition> queries = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isQuery(method)) {
                GraphQLFieldDefinition field = resolveQuery(method);
                if (field != null) {
                    queries.add(field);
                }
            }
        }
        return queries;
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveMutations(Class<?> controllerClass) {
        List<GraphQLFieldDefinition> mutations = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isMutation(method)) {
                GraphQLFieldDefinition field = resolveMutation(method);
                if (field != null) {
                    mutations.add(field);
                }
            }
        }
        return mutations;
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveSubscriptions(Class<?> controllerClass) {
        List<GraphQLFieldDefinition> subscriptions = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isSubscription(method)) {
                GraphQLFieldDefinition field = resolveSubscription(method);
                if (field != null) {
                    subscriptions.add(field);
                }
            }
        }
        return subscriptions;
    }
    
    @Override
    public GraphQLFieldDefinition resolveQuery(Method method) {
        if (!isQuery(method)) {
            return null;
        }
        
        String fieldName = getOperationName(method);
        GraphQLOutputType returnType = resolveReturnType(method);
        
        return GraphQLFieldDefinition.newFieldDefinition()
            .name(fieldName)
            .type(returnType)
            .build();
    }
    
    @Override
    public GraphQLFieldDefinition resolveMutation(Method method) {
        if (!isMutation(method)) {
            return null;
        }
        
        String fieldName = getOperationName(method);
        GraphQLOutputType returnType = resolveReturnType(method);
        
        return GraphQLFieldDefinition.newFieldDefinition()
            .name(fieldName)
            .type(returnType)
            .build();
    }
    
    @Override
    public GraphQLFieldDefinition resolveSubscription(Method method) {
        if (!isSubscription(method)) {
            return null;
        }
        
        String fieldName = getOperationName(method);
        GraphQLOutputType returnType = resolveReturnType(method);
        
        return GraphQLFieldDefinition.newFieldDefinition()
            .name(fieldName)
            .type(returnType)
            .build();
    }
    
    @Override
    public boolean isQuery(Method method) {
        return method.isAnnotationPresent(GraphQLQuery.class) && 
               !Modifier.isStatic(method.getModifiers()) &&
               method.getDeclaringClass().isAnnotationPresent(GraphQLController.class);
    }
    
    @Override
    public boolean isMutation(Method method) {
        return method.isAnnotationPresent(GraphQLMutation.class) && 
               !Modifier.isStatic(method.getModifiers()) &&
               method.getDeclaringClass().isAnnotationPresent(GraphQLController.class);
    }
    
    @Override
    public boolean isSubscription(Method method) {
        return method.isAnnotationPresent(GraphQLSubscription.class) && 
               !Modifier.isStatic(method.getModifiers()) &&
               method.getDeclaringClass().isAnnotationPresent(GraphQLController.class);
    }
    
    private String getOperationName(Method method) {
        if (method.isAnnotationPresent(GraphQLQuery.class)) {
            String name = method.getAnnotation(GraphQLQuery.class).name();
            return name.isEmpty() ? method.getName() : name;
        }
        if (method.isAnnotationPresent(GraphQLMutation.class)) {
            String name = method.getAnnotation(GraphQLMutation.class).name();
            return name.isEmpty() ? method.getName() : name;
        }
        if (method.isAnnotationPresent(GraphQLSubscription.class)) {
            String name = method.getAnnotation(GraphQLSubscription.class).name();
            return name.isEmpty() ? method.getName() : name;
        }
        return method.getName();
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveQueryOperations(Class<?> controllerClass) {
        return resolveQueries(controllerClass);
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveMutationOperations(Class<?> controllerClass) {
        return resolveMutations(controllerClass);
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveSubscriptionOperations(Class<?> controllerClass) {
        return resolveSubscriptions(controllerClass);
    }
    
    // Additional utility methods for backward compatibility
    public boolean isQueryOperation(Method method) {
        return isQuery(method);
    }
    
    public boolean isMutationOperation(Method method) {
        return isMutation(method);
    }
    
    public boolean isSubscriptionOperation(Method method) {
        return isSubscription(method);
    }
    
    private GraphQLOutputType resolveReturnType(Method method) {
        if (method.getReturnType() == void.class) {
            return Scalars.GraphQLBoolean;
        }
        
        graphql.schema.GraphQLType type = typeResolver.resolveType(method.getGenericReturnType());
        return (GraphQLOutputType) type;
    }
}

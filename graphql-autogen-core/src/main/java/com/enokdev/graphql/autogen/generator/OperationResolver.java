package com.enokdev.graphql.autogen.generator;

import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface for resolving Java methods to GraphQL operation definitions.
 * 
 * Handles the conversion from controller methods to GraphQL queries, mutations, and subscriptions.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface OperationResolver {
    
    /**
     * Resolves all query operations from a controller class.
     * 
     * @param controllerClass The controller class to scan
     * @return List of GraphQL query field definitions
     */
    List<GraphQLFieldDefinition> resolveQueries(Class<?> controllerClass);
    
    /**
     * Resolves all mutation operations from a controller class.
     * 
     * @param controllerClass The controller class to scan
     * @return List of GraphQL mutation field definitions
     */
    List<GraphQLFieldDefinition> resolveMutations(Class<?> controllerClass);
    
    /**
     * Resolves all subscription operations from a controller class.
     * 
     * @param controllerClass The controller class to scan
     * @return List of GraphQL subscription field definitions
     */
    List<GraphQLFieldDefinition> resolveSubscriptions(Class<?> controllerClass);
    
    /**
     * Resolves a specific method to a GraphQL query operation.
     * 
     * @param method The method to resolve
     * @return GraphQL field definition, or null if not a query
     */
    GraphQLFieldDefinition resolveQuery(Method method);
    
    /**
     * Resolves a specific method to a GraphQL mutation operation.
     * 
     * @param method The method to resolve
     * @return GraphQL field definition, or null if not a mutation
     */
    GraphQLFieldDefinition resolveMutation(Method method);
    
    /**
     * Resolves a specific method to a GraphQL subscription operation.
     * 
     * @param method The method to resolve
     * @return GraphQL field definition, or null if not a subscription
     */
    GraphQLFieldDefinition resolveSubscription(Method method);
    
    /**
     * Checks if a method is a GraphQL query operation.
     * 
     * @param method The method to check
     * @return true if method is a query operation
     */
    boolean isQueryOperation(Method method);
    
    /**
     * Checks if a method is a GraphQL mutation operation.
     * 
     * @param method The method to check
     * @return true if method is a mutation operation
     */
    boolean isMutationOperation(Method method);
    
    /**
     * Checks if a method is a GraphQL subscription operation.
     * 
     * @param method The method to check
     * @return true if method is a subscription operation
     */
    boolean isSubscriptionOperation(Method method);
}

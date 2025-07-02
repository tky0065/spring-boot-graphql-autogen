package com.enokdev.graphql.autogen.generator;

import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface for resolving Java controller methods to GraphQL operations (queries, mutations, subscriptions).
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface OperationResolver {
    
    /**
     * Resolves all query operations from a controller class.
     * 
     * @param controllerClass The controller class to resolve queries from
     * @return List of GraphQL field definitions for queries
     */
    List<GraphQLFieldDefinition> resolveQueries(Class<?> controllerClass);
    
    /**
     * Resolves all mutation operations from a controller class.
     * 
     * @param controllerClass The controller class to resolve mutations from
     * @return List of GraphQL field definitions for mutations
     */
    List<GraphQLFieldDefinition> resolveMutations(Class<?> controllerClass);
    
    /**
     * Resolves all subscription operations from a controller class.
     * 
     * @param controllerClass The controller class to resolve subscriptions from
     * @return List of GraphQL field definitions for subscriptions
     */
    List<GraphQLFieldDefinition> resolveSubscriptions(Class<?> controllerClass);
    
    /**
     * Resolves a single method to a GraphQL query field.
     * 
     * @param method The method to resolve
     * @return GraphQL field definition or null if method is not a query
     */
    GraphQLFieldDefinition resolveQuery(Method method);
    
    /**
     * Resolves a single method to a GraphQL mutation field.
     * 
     * @param method The method to resolve
     * @return GraphQL field definition or null if method is not a mutation
     */
    GraphQLFieldDefinition resolveMutation(Method method);
    
    /**
     * Resolves a single method to a GraphQL subscription field.
     * 
     * @param method The method to resolve
     * @return GraphQL field definition or null if method is not a subscription
     */
    GraphQLFieldDefinition resolveSubscription(Method method);
    
    /**
     * Determines if a method should be treated as a GraphQL query.
     * 
     * @param method The method to check
     * @return true if the method is a query
     */
    boolean isQuery(Method method);
    
    /**
     * Determines if a method should be treated as a GraphQL mutation.
     * 
     * @param method The method to check
     * @return true if the method is a mutation
     */
    boolean isMutation(Method method);
    
    /**
     * Determines if a method should be treated as a GraphQL subscription.
     * 
     * @param method The method to check
     * @return true if the method is a subscription
     */
    boolean isSubscription(Method method);
    
    /**
     * Resolves all query operations from a controller class (alias for resolveQueries).
     * 
     * @param controllerClass The controller class to resolve queries from
     * @return List of GraphQL field definitions for queries
     */
    List<GraphQLFieldDefinition> resolveQueryOperations(Class<?> controllerClass);
    
    /**
     * Resolves all mutation operations from a controller class (alias for resolveMutations).
     * 
     * @param controllerClass The controller class to resolve mutations from
     * @return List of GraphQL field definitions for mutations
     */
    List<GraphQLFieldDefinition> resolveMutationOperations(Class<?> controllerClass);
    
    /**
     * Resolves all subscription operations from a controller class (alias for resolveSubscriptions).
     * 
     * @param controllerClass The controller class to resolve subscriptions from
     * @return List of GraphQL field definitions for subscriptions
     */
    List<GraphQLFieldDefinition> resolveSubscriptionOperations(Class<?> controllerClass);

    /**
     * Sets the validation exception handler.
     * @param validationExceptionHandler The validation exception handler to use.
     */
    void setValidationExceptionHandler(com.enokdev.graphql.autogen.validation.ValidationExceptionHandler validationExceptionHandler);
}

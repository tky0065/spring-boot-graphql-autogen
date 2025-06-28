package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import graphql.schema.*;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of OperationResolver for converting controller methods to GraphQL operations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
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
        log.debug("Resolving queries for controller: {}", controllerClass.getName());
        
        List<GraphQLFieldDefinition> queries = new ArrayList<>();
        
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isQueryOperation(method)) {
                GraphQLFieldDefinition query = resolveQuery(method);
                if (query != null) {
                    queries.add(query);
                }
            }
        }
        
        log.debug("Resolved {} queries for controller {}", queries.size(), controllerClass.getName());
        return queries;
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveMutations(Class<?> controllerClass) {
        log.debug("Resolving mutations for controller: {}", controllerClass.getName());
        
        List<GraphQLFieldDefinition> mutations = new ArrayList<>();
        
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isMutationOperation(method)) {
                GraphQLFieldDefinition mutation = resolveMutation(method);
                if (mutation != null) {
                    mutations.add(mutation);
                }
            }
        }
        
        log.debug("Resolved {} mutations for controller {}", mutations.size(), controllerClass.getName());
        return mutations;
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveSubscriptions(Class<?> controllerClass) {
        log.debug("Resolving subscriptions for controller: {}", controllerClass.getName());
        
        List<GraphQLFieldDefinition> subscriptions = new ArrayList<>();
        
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isSubscriptionOperation(method)) {
                GraphQLFieldDefinition subscription = resolveSubscription(method);
                if (subscription != null) {
                    subscriptions.add(subscription);
                }
            }
        }
        
        log.debug("Resolved {} subscriptions for controller {}", subscriptions.size(), controllerClass.getName());
        return subscriptions;
    }
    
    @Override
    public GraphQLFieldDefinition resolveQuery(Method method) {
        if (!isQueryOperation(method)) {
            return null;
        }
        
        try {
            String operationName = getOperationName(method, method.getAnnotation(GraphQLQuery.class));
            String description = getOperationDescription(method, method.getAnnotation(GraphQLQuery.class));
            GraphQLOutputType returnType = resolveReturnType(method);
            List<GraphQLArgument> arguments = resolveArguments(method);
            
            GraphQLFieldDefinition.Builder queryBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .name(operationName)
                .type(returnType)
                .arguments(arguments);
            
            if (!description.isEmpty()) {
                queryBuilder.description(description);
            }
            
            // Handle deprecation
            String deprecationReason = method.getAnnotation(GraphQLQuery.class).deprecationReason();
            if (!deprecationReason.isEmpty()) {
                queryBuilder.deprecate(deprecationReason);
            }
            
            log.debug("Resolved query: {} -> {}", method.getName(), operationName);
            return queryBuilder.build();
            
        } catch (Exception e) {
            log.error("Error resolving query method {}: {}", method.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public GraphQLFieldDefinition resolveMutation(Method method) {
        if (!isMutationOperation(method)) {
            return null;
        }
        
        try {
            String operationName = getOperationName(method, method.getAnnotation(GraphQLMutation.class));
            String description = getOperationDescription(method, method.getAnnotation(GraphQLMutation.class));
            GraphQLOutputType returnType = resolveReturnType(method);
            List<GraphQLArgument> arguments = resolveArguments(method);
            
            GraphQLFieldDefinition.Builder mutationBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .name(operationName)
                .type(returnType)
                .arguments(arguments);
            
            if (!description.isEmpty()) {
                mutationBuilder.description(description);
            }
            
            // Handle deprecation
            String deprecationReason = method.getAnnotation(GraphQLMutation.class).deprecationReason();
            if (!deprecationReason.isEmpty()) {
                mutationBuilder.deprecate(deprecationReason);
            }
            
            log.debug("Resolved mutation: {} -> {}", method.getName(), operationName);
            return mutationBuilder.build();
            
        } catch (Exception e) {
            log.error("Error resolving mutation method {}: {}", method.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public GraphQLFieldDefinition resolveSubscription(Method method) {
        if (!isSubscriptionOperation(method)) {
            return null;
        }
        
        try {
            String operationName = getOperationName(method, method.getAnnotation(GraphQLSubscription.class));
            String description = getOperationDescription(method, method.getAnnotation(GraphQLSubscription.class));
            GraphQLOutputType returnType = resolveSubscriptionReturnType(method);
            List<GraphQLArgument> arguments = resolveArguments(method);
            
            GraphQLFieldDefinition.Builder subscriptionBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .name(operationName)
                .type(returnType)
                .arguments(arguments);
            
            if (!description.isEmpty()) {
                subscriptionBuilder.description(description);
            }
            
            // Handle deprecation
            String deprecationReason = method.getAnnotation(GraphQLSubscription.class).deprecationReason();
            if (!deprecationReason.isEmpty()) {
                subscriptionBuilder.deprecate(deprecationReason);
            }
            
            log.debug("Resolved subscription: {} -> {}", method.getName(), operationName);
            return subscriptionBuilder.build();
            
        } catch (Exception e) {
            log.error("Error resolving subscription method {}: {}", method.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean isQueryOperation(Method method) {
        return method.isAnnotationPresent(GraphQLQuery.class) && 
               method.getAnnotation(GraphQLQuery.class).enabled();
    }
    
    @Override
    public boolean isMutationOperation(Method method) {
        return method.isAnnotationPresent(GraphQLMutation.class) && 
               method.getAnnotation(GraphQLMutation.class).enabled();
    }
    
    @Override
    public boolean isSubscriptionOperation(Method method) {
        return method.isAnnotationPresent(GraphQLSubscription.class) && 
               method.getAnnotation(GraphQLSubscription.class).enabled();
    }
    
    /**
     * Gets the operation name, using annotation name or method name as fallback.
     */
    private String getOperationName(Method method, Object annotation) {
        String annotationName = "";
        
        if (annotation instanceof GraphQLQuery query) {
            annotationName = query.name();
        } else if (annotation instanceof GraphQLMutation mutation) {
            annotationName = mutation.name();
        } else if (annotation instanceof GraphQLSubscription subscription) {
            annotationName = subscription.name();
        }
        
        if (!annotationName.isEmpty()) {
            return annotationName;
        }
        
        // Apply controller prefix if present
        String prefix = getControllerPrefix(method.getDeclaringClass());
        String methodName = method.getName();
        
        return prefix.isEmpty() ? methodName : prefix + capitalize(methodName);
    }
    
    /**
     * Gets the operation description from annotation.
     */
    private String getOperationDescription(Method method, Object annotation) {
        if (annotation instanceof GraphQLQuery query) {
            return query.description();
        } else if (annotation instanceof GraphQLMutation mutation) {
            return mutation.description();
        } else if (annotation instanceof GraphQLSubscription subscription) {
            return subscription.description();
        }
        
        return "";
    }
    
    /**
     * Gets the controller prefix if @GraphQLController is present.
     */
    private String getControllerPrefix(Class<?> controllerClass) {
        if (controllerClass.isAnnotationPresent(GraphQLController.class)) {
            return controllerClass.getAnnotation(GraphQLController.class).prefix();
        }
        return "";
    }
    
    /**
     * Resolves the return type for queries and mutations.
     */
    private GraphQLOutputType resolveReturnType(Method method) {
        Type returnType = method.getGenericReturnType();
        GraphQLType graphqlType = typeResolver.resolveType(returnType);
        
        // Ensure it's an output type
        if (graphqlType instanceof GraphQLOutputType) {
            return (GraphQLOutputType) graphqlType;
        }
        
        throw new IllegalArgumentException("Method " + method.getName() + " return type cannot be used as GraphQL output type: " + returnType);
    }
    
    /**
     * Resolves the return type for subscriptions (handles reactive types).
     */
    private GraphQLOutputType resolveSubscriptionReturnType(Method method) {
        Type returnType = method.getGenericReturnType();
        
        // Handle reactive types like Publisher<T>, Flux<T>, etc.
        if (isReactiveType(returnType)) {
            Type elementType = extractElementType(returnType);
            if (elementType != null) {
                GraphQLType graphqlType = typeResolver.resolveType(elementType);
                if (graphqlType instanceof GraphQLOutputType) {
                    return (GraphQLOutputType) graphqlType;
                }
            }
        }
        
        // Fallback to regular return type resolution
        return resolveReturnType(method);
    }
    
    /**
     * Resolves arguments for a method.
     */
    private List<GraphQLArgument> resolveArguments(Method method) {
        List<GraphQLArgument> arguments = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        
        for (Parameter parameter : parameters) {
            // Skip parameters that should not be GraphQL arguments
            if (shouldSkipParameter(parameter)) {
                continue;
            }
            
            GraphQLArgument argument = resolveArgument(parameter);
            if (argument != null) {
                arguments.add(argument);
            }
        }
        
        return arguments;
    }
    
    /**
     * Resolves a single method parameter to a GraphQL argument.
     */
    private GraphQLArgument resolveArgument(Parameter parameter) {
        try {
            String argumentName = getArgumentName(parameter);
            String description = getArgumentDescription(parameter);
            boolean required = getArgumentRequired(parameter);
            String defaultValue = getArgumentDefaultValue(parameter);
            
            GraphQLInputType argumentType = resolveArgumentType(parameter);
            
            // Apply non-null wrapper if required
            if (required && !(argumentType instanceof GraphQLNonNull)) {
                argumentType = GraphQLNonNull.nonNull(argumentType);
            }
            
            GraphQLArgument.Builder argumentBuilder = GraphQLArgument.newArgument()
                .name(argumentName)
                .type(argumentType);
            
            if (!description.isEmpty()) {
                argumentBuilder.description(description);
            }
            
            if (!defaultValue.isEmpty()) {
                argumentBuilder.defaultValue(parseDefaultValue(defaultValue, parameter.getType()));
            }
            
            return argumentBuilder.build();
            
        } catch (Exception e) {
            log.error("Error resolving argument {}: {}", parameter.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Gets the argument name from @GraphQLArgument or parameter name.
     */
    private String getArgumentName(Parameter parameter) {
        if (parameter.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class)) {
            String name = parameter.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class).name();
        }

        return parameter.getName();
    }

    /**
     * Gets the argument description from @GraphQLArgument.
     */
    private String getArgumentDescription(Parameter parameter) {
        if (parameter.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class)) {
            return parameter.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class).description();
        }

        return "";
    }
    
    /**
     * Checks if an argument is required.
     */
    private boolean getArgumentRequired(Parameter parameter) {
        if (parameter.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class)) {
            return parameter.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class).required();
        }
        
        // Default: required if not Optional and not primitive wrapper
        return !isOptionalType(parameter.getType());
    }
    
    /**
     * Gets the default value for an argument.
     */
    private String getArgumentDefaultValue(Parameter parameter) {
        if (parameter.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class)) {
            return parameter.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLArgument.class).defaultValue();
        }
        
        return "";
    }
    
    /**
     * Resolves the GraphQL input type for a parameter.
     */
    private GraphQLInputType resolveArgumentType(Parameter parameter) {
        GraphQLType graphqlType = typeResolver.resolveType(parameter.getParameterizedType());
        
        if (graphqlType instanceof GraphQLInputType) {
            return (GraphQLInputType) graphqlType;
        }
        
        throw new IllegalArgumentException("Parameter " + parameter.getName() + " type cannot be used as GraphQL input type: " + parameter.getType());
    }
    
    /**
     * Checks if a parameter should be skipped (e.g., Spring MVC parameters).
     */
    private boolean shouldSkipParameter(Parameter parameter) {
        // Skip common Spring MVC parameters
        String typeName = parameter.getType().getName();
        return typeName.startsWith("org.springframework.web") ||
               typeName.startsWith("jakarta.servlet") ||
               typeName.equals("org.springframework.ui.Model") ||
               typeName.equals("org.springframework.validation.BindingResult");
    }
    
    /**
     * Checks if a type is reactive (Publisher, Flux, etc.).
     */
    private boolean isReactiveType(Type type) {
        if (type instanceof Class<?> clazz) {
            String className = clazz.getName();
            return className.startsWith("org.reactivestreams.Publisher") ||
                   className.startsWith("reactor.core.publisher") ||
                   className.startsWith("io.reactivex");
        }
        return false;
    }
    
    /**
     * Extracts element type from reactive types.
     */
    private Type extractElementType(Type reactiveType) {
        // Simplified implementation - would need more sophisticated generic type extraction
        return String.class; // Fallback
    }
    
    /**
     * Checks if a type is optional (Optional, nullable wrapper, etc.).
     */
    private boolean isOptionalType(Class<?> type) {
        return type.getName().equals("java.util.Optional") ||
               !type.isPrimitive(); // Non-primitives are generally optional
    }
    
    /**
     * Parses a default value string to appropriate Java object.
     */
    private Object parseDefaultValue(String defaultValue, Class<?> parameterType) {
        if (defaultValue.isEmpty()) {
            return null;
        }
        
        // Simple parsing - could be enhanced
        try {
            if (parameterType == String.class) {
                return defaultValue;
            } else if (parameterType == Integer.class || parameterType == int.class) {
                return Integer.parseInt(defaultValue);
            } else if (parameterType == Long.class || parameterType == long.class) {
                return Long.parseLong(defaultValue);
            } else if (parameterType == Boolean.class || parameterType == boolean.class) {
                return Boolean.parseBoolean(defaultValue);
            }
        } catch (NumberFormatException e) {
            log.warn("Cannot parse default value '{}' for type {}", defaultValue, parameterType.getName());
        }
        
        return defaultValue;
    }
    
    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

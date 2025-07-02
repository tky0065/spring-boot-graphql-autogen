package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.generator.PaginationGenerator.PaginationConfiguration;
import graphql.Scalars;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.enokdev.graphql.autogen.validation.ValidationExceptionHandler;

/**
 * Default implementation of OperationResolver for converting controller methods to GraphQL operations.
 */
@Component
public class DefaultOperationResolver implements OperationResolver {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultOperationResolver.class);
    private final TypeResolver typeResolver;
    private final PaginationGenerator paginationGenerator;
    private ValidationExceptionHandler validationExceptionHandler;
    
    public DefaultOperationResolver(TypeResolver typeResolver, PaginationGenerator paginationGenerator) {
        this.typeResolver = typeResolver;
        this.paginationGenerator = paginationGenerator;
    }
    
    @Override
    public void setValidationExceptionHandler(ValidationExceptionHandler validationExceptionHandler) {
        this.validationExceptionHandler = validationExceptionHandler;
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
        
        GraphQLFieldDefinition.Builder fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .name(fieldName)
            .type(returnType);

        // Add security directives
        GraphQLQuery queryAnnotation = method.getAnnotation(GraphQLQuery.class);
        if (queryAnnotation != null) {
            if (queryAnnotation.roles().length > 0 || queryAnnotation.permissions().length > 0) {
                fieldBuilder.withDirective(createAuthDirective(queryAnnotation.roles(), queryAnnotation.permissions()));
            }
        }

        // Add arguments for pagination
        if (method.isAnnotationPresent(GraphQLPagination.class)) {
            PaginationConfiguration paginationConfig = paginationGenerator.generatePaginationConfiguration(method);
            if (paginationConfig != null) {
                fieldBuilder.arguments(paginationGenerator.generatePaginationArguments(paginationConfig));
                // If Relay pagination, wrap the return type in a Connection type
                if (paginationConfig.getType() == GraphQLPagination.PaginationType.RELAY_CURSOR) {
                    fieldBuilder.type(paginationGenerator.generateConnectionType(paginationConfig));
                }
            }
        }
        
        return fieldBuilder.build();
    }
    
    @Override
    public GraphQLFieldDefinition resolveMutation(Method method) {
        if (!isMutation(method)) {
            return null;
        }

        String fieldName = getOperationName(method);
        GraphQLMutation mutationAnnotation = method.getAnnotation(GraphQLMutation.class);
        GraphQLOutputType returnType;

        if (mutationAnnotation.payloadType() != void.class) {
            returnType = (GraphQLOutputType) typeResolver.resolveType(mutationAnnotation.payloadType());
        } else {
            // Generate a default payload type
            String payloadTypeName = fieldName + "Payload";
            GraphQLOutputType actualReturnType = resolveReturnType(method);

            GraphQLObjectType.Builder payloadBuilder = GraphQLObjectType.newObject()
                .name(payloadTypeName)
                .description("Payload for " + fieldName + " mutation");

            if (actualReturnType != null) {
                payloadBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                    .name(fieldName) // Use mutation name as field name in payload
                    .type(actualReturnType)
                    .build());
            }

            // Add errors field
            payloadBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                .name("errors")
                .type(GraphQLList.list(GraphQLTypeReference.typeRef("ValidationError")))
                .build());

            returnType = payloadBuilder.build();
        }

        if (mutationAnnotation.batch()) {
            returnType = GraphQLList.list(returnType);
        }

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
        
        GraphQLFieldDefinition.Builder fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .name(fieldName)
            .type(returnType);

        // Add arguments for subscription filters
        for (java.lang.reflect.Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GraphQLSubscriptionFilter.class)) {
                GraphQLSubscriptionFilter filterAnnotation = parameter.getAnnotation(GraphQLSubscriptionFilter.class);
                String argName = filterAnnotation.name().isEmpty() ? parameter.getName() : filterAnnotation.name();
                GraphQLInputType argType = (GraphQLInputType) typeResolver.resolveType(parameter.getParameterizedType());

                if (!filterAnnotation.optional()) {
                    argType = GraphQLNonNull.nonNull(argType);
                }

                fieldBuilder.argument(GraphQLArgument.newArgument()
                    .name(argName)
                    .type(argType)
                    .description(filterAnnotation.description())
                    .build());
            }
        }

        // Add security directives
        GraphQLSubscription subscriptionAnnotation = method.getAnnotation(GraphQLSubscription.class);
        if (subscriptionAnnotation != null) {
            if (subscriptionAnnotation.roles().length > 0 || subscriptionAnnotation.permissions().length > 0) {
                fieldBuilder.withDirective(createAuthDirective(subscriptionAnnotation.roles(), subscriptionAnnotation.permissions()));
            }
        }
        
        return fieldBuilder.build();
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

        Type returnType = method.getGenericReturnType();

        // If the return type is a reactive type (e.g., Flux, Mono), extract the actual type argument
        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class && (rawType == reactor.core.publisher.Flux.class || rawType == reactor.core.publisher.Mono.class)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    returnType = actualTypeArguments[0];
                }
            }
        }

        graphql.schema.GraphQLType type = typeResolver.resolveType(returnType);
        return (GraphQLOutputType) type;
    }

    private GraphQLDirective createAuthDirective(String[] roles, String[] permissions) {
        GraphQLDirective.Builder directiveBuilder = GraphQLDirective.newDirective()
            .name("auth")
            .description("Directive for authorization");

        if (roles.length > 0) {
            directiveBuilder.argument(GraphQLArgument.newArgument()
                .name("roles")
                .type(GraphQLList.list(Scalars.GraphQLString))
                .value(List.of(roles))
                .build());
        }

        if (permissions.length > 0) {
            directiveBuilder.argument(GraphQLArgument.newArgument()
                .name("permissions")
                .type(GraphQLList.list(Scalars.GraphQLString))
                .value(List.of(permissions))
                .build());
        }

        return directiveBuilder.build();
    }
}

package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import graphql.schema.*;
// Pas d'import pour GraphQLArgument et GraphQLType pour éviter les conflits
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of FieldResolver for converting Java fields/methods to GraphQL fields.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultFieldResolver implements FieldResolver {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultFieldResolver.class);
    
    private final TypeResolver typeResolver;
    
    public DefaultFieldResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }
    
    @Override
    public List<GraphQLFieldDefinition> resolveFields(Class<?> clazz) {
        log.debug("Resolving fields for class: {}", clazz.getName());
        
        List<GraphQLFieldDefinition> fields = new ArrayList<>();
        
        // Process declared fields
        for (Field field : clazz.getDeclaredFields()) {
            if (shouldIncludeField(field)) {
                GraphQLFieldDefinition fieldDef = resolveField(field);
                if (fieldDef != null) {
                    fields.add(fieldDef);
                }
            }
        }
        
        // Process methods (getters and @GraphQLField annotated methods)
        for (Method method : clazz.getDeclaredMethods()) {
            if (shouldIncludeMethod(method)) {
                GraphQLFieldDefinition fieldDef = resolveMethod(method);
                if (fieldDef != null) {
                    fields.add(fieldDef);
                }
            }
        }
        
        log.debug("Resolved {} fields for class {}", fields.size(), clazz.getName());
        return fields;
    }
    
    @Override
    public GraphQLFieldDefinition resolveField(Field field) {
        if (!shouldIncludeField(field)) {
            return null;
        }
        
        try {
            String fieldName = getFieldName(field);
            String description = getFieldDescription(field);
            GraphQLOutputType fieldType = resolveFieldType(field.getGenericType(), field);
            
            GraphQLFieldDefinition.Builder fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .name(fieldName)
                .type(fieldType);
            
            if (!description.isEmpty()) {
                fieldBuilder.description(description);
            }
            
            // Handle deprecation
            String deprecationReason = getDeprecationReason(field);
            if (!deprecationReason.isEmpty()) {
                fieldBuilder.deprecate(deprecationReason);
            }
            
            log.debug("Resolved field: {} -> {}", field.getName(), fieldName);
            return fieldBuilder.build();
            
        } catch (Exception e) {
            log.error("Error resolving field {}: {}", field.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public GraphQLFieldDefinition resolveMethod(Method method) {
        if (!shouldIncludeMethod(method)) {
            return null;
        }
        
        try {
            String fieldName = getMethodFieldName(method);
            String description = getMethodDescription(method);
            GraphQLOutputType fieldType = resolveFieldType(method.getGenericReturnType(), method);
            
            GraphQLFieldDefinition.Builder fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .name(fieldName)
                .type(fieldType);
            
            if (!description.isEmpty()) {
                fieldBuilder.description(description);
            }
            
            // Handle deprecation
            String deprecationReason = getMethodDeprecationReason(method);
            if (!deprecationReason.isEmpty()) {
                fieldBuilder.deprecate(deprecationReason);
            }
            
            // Add arguments if method has parameters
            List<graphql.schema.GraphQLArgument> arguments = resolveMethodArguments(method);
            if (!arguments.isEmpty()) {
                fieldBuilder.arguments(arguments);
            }
            
            log.debug("Resolved method: {} -> {}", method.getName(), fieldName);
            return fieldBuilder.build();
            
        } catch (Exception e) {
            log.error("Error resolving method {}: {}", method.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean shouldIncludeField(Field field) {
        // Skip if marked with @GraphQLIgnore
        if (field.isAnnotationPresent(GraphQLIgnore.class)) {
            return false;
        }
        
        // Skip static fields
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        
        // Include if explicitly marked with GraphQL annotations
        if (field.isAnnotationPresent(GraphQLField.class) || 
            field.isAnnotationPresent(GraphQLId.class)) {
            return true;
        }
        
        // Include if class is annotated with @AutoGenType and field is not synthetic
        if (field.getDeclaringClass().isAnnotationPresent(GType.class)) {
            return !field.isSynthetic();
        }
        
        // Include if class is annotated with @GraphQLInterface and field is not synthetic
        if (field.getDeclaringClass().isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class)) {
            return !field.isSynthetic();
        }
        
        // Include if class is annotated with @GraphQLInput and field is not synthetic
        if (field.getDeclaringClass().isAnnotationPresent(GraphQLInput.class)) {
            return !field.isSynthetic();
        }
        
        return false;
    }
    
    @Override
    public boolean shouldIncludeMethod(Method method) {
        // Skip if marked with @GraphQLIgnore
        if (method.isAnnotationPresent(GraphQLIgnore.class)) {
            return false;
        }
        
        // Skip static methods
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        
        // Include if explicitly marked with @GraphQLField
        if (method.isAnnotationPresent(GraphQLField.class)) {
            return true;
        }
        
        // Include getter methods for @AutoGenType classes
        if (method.getDeclaringClass().isAnnotationPresent(GType.class)) {
            return isGetterMethod(method);
        }
        
        // Include getter methods for @GraphQLInterface classes
        if (method.getDeclaringClass().isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class)) {
            return isGetterMethod(method);
        }
        
        return false;
    }
    
    /**
     * Resolves the GraphQL type for a field or method return type.
     */
    private GraphQLOutputType resolveFieldType(Type javaType, Object context) {
        graphql.schema.GraphQLType graphqlType = typeResolver.resolveType(javaType);
        
        // Handle nullability
        boolean isNullable = true;
        
        if (context instanceof Field field) {
            if (field.isAnnotationPresent(GraphQLField.class)) {
                isNullable = field.getAnnotation(GraphQLField.class).nullable();
            } else if (field.isAnnotationPresent(GraphQLId.class)) {
                isNullable = false; // IDs are typically non-null
            }
        }
        
        // Convert to output type
        GraphQLOutputType outputType = (GraphQLOutputType) graphqlType;
        
        // Apply non-null wrapper if needed
        if (!isNullable) {
            outputType = GraphQLNonNull.nonNull(outputType);
        }
        
        return outputType;
    }
    
    /**
     * Gets the GraphQL field name for a Java field.
     */
    private String getFieldName(Field field) {
        if (field.isAnnotationPresent(GraphQLField.class)) {
            String name = field.getAnnotation(GraphQLField.class).name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        
        if (field.isAnnotationPresent(GraphQLId.class)) {
            String name = field.getAnnotation(GraphQLId.class).name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        
        return field.getName();
    }
    
    /**
     * Gets the description for a field.
     */
    private String getFieldDescription(Field field) {
        String annotationDescription = "";
        
        if (field.isAnnotationPresent(GraphQLField.class)) {
            annotationDescription = field.getAnnotation(GraphQLField.class).description();
        } else if (field.isAnnotationPresent(GraphQLId.class)) {
            annotationDescription = field.getAnnotation(GraphQLId.class).description();
        }
        
        // Use JavaDoc extractor with fallback to annotation
        return JavaDocExtractor.extractDescriptionWithFallback(field, annotationDescription);
    }
    
    /**
     * Gets the deprecation reason for a field.
     */
    private String getDeprecationReason(Field field) {
        if (field.isAnnotationPresent(GraphQLField.class)) {
            return field.getAnnotation(GraphQLField.class).deprecationReason();
        }
        
        return "";
    }
    
    /**
     * Gets the GraphQL field name for a method.
     */
    private String getMethodFieldName(Method method) {
        if (method.isAnnotationPresent(GraphQLField.class)) {
            String name = method.getAnnotation(GraphQLField.class).name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        
        // Convert getter method name to field name
        String methodName = method.getName();
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        
        return methodName;
    }
    
    /**
     * Gets the description for a method.
     */
    private String getMethodDescription(Method method) {
        String annotationDescription = "";
        
        if (method.isAnnotationPresent(GraphQLField.class)) {
            annotationDescription = method.getAnnotation(GraphQLField.class).description();
        }
        
        // Use JavaDoc extractor with fallback to annotation
        return JavaDocExtractor.extractDescriptionWithFallback(method, annotationDescription);
    }
    
    /**
     * Gets the deprecation reason for a method.
     */
    private String getMethodDeprecationReason(Method method) {
        if (method.isAnnotationPresent(GraphQLField.class)) {
            return method.getAnnotation(GraphQLField.class).deprecationReason();
        }
        
        return "";
    }
    
    /**
     * Resolves arguments for a method.
     */
    private List<graphql.schema.GraphQLArgument> resolveMethodArguments(Method method) {
        List<graphql.schema.GraphQLArgument> arguments = new ArrayList<>();
        
        // For now, return empty list - method arguments will be handled
        // when we implement operation resolvers for queries/mutations
        
        return arguments;
    }
    
    /**
     * Checks if a method is a getter method.
     */
    private boolean isGetterMethod(Method method) {
        String methodName = method.getName();
        
        // Must have no parameters
        if (method.getParameterCount() != 0) {
            return false;
        }
        
        // Must have a return type
        if (method.getReturnType() == void.class) {
            return false;
        }
        
        // Must start with "get" or "is"
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.isUpperCase(methodName.charAt(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.isUpperCase(methodName.charAt(2)) && 
                   (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class);
        }
        
        return false;
    }
}

package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.exception.TypeResolutionException;
import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.Coercing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultTypeResolver implements TypeResolver {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultTypeResolver.class);
    private final Map<Class<?>, graphql.schema.GraphQLType> typeCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> customTypeMappings = new ConcurrentHashMap<>();
    private static final Map<Class<?>, GraphQLScalarType> SCALAR_MAPPINGS;

    static {
        Map<Class<?>, GraphQLScalarType> scalarMap = new HashMap<>();
        scalarMap.put(String.class, Scalars.GraphQLString);
        scalarMap.put(Integer.class, Scalars.GraphQLInt);
        scalarMap.put(int.class, Scalars.GraphQLInt);
        scalarMap.put(Long.class, Scalars.GraphQLInt);
        scalarMap.put(long.class, Scalars.GraphQLInt);
        scalarMap.put(Float.class, Scalars.GraphQLFloat);
        scalarMap.put(float.class, Scalars.GraphQLFloat);
        scalarMap.put(Double.class, Scalars.GraphQLFloat);
        scalarMap.put(double.class, Scalars.GraphQLFloat);
        scalarMap.put(Boolean.class, Scalars.GraphQLBoolean);
        scalarMap.put(boolean.class, Scalars.GraphQLBoolean);
        scalarMap.put(BigInteger.class, Scalars.GraphQLID);
        scalarMap.put(BigDecimal.class, Scalars.GraphQLFloat);
        SCALAR_MAPPINGS = Collections.unmodifiableMap(scalarMap);
    }

    public DefaultTypeResolver() {
        registerTypeMapping(LocalDateTime.class, "DateTime");
        registerTypeMapping(LocalDate.class, "Date");
        registerTypeMapping(LocalTime.class, "Time");
    }

    @Override
    public graphql.schema.GraphQLType resolveType(Class<?> javaType) {
        // Check cache first
        if (typeCache.containsKey(javaType)) {
            return typeCache.get(javaType);
        }

        GraphQLType resolvedType;

        // Check if it's a scalar type
        if (SCALAR_MAPPINGS.containsKey(javaType)) {
            return SCALAR_MAPPINGS.get(javaType);
        }

        // Check if it's an enum
        if (isEnumType(javaType)) {
            resolvedType = createEnumType(javaType);
        }
        // Check if it's an interface
        else if (isInterfaceType(javaType)) {
            resolvedType = createInterfaceType(javaType);
        }
        // Check if it's a union
        else if (isUnionType(javaType)) {
            resolvedType = createUnionType(javaType);
        }
        // Check if it's an input type
        else if (isInputType(javaType)) {
            resolvedType = createInputObjectType(javaType);
        }
        // Default to object type
        else if (isObjectType(javaType)) {
            resolvedType = createObjectType(javaType);
        }
        // Special handling for collections
        else if (Collection.class.isAssignableFrom(javaType)) {
            resolvedType = GraphQLList.list(Scalars.GraphQLString); // Default list of strings
        }
        // Special handling for maps
        else if (Map.class.isAssignableFrom(javaType)) {
            resolvedType = Scalars.GraphQLString; // Maps default to JSON strings
        }
        else {
            throw new TypeResolutionException("Cannot resolve Java type to GraphQL type: " + javaType.getName());
        }

        // Cache the resolved type
        typeCache.put(javaType, resolvedType);
        return resolvedType;
    }
    
    @Override
    public graphql.schema.GraphQLType resolveType(Type javaType) {
        if (javaType instanceof Class<?>) {
            return resolveType((Class<?>) javaType);
        } else if (javaType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) javaType;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?>) {
                Class<?> rawClass = (Class<?>) rawType;

                // Handle collections
                if (Collection.class.isAssignableFrom(rawClass)) {
                    Type itemType = parameterizedType.getActualTypeArguments()[0];
                    if (itemType instanceof Class<?>) {
                        return GraphQLList.list(resolveOutputType((Class<?>) itemType));
                    }
                }

                // Handle maps (convert to JSON string)
                if (Map.class.isAssignableFrom(rawClass)) {
                    return Scalars.GraphQLString;
                }

                // Handle optionals
                if (Optional.class.isAssignableFrom(rawClass)) {
                    Type innerType = parameterizedType.getActualTypeArguments()[0];
                    if (innerType instanceof Class<?>) {
                        return resolveType((Class<?>) innerType);
                    }
                }
            }

            throw new TypeResolutionException("Cannot resolve parameterized type: " + javaType);
        }
        
        throw new TypeResolutionException("Unsupported Java type: " + javaType);
    }
    
    @Override
    public boolean canResolve(Class<?> javaType) {
        // Check if it's in cache
        if (typeCache.containsKey(javaType)) {
            return true;
        }

        // Check if it's a scalar type
        if (SCALAR_MAPPINGS.containsKey(javaType)) {
            return true;
        }

        // Check if it's an annotated type
        if (isObjectType(javaType) || isInputType(javaType) || isEnumType(javaType) ||
            isInterfaceType(javaType) || isUnionType(javaType)) {
            return true;
        }

        // Check for collection types
        if (Collection.class.isAssignableFrom(javaType)) {
            return true;
        }

        // Check for map types
        if (Map.class.isAssignableFrom(javaType)) {
            return true;
        }

        return false;
    }
    
    @Override
    public boolean canResolve(Type javaType) {
        if (javaType instanceof Class<?>) {
            return canResolve((Class<?>) javaType);
        } else if (javaType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) javaType;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?>) {
                Class<?> rawClass = (Class<?>) rawType;

                // Handle collections, maps, optionals
                return Collection.class.isAssignableFrom(rawClass) ||
                       Map.class.isAssignableFrom(rawClass) ||
                       Optional.class.isAssignableFrom(rawClass);
            }
        }
        
        return false;
    }
    
    @Override
    public void registerTypeMapping(Class<?> javaType, String graphQLTypeName) {
        log.debug("Registering type mapping: {} -> {}", javaType.getName(), graphQLTypeName);
        customTypeMappings.put(javaType, graphQLTypeName);
    }

    @Override
    public String getGraphQLTypeName(Class<?> javaType) {
        // Check custom mappings first
        if (customTypeMappings.containsKey(javaType)) {
            return customTypeMappings.get(javaType);
        }
        
        // Check for class-level annotations
        if (javaType.isAnnotationPresent(GType.class)) {
            GType annotation = javaType.getAnnotation(GType.class);
            String name = annotation.value();
            if (!name.isEmpty()) {
                return name;
            }
        }
        
        if (javaType.isAnnotationPresent(GraphQLInterface.class)) {
            GraphQLInterface annotation = javaType.getAnnotation(GraphQLInterface.class);
            String name = annotation.value();
            if (!name.isEmpty()) {
                return name;
            }
        }
        
        if (javaType.isAnnotationPresent(GraphQLUnion.class)) {
            GraphQLUnion annotation = javaType.getAnnotation(GraphQLUnion.class);
            String name = annotation.value();
            if (!name.isEmpty()) {
                return name;
            }
        }

        if (javaType.isAnnotationPresent(GraphQLEnum.class)) {
            GraphQLEnum annotation = javaType.getAnnotation(GraphQLEnum.class);
            String name = annotation.value();
            if (!name.isEmpty()) {
                return name;
            }
        }
        
        // Default to class simple name
        return javaType.getSimpleName();
    }

    @Override
    public String getGraphQLInputTypeName(Class<?> javaType) {
        // Check for input type annotation
        if (javaType.isAnnotationPresent(GraphQLInput.class)) {
            GraphQLInput annotation = javaType.getAnnotation(GraphQLInput.class);
            String name = annotation.value();
            if (!name.isEmpty()) {
                return name;
            }
            return javaType.getSimpleName();
        }

        // For object types that need input types, append "Input"
        return getGraphQLTypeName(javaType) + "Input";
    }

    @Override
    public GraphQLOutputType resolveOutputType(Class<?> javaType) {
        GraphQLType type = resolveType(javaType);
        if (type instanceof GraphQLOutputType) {
            return (GraphQLOutputType) type;
        }
        throw new TypeResolutionException("Type " + javaType.getName() + " cannot be used as an output type");
    }

    @Override
    public GraphQLInputType resolveInputType(Class<?> javaType) {
        GraphQLType type = resolveType(javaType);
        if (type instanceof GraphQLInputType) {
            return (GraphQLInputType) type;
        }
        throw new TypeResolutionException("Type " + javaType.getName() + " cannot be used as an input type");
    }
    
    // Helper methods for creating different GraphQL types

    private GraphQLEnumType createEnumType(Class<?> javaEnum) {
        String typeName = getGraphQLTypeName(javaEnum);
        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum().name(typeName);

        // Add enum values
        for (Object enumConstant : javaEnum.getEnumConstants()) {
            String enumName = ((Enum<?>) enumConstant).name();
            builder.value(enumName);
        }

        return builder.build();
    }

    private GraphQLInterfaceType createInterfaceType(Class<?> javaInterface) {
        String typeName = getGraphQLTypeName(javaInterface);
        return GraphQLInterfaceType.newInterface()
            .name(typeName)
            .description("Interface generated from " + javaInterface.getName())
            .build();
    }
    
    private GraphQLUnionType createUnionType(Class<?> javaUnion) {
        String typeName = getGraphQLTypeName(javaUnion);
        return GraphQLUnionType.newUnionType()
            .name(typeName)
            .description("Union type generated from " + javaUnion.getName())
            .build();
    }
    
    private GraphQLInputObjectType createInputObjectType(Class<?> javaClass) {
        String typeName = getGraphQLInputTypeName(javaClass);
        return GraphQLInputObjectType.newInputObject()
            .name(typeName)
            .description("Input type generated from " + javaClass.getName())
            .build();
    }

    private GraphQLObjectType createObjectType(Class<?> javaClass) {
        String typeName = getGraphQLTypeName(javaClass);
        return GraphQLObjectType.newObject()
            .name(typeName)
            .description("Type generated from " + javaClass.getName())
            .build();
    }
}

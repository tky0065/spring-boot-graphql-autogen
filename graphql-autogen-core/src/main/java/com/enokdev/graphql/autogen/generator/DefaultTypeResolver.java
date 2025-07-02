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
        registerTypeMapping(UUID.class, "ID");
    }
    
    public DefaultTypeResolver(com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig config) {
        this(); // Call default constructor for basic setup
        // Additional configuration can be applied here if needed
    }

    @Override
    public graphql.schema.GraphQLType resolveType(Class<?> javaType) {
        if (javaType == null) {
            throw new TypeResolutionException("Java type cannot be null", null);
        }

        graphql.schema.GraphQLType cachedType = typeCache.get(javaType);
        if (cachedType != null) {
            return cachedType;
        }

        graphql.schema.GraphQLType resolvedType = doResolveType(javaType);
        if (resolvedType != null) {
            typeCache.put(javaType, resolvedType);
        }

        return resolvedType;
    }
    
    @Override
    public graphql.schema.GraphQLType resolveType(Type javaType) {
        if (javaType instanceof Class<?>) {
            return resolveType((Class<?>) javaType);
        }
        
        if (javaType instanceof ParameterizedType parameterizedType) {
            return resolveParameterizedType(parameterizedType);
        }
        
        throw new TypeResolutionException("Unsupported Java type: " + javaType, javaType.getClass());
    }
    
    @Override
    public boolean canResolve(Class<?> javaType) {
        if (javaType == null) {
            return false;
        }
        
        try {
            return doResolveType(javaType) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean canResolve(Type javaType) {
        if (javaType instanceof Class<?>) {
            return canResolve((Class<?>) javaType);
        }
        
        if (javaType instanceof ParameterizedType) {
            try {
                resolveParameterizedType((ParameterizedType) javaType);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        return false;
    }
    
    @Override
    public void registerTypeMapping(Class<?> javaType, String graphqlTypeName) {
        customTypeMappings.put(javaType, graphqlTypeName);
    }
    
    private graphql.schema.GraphQLType doResolveType(Class<?> javaType) {
        // Handle built-in scalar types
        GraphQLScalarType scalarType = SCALAR_MAPPINGS.get(javaType);
        if (scalarType != null) {
            return scalarType;
        }
        
        // Handle custom scalar mappings
        String customTypeName = customTypeMappings.get(javaType);
        if (customTypeName != null) {
            return createCustomScalar(customTypeName, javaType);
        }
        
        // Handle @GraphQLId annotation
        if (hasGraphQLIdAnnotation(javaType)) {
            return Scalars.GraphQLID;
        }
        
        // Handle arrays
        if (javaType.isArray()) {
            Class<?> componentType = javaType.getComponentType();
            graphql.schema.GraphQLType elementType = resolveType(componentType);
            return GraphQLList.list(elementType);
        }
        
        // Handle Enums with @GraphQLEnum
        if (javaType.isEnum() && javaType.isAnnotationPresent(GraphQLEnum.class)) {
            return createGraphQLEnum(javaType);
        }
        
        // Handle Collections (List, Set, etc.)
        if (Collection.class.isAssignableFrom(javaType)) {
            return GraphQLList.list(Scalars.GraphQLString);
        }
        
        // Handle Optional
        if (Optional.class.isAssignableFrom(javaType)) {
            return Scalars.GraphQLString;
        }
        
        // Handle @GraphQLInterface annotated classes/interfaces
        if (javaType.isAnnotationPresent(GraphQLInterface.class)) {
            return createGraphQLInterfaceType(javaType);
        }
        
        // Handle @GraphQLUnion annotated classes
        if (javaType.isAnnotationPresent(GraphQLUnion.class)) {
            return createGraphQLUnionType(javaType);
        }
        
        // Handle @AutoGenType annotated classes
        if (javaType.isAnnotationPresent(GType.class)) {
            return createGraphQLObjectType(javaType);
        }
        
        // Handle @GraphQLInput annotated classes
        if (javaType.isAnnotationPresent(GraphQLInput.class)) {
            return createGraphQLInputType(javaType);
        }
        
        throw new TypeResolutionException(STR."Cannot resolve Java type to GraphQL type: \{javaType.getName()}", javaType);
    }
    
    private graphql.schema.GraphQLType resolveParameterizedType(ParameterizedType parameterizedType) {
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        
        if (Collection.class.isAssignableFrom(rawType) && typeArguments.length > 0) {
            graphql.schema.GraphQLType elementType = resolveType(typeArguments[0]);
            return GraphQLList.list(elementType);
        }
        
        if (Optional.class.isAssignableFrom(rawType) && typeArguments.length > 0) {
            return resolveType(typeArguments[0]);
        }
        
        return resolveType(rawType);
    }
    
    private GraphQLScalarType createCustomScalar(String typeName, Class<?> javaType) {
        return GraphQLScalarType.newScalar()
            .name(typeName)
            .description("Custom scalar type for " + javaType.getSimpleName())
            .coercing(new CustomScalarCoercing(javaType))
            .build();
    }
    
    private GraphQLEnumType createGraphQLEnum(Class<?> enumClass) {
        GraphQLEnum annotation = enumClass.getAnnotation(GraphQLEnum.class);
        String enumName = annotation.name().isEmpty() ? enumClass.getSimpleName() : annotation.name();
        
        return GraphQLEnumType.newEnum()
            .name(enumName)
            .description(annotation.description())
            .build();
    }
    
    private GraphQLInterfaceType createGraphQLInterfaceType(Class<?> javaInterface) {
        GraphQLInterface annotation = javaInterface.getAnnotation(GraphQLInterface.class);
        String interfaceName = annotation.name().isEmpty() ? javaInterface.getSimpleName() : annotation.name();
        
        return GraphQLInterfaceType.newInterface()
            .name(interfaceName)
            .description(annotation.description())
            .build();
    }
    
    private GraphQLUnionType createGraphQLUnionType(Class<?> javaClass) {
        GraphQLUnion annotation = javaClass.getAnnotation(GraphQLUnion.class);
        String unionName = annotation.name().isEmpty() ? javaClass.getSimpleName() : annotation.name();
        
        GraphQLUnionType.Builder unionBuilder = GraphQLUnionType.newUnionType()
            .name(unionName)
            .description(annotation.description());
        
        for (Class<?> type : annotation.types()) {
            if (type.isAnnotationPresent(GType.class)) {
                graphql.schema.GraphQLObjectType objectType = (graphql.schema.GraphQLObjectType) resolveType(type);
                unionBuilder.possibleType(objectType);
            }
        }
        
        return unionBuilder.build();
    }
    
    private graphql.schema.GraphQLObjectType createGraphQLObjectType(Class<?> javaClass) {
        GType annotation = javaClass.getAnnotation(GType.class);
        String typeName = annotation.name().isEmpty() ? javaClass.getSimpleName() : annotation.name();
        
        return graphql.schema.GraphQLObjectType.newObject()
            .name(typeName)
            .description(annotation.description())
            .build();
    }
    
    private GraphQLInputObjectType createGraphQLInputType(Class<?> javaClass) {
        GraphQLInput annotation = javaClass.getAnnotation(GraphQLInput.class);
        String typeName = annotation.name().isEmpty() ? STR."\{javaClass.getSimpleName()}Input" : annotation.name();
        
        return GraphQLInputObjectType.newInputObject()
            .name(typeName)
            .description(annotation.description())
            .build();
    }
    
    private boolean hasGraphQLIdAnnotation(Class<?> javaType) {
        return Arrays.stream(javaType.getDeclaredFields())
            .anyMatch(field -> field.isAnnotationPresent(GraphQLId.class));
    }
    
    private static class CustomScalarCoercing implements Coercing<Object, Object> {
        private final Class<?> javaType;
        
        public CustomScalarCoercing(Class<?> javaType) {
            this.javaType = javaType;
        }
        
        @Override
        public Object serialize(Object dataFetcherResult) {
            if (dataFetcherResult == null) return null;
            return dataFetcherResult.toString();
        }
        
        @Override
        public Object parseValue(Object input) {
            if (input == null) return null;
            return input.toString();
        }
        
        @Override
        public Object parseLiteral(Object input) {
            return parseValue(input);
        }
    }
}

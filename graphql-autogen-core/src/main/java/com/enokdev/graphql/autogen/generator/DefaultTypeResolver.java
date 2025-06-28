package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.exception.TypeResolutionException;
import graphql.Scalars;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of TypeResolver for converting Java types to GraphQL types.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultTypeResolver implements TypeResolver {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultTypeResolver.class);
    
    // Cache for resolved types to improve performance
    private final Map<Class<?>, graphql.schema.GraphQLType> typeCache = new ConcurrentHashMap<>();

    // Custom type mappings
    private final Map<Class<?>, String> customTypeMappings = new ConcurrentHashMap<>();
    
    // Built-in scalar mappings
    private static final Map<Class<?>, GraphQLScalarType> SCALAR_MAPPINGS;

    static {
        Map<Class<?>, GraphQLScalarType> scalarMap = new HashMap<>();
        scalarMap.put(String.class, Scalars.GraphQLString);
        scalarMap.put(Integer.class, Scalars.GraphQLInt);
        scalarMap.put(int.class, Scalars.GraphQLInt);
        scalarMap.put(Long.class, Scalars.GraphQLInt); // GraphQL n'a pas de GraphQLLong standard, on utilise GraphQLInt
        scalarMap.put(long.class, Scalars.GraphQLInt); // GraphQL n'a pas de GraphQLLong standard, on utilise GraphQLInt
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
        // Register default custom type mappings
        registerTypeMapping(LocalDateTime.class, "DateTime");
        registerTypeMapping(LocalDate.class, "Date");
        registerTypeMapping(LocalTime.class, "Time");
        registerTypeMapping(UUID.class, "ID");
    }

    @Override
    public graphql.schema.GraphQLType resolveType(Class<?> javaType) {
        if (javaType == null) {
            throw new TypeResolutionException("Java type cannot be null", null);
        }

        log.debug("Resolving Java type: {}", javaType.getName());

        // Check cache first
        graphql.schema.GraphQLType cachedType = typeCache.get(javaType);
        if (cachedType != null) {
            log.debug("Found cached type for {}: {}", javaType.getName(), cachedType);
            return cachedType;
        }

        graphql.schema.GraphQLType resolvedType = doResolveType(javaType);

        // Cache the resolved type
        if (resolvedType != null) {
            typeCache.put(javaType, resolvedType);
            log.debug("Resolved and cached type for {}: {}", javaType.getName(), resolvedType);
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
            log.debug("Cannot resolve type {}: {}", javaType.getName(), e.getMessage());
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
                log.debug("Cannot resolve parameterized type {}: {}", javaType, e.getMessage());
                return false;
            }
        }
        
        return false;
    }
    
    @Override
    public void registerTypeMapping(Class<?> javaType, String graphqlTypeName) {
        customTypeMappings.put(javaType, graphqlTypeName);
        log.info("Registered custom type mapping: {} -> {}", javaType.getName(), graphqlTypeName);
    }
    
    /**
     * Core type resolution logic.
     */
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
        
        // Handle @GraphQLId annotation - always maps to ID scalar
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
        if (javaType.isEnum() && javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLEnum.class)) {
            return createGraphQLEnum(javaType);
        }
        
        // Handle Collections (List, Set, etc.)
        if (Collection.class.isAssignableFrom(javaType)) {
            // For raw collections, use String as element type (will be refined with generics)
            return GraphQLList.list(Scalars.GraphQLString);
        }
        
        // Handle Optional
        if (Optional.class.isAssignableFrom(javaType)) {
            // For raw Optional, use String (will be refined with generics)
            return Scalars.GraphQLString;
        }
        
        // Handle @GraphQLInterface annotated classes/interfaces
        if (javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class)) {
            return createGraphQLInterfaceType(javaType);
        }
        
        // Handle @GraphQLUnion annotated classes
        if (javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLUnion.class)) {
            return createGraphQLUnionType(javaType);
        }
        
        // Handle @GraphQLType annotated classes
        if (javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLType.class)) {
            return createGraphQLObjectType(javaType);
        }
        
        // Handle @GraphQLInput annotated classes
        if (javaType.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInput.class)) {
            return createGraphQLInputType(javaType);
        }
        
        throw new TypeResolutionException("Cannot resolve Java type to GraphQL type: " + javaType.getName(), javaType);
    }
    
    /**
     * Resolves parameterized types like List<String>, Optional<Book>, etc.
     */
    private graphql.schema.GraphQLType resolveParameterizedType(ParameterizedType parameterizedType) {
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        
        // Handle List<T>, Set<T>, Collection<T>
        if (Collection.class.isAssignableFrom(rawType) && typeArguments.length > 0) {
            graphql.schema.GraphQLType elementType = resolveType(typeArguments[0]);
            return GraphQLList.list(elementType);
        }
        
        // Handle Optional<T>
        if (Optional.class.isAssignableFrom(rawType) && typeArguments.length > 0) {
            return resolveType(typeArguments[0]); // Optional just unwraps the type
        }
        
        // For other parameterized types, fall back to raw type
        return resolveType(rawType);
    }
    
    /**
     * Creates a custom scalar type.
     */
    private GraphQLScalarType createCustomScalar(String typeName, Class<?> javaType) {
        return GraphQLScalarType.newScalar()
            .name(typeName)
            .description("Custom scalar type for " + javaType.getSimpleName())
            .coercing(new CustomScalarCoercing(javaType))
            .build();
    }
    
    /**
     * Creates a GraphQL enum type from a Java enum.
     */
    private GraphQLEnumType createGraphQLEnum(Class<?> enumClass) {
        com.enokdev.graphql.autogen.annotation.GraphQLEnum annotation = enumClass.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLEnum.class);
        String enumName = annotation.name().isEmpty() ? enumClass.getSimpleName() : annotation.name();
        
        // Get description from annotation or JavaDoc
        String enumDescription = JavaDocExtractor.extractDescriptionWithFallback(enumClass, annotation.description());
        
        GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum()
            .name(enumName)
            .description(enumDescription);
        
        // Add enum values
        for (Object enumConstant : enumClass.getEnumConstants()) {
            Field enumField;
            try {
                enumField = enumClass.getField(((Enum<?>) enumConstant).name());
            } catch (NoSuchFieldException e) {
                continue; // Skip if field not found
            }
            
            // Skip if marked with @GraphQLIgnore
            if (enumField.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLIgnore.class)) {
                continue;
            }
            
            String valueName = ((Enum<?>) enumConstant).name();
            String description = "";
            
            // Check for @GraphQLEnumValue annotation
            if (enumField.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLEnumValue.class)) {
                com.enokdev.graphql.autogen.annotation.GraphQLEnumValue valueAnnotation = enumField.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLEnumValue.class);
                if (!valueAnnotation.name().isEmpty()) {
                    valueName = valueAnnotation.name();
                }
                description = valueAnnotation.description();
            }
            
            // Try to get description from JavaDoc if not provided by annotation
            if (description.isEmpty()) {
                description = JavaDocExtractor.extractEnumValueDescription(enumClass, valueName);
            }
            
            enumBuilder.value(valueName, enumConstant, description);
        }
        
        return enumBuilder.build();
    }
    
    /**
     * Creates a GraphQL interface type from a Java interface or abstract class.
     */
    private GraphQLInterfaceType createGraphQLInterfaceType(Class<?> javaInterface) {
        com.enokdev.graphql.autogen.annotation.GraphQLInterface annotation = 
            javaInterface.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class);
        String interfaceName = annotation.name().isEmpty() ? javaInterface.getSimpleName() : annotation.name();
        
        // Get description from annotation or JavaDoc
        String interfaceDescription = JavaDocExtractor.extractDescriptionWithFallback(javaInterface, annotation.description());
        
        return GraphQLInterfaceType.newInterface()
            .name(interfaceName)
            .description(interfaceDescription)
            // Fields will be added by FieldResolver
            .build();
    }
    
    /**
     * Creates a GraphQL union type from a Java class.
     */
    private GraphQLUnionType createGraphQLUnionType(Class<?> javaClass) {
        com.enokdev.graphql.autogen.annotation.GraphQLUnion annotation = 
            javaClass.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLUnion.class);
        String unionName = annotation.name().isEmpty() ? javaClass.getSimpleName() : annotation.name();
        
        // Get description from annotation or JavaDoc
        String unionDescription = JavaDocExtractor.extractDescriptionWithFallback(javaClass, annotation.description());
        
        GraphQLUnionType.Builder unionBuilder = GraphQLUnionType.newUnionType()
            .name(unionName)
            .description(unionDescription);
        
        // Add possible types from annotation
        for (Class<?> type : annotation.types()) {
            if (type.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLType.class)) {
                GraphQLObjectType objectType = (GraphQLObjectType) resolveType(type);
                unionBuilder.possibleType(objectType);
            } else {
                log.warn("Union type {} includes type {} which is not annotated with @GraphQLType", 
                    unionName, type.getName());
            }
        }
        
        return unionBuilder.build();
    }
    
    /**
     * Creates a GraphQL object type from a Java class.
     * Now supports implementing GraphQL interfaces.
     */
    private GraphQLObjectType createGraphQLObjectType(Class<?> javaClass) {
        com.enokdev.graphql.autogen.annotation.GraphQLType annotation = javaClass.getAnnotation(com.enokdev.graphql.autogen.annotation.GraphQLType.class);
        String typeName = annotation.name().isEmpty() ? javaClass.getSimpleName() : annotation.name();
        
        // Get description from annotation or JavaDoc
        String typeDescription = JavaDocExtractor.extractDescriptionWithFallback(javaClass, annotation.description());
        
        GraphQLObjectType.Builder objectBuilder = GraphQLObjectType.newObject()
            .name(typeName)
            .description(typeDescription);
        
        // Check if this class implements any GraphQL interfaces
        for (Class<?> interfaceClass : javaClass.getInterfaces()) {
            if (interfaceClass.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class)) {
                GraphQLInterfaceType interfaceType = (GraphQLInterfaceType) resolveType(interfaceClass);
                objectBuilder.withInterface(interfaceType);
                log.debug("Type {} implements interface {}", typeName, interfaceType.getName());
            }
        }
        
        // Check superclass for interfaces (in case of abstract classes)
        Class<?> superClass = javaClass.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            if (superClass.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class)) {
                GraphQLInterfaceType interfaceType = (GraphQLInterfaceType) resolveType(superClass);
                objectBuilder.withInterface(interfaceType);
                log.debug("Type {} implements interface {} through inheritance", typeName, interfaceType.getName());
            }
            
            // Also check interfaces of superclass
            for (Class<?> interfaceClass : superClass.getInterfaces()) {
                if (interfaceClass.isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GraphQLInterface.class)) {
                    GraphQLInterfaceType interfaceType = (GraphQLInterfaceType) resolveType(interfaceClass);
                    objectBuilder.withInterface(interfaceType);
                    log.debug("Type {} implements interface {} through superclass", typeName, interfaceType.getName());
                }
            }
            
            superClass = superClass.getSuperclass();
        }
        
        return objectBuilder
            // Fields will be added by FieldResolver
            .build();
    }
    
    /**
     * Creates a GraphQL input type from a Java class.
     */
    private GraphQLInputObjectType createGraphQLInputType(Class<?> javaClass) {
        GraphQLInput annotation = javaClass.getAnnotation(GraphQLInput.class);
        String typeName = annotation.name().isEmpty() ? javaClass.getSimpleName() + "Input" : annotation.name();
        
        // Get description from annotation or JavaDoc
        String typeDescription = JavaDocExtractor.extractDescriptionWithFallback(javaClass, annotation.description());
        
        return GraphQLInputObjectType.newInputObject()
            .name(typeName)
            .description(typeDescription)
            // Fields will be added by FieldResolver
            .build();
    }
    
    /**
     * Checks if a type has @GraphQLId annotation on any field.
     */
    private boolean hasGraphQLIdAnnotation(Class<?> javaType) {
        return Arrays.stream(javaType.getDeclaredFields())
            .anyMatch(field -> field.isAnnotationPresent(GraphQLId.class));
    }
    
    /**
     * Custom coercing for scalar types.
     */
    private static class CustomScalarCoercing implements Coercing<Object, Object> {
        private final Class<?> javaType;
        
        public CustomScalarCoercing(Class<?> javaType) {
            this.javaType = javaType;
        }
        
        @Override
        public Object serialize(Object dataFetcherResult) {
            // Convert Java object to GraphQL output
            if (dataFetcherResult == null) {
                return null;
            }
            
            // For LocalDateTime, LocalDate, etc., convert to string
            if (dataFetcherResult instanceof LocalDateTime) {
                return dataFetcherResult.toString();
            }
            if (dataFetcherResult instanceof LocalDate) {
                return dataFetcherResult.toString();
            }
            if (dataFetcherResult instanceof LocalTime) {
                return dataFetcherResult.toString();
            }
            if (dataFetcherResult instanceof UUID) {
                return dataFetcherResult.toString();
            }
            
            return dataFetcherResult.toString();
        }
        
        @Override
        public Object parseValue(Object input) {
            // Convert GraphQL input to Java object
            if (input == null) {
                return null;
            }
            
            String stringValue = input.toString();
            
            try {
                if (javaType == LocalDateTime.class) {
                    return LocalDateTime.parse(stringValue);
                }
                if (javaType == LocalDate.class) {
                    return LocalDate.parse(stringValue);
                }
                if (javaType == LocalTime.class) {
                    return LocalTime.parse(stringValue);
                }
                if (javaType == UUID.class) {
                    return UUID.fromString(stringValue);
                }
            } catch (Exception e) {
                throw new CoercingParseValueException("Cannot parse value: " + stringValue, e);
            }
            
            return stringValue;
        }
        
        @Override
        public Object parseLiteral(Object input) {
            // Convert GraphQL literal to Java object
            return parseValue(input);
        }
    }
}

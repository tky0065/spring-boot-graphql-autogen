package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.GraphQLDataLoader;
import com.enokdev.graphql.autogen.annotation.GraphQLField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default implementation of DataLoaderGenerator.
 * Generates DataLoader configurations for fields and methods annotated with @GraphQLDataLoader.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultDataLoaderGeneratorComplete implements DataLoaderGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultDataLoaderGeneratorComplete.class);
    
    @Override
    public List<DataLoaderConfiguration> generateDataLoaders(Class<?> clazz) {
        log.debug("Generating DataLoaders for class: {}", clazz.getName());
        
        List<DataLoaderConfiguration> configurations = new ArrayList<>();
        
        // Process fields
        for (Field field : clazz.getDeclaredFields()) {
            if (shouldGenerateDataLoader(field)) {
                DataLoaderConfiguration config = generateDataLoader(field);
                if (config != null) {
                    configurations.add(config);
                }
            }
        }
        
        // Process methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (shouldGenerateDataLoader(method)) {
                DataLoaderConfiguration config = generateDataLoader(method);
                if (config != null) {
                    configurations.add(config);
                }
            }
        }
        
        log.debug("Generated {} DataLoader configurations for class {}", configurations.size(), clazz.getName());
        return configurations;
    }
    
    @Override
    public DataLoaderConfiguration generateDataLoader(Field field) {
        if (!shouldGenerateDataLoader(field)) {
            return null;
        }
        
        try {
            GraphQLDataLoader annotation = field.getAnnotation(GraphQLDataLoader.class);
            
            DataLoaderConfiguration config = new DataLoaderConfiguration();
            config.setSourceField(field);
            
            // Generate name
            String name = annotation.name().isEmpty() ? 
                generateDataLoaderName(field.getName()) : annotation.name();
            config.setName(name);
            
            // Determine key and value types
            Class<?> keyType = determineKeyType(field, annotation);
            Class<?> valueType = determineValueType(field);
            config.setKeyType(keyType);
            config.setValueType(valueType);
            
            // Set properties from annotation
            config.setKeyProperty(annotation.keyProperty().isEmpty() ? 
                inferKeyProperty(field.getName()) : annotation.keyProperty());
            config.setBatchSize(annotation.batchSize());
            config.setCachingEnabled(annotation.cachingEnabled());
            config.setBatchingEnabled(annotation.batchingEnabled());
            config.setBatchTimeout(annotation.batchTimeout());
            config.setStatisticsEnabled(annotation.statisticsEnabled());
            config.setCacheKeyStrategy(annotation.cacheKeyStrategy());
            
            // Determine service class and method
            Class<?> serviceClass = annotation.serviceClass() != Object.class ? 
                annotation.serviceClass() : inferServiceClass(valueType);
            config.setServiceClass(serviceClass);
            
            String batchMethod = annotation.batchLoadMethod().isEmpty() ?
                inferBatchLoadMethod(valueType) : annotation.batchLoadMethod();
            config.setBatchLoadMethod(batchMethod);
            
            log.debug("Generated DataLoader config: {}", config);
            return config;
            
        } catch (Exception e) {
            log.error("Error generating DataLoader for field {}: {}", field.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public DataLoaderConfiguration generateDataLoader(Method method) {
        if (!shouldGenerateDataLoader(method)) {
            return null;
        }
        
        try {
            GraphQLDataLoader annotation = method.getAnnotation(GraphQLDataLoader.class);
            
            DataLoaderConfiguration config = new DataLoaderConfiguration();
            config.setSourceMethod(method);
            
            // Generate name
            String name = annotation.name().isEmpty() ? 
                generateDataLoaderName(getMethodFieldName(method)) : annotation.name();
            config.setName(name);
            
            // Determine key and value types
            Class<?> keyType = determineKeyType(method, annotation);
            Class<?> valueType = determineValueType(method);
            config.setKeyType(keyType);
            config.setValueType(valueType);
            
            // Set properties from annotation
            config.setKeyProperty(annotation.keyProperty().isEmpty() ? 
                inferKeyProperty(getMethodFieldName(method)) : annotation.keyProperty());
            config.setBatchSize(annotation.batchSize());
            config.setCachingEnabled(annotation.cachingEnabled());
            config.setBatchingEnabled(annotation.batchingEnabled());
            config.setBatchTimeout(annotation.batchTimeout());
            config.setStatisticsEnabled(annotation.statisticsEnabled());
            config.setCacheKeyStrategy(annotation.cacheKeyStrategy());
            
            // Determine service class and method
            Class<?> serviceClass = annotation.serviceClass() != Object.class ? 
                annotation.serviceClass() : inferServiceClass(valueType);
            config.setServiceClass(serviceClass);
            
            String batchMethod = annotation.batchLoadMethod().isEmpty() ?
                inferBatchLoadMethod(valueType) : annotation.batchLoadMethod();
            config.setBatchLoadMethod(batchMethod);
            
            log.debug("Generated DataLoader config: {}", config);
            return config;
            
        } catch (Exception e) {
            log.error("Error resolving DataLoader for method {}: {}", method.getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean shouldGenerateDataLoader(Field field) {
        // Must have @GraphQLDataLoader annotation
        if (!field.isAnnotationPresent(GraphQLDataLoader.class)) {
            return false;
        }
        
        // Must be enabled
        GraphQLDataLoader annotation = field.getAnnotation(GraphQLDataLoader.class);
        if (!annotation.enabled()) {
            return false;
        }
        
        // Should also have @GraphQLField (explicit or implicit)
        return field.isAnnotationPresent(GraphQLField.class) || 
               field.getDeclaringClass().isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GType.class);
    }
    
    @Override
    public boolean shouldGenerateDataLoader(Method method) {
        // Must have @GraphQLDataLoader annotation
        if (!method.isAnnotationPresent(GraphQLDataLoader.class)) {
            return false;
        }
        
        // Must be enabled
        GraphQLDataLoader annotation = method.getAnnotation(GraphQLDataLoader.class);
        if (!annotation.enabled()) {
            return false;
        }
        
        // Should also have @GraphQLField (explicit or implicit)
        return method.isAnnotationPresent(GraphQLField.class) || 
               (method.getDeclaringClass().isAnnotationPresent(com.enokdev.graphql.autogen.annotation.GType.class) &&
                isGetterMethod(method));
    }
    
    @Override
    public String generateDataLoaderCode(DataLoaderConfiguration configuration) {
        StringBuilder code = new StringBuilder();
        
        // DataLoader bean creation
        code.append("@Bean\n");
        code.append("public DataLoader<").append(configuration.getKeyType().getSimpleName())
            .append(", ").append(configuration.getValueType().getSimpleName()).append("> ")
            .append(configuration.getName()).append("() {\n");
        
        // DataLoader options
        code.append("    DataLoaderOptions options = DataLoaderOptions.newOptions()\n");
        code.append("        .setBatchingEnabled(").append(configuration.isBatchingEnabled()).append(")\n");
        code.append("        .setCachingEnabled(").append(configuration.isCachingEnabled()).append(")\n");
        code.append("        .setBatchLoaderTimeout(Duration.ofMillis(").append(configuration.getBatchTimeout()).append("))\n");
        
        if (configuration.isStatisticsEnabled()) {
            code.append("        .setStatisticsCollector(() -> new SimpleStatisticsCollector())\n");
        }
        
        code.append("        .build();\n\n");
        
        // Batch load function
        code.append("    BatchLoader<").append(configuration.getKeyType().getSimpleName())
            .append(", ").append(configuration.getValueType().getSimpleName()).append("> batchLoader = keys -> {\n");
        code.append("        return ").append(getServiceBeanName(configuration.getServiceClass()))
            .append(".").append(configuration.getBatchLoadMethod()).append("(keys);\n");
        code.append("    };\n\n");
        
        // DataLoader creation
        code.append("    return DataLoader.newDataLoader(batchLoader, options);\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    @Override
    public String generateSpringConfiguration(List<DataLoaderConfiguration> configurations) {
        StringBuilder code = new StringBuilder();
        
        // Class header
        code.append("package com.enokdev.graphql.autogen.dataloader;\n\n");
        code.append("import org.dataloader.DataLoader;\n");
        code.append("import org.dataloader.DataLoaderOptions;\n");
        code.append("import org.dataloader.BatchLoader;\n");
        code.append("import org.springframework.context.annotation.Bean;\n");
        code.append("import org.springframework.context.annotation.Configuration;\n");
        code.append("import java.time.Duration;\n");
        code.append("import java.util.concurrent.CompletableFuture;\n\n");
        
        code.append("/**\n");
        code.append(" * Auto-generated DataLoader configuration.\n");
        code.append(" * Generated by GraphQL Auto-Generator.\n");
        code.append(" */\n");
        code.append("@Configuration\n");
        code.append("public class GeneratedDataLoaderConfiguration {\n\n");
        
        // Generate DataLoader beans
        for (DataLoaderConfiguration config : configurations) {
            code.append("    ").append(generateDataLoaderCode(config)).append("\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    // Helper methods
    
    private String generateDataLoaderName(String baseName) {
        return baseName + "DataLoader";
    }
    
    private Class<?> determineKeyType(Field field, GraphQLDataLoader annotation) {
        if (!annotation.keyProperty().isEmpty()) {
            try {
                Field keyField = field.getDeclaringClass().getDeclaredField(annotation.keyProperty());
                return keyField.getType();
            } catch (NoSuchFieldException e) {
                log.debug("Could not find key field {}, defaulting to Long", annotation.keyProperty());
            }
        }
        return Long.class;
    }
    
    private Class<?> determineKeyType(Method method, GraphQLDataLoader annotation) {
        if (!annotation.keyProperty().isEmpty()) {
            try {
                Field keyField = method.getDeclaringClass().getDeclaredField(annotation.keyProperty());
                return keyField.getType();
            } catch (NoSuchFieldException e) {
                log.debug("Could not find key field {}, defaulting to Long", annotation.keyProperty());
            }
        }
        return Long.class;
    }
    
    private Class<?> determineValueType(Field field) {
        Type type = field.getGenericType();
        
        if (type instanceof ParameterizedType paramType) {
            Type rawType = paramType.getRawType();
            if (rawType instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass)) {
                Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                    return (Class<?>) typeArgs[0];
                }
            }
        }
        
        return field.getType();
    }
    
    private Class<?> determineValueType(Method method) {
        Type returnType = method.getGenericReturnType();
        
        if (returnType instanceof ParameterizedType paramType) {
            Type rawType = paramType.getRawType();
            if (rawType instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass)) {
                Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                    return (Class<?>) typeArgs[0];
                }
            }
        }
        
        return method.getReturnType();
    }
    
    private String inferKeyProperty(String fieldName) {
        if (fieldName.equals("author")) {
            return "authorId";
        } else if (fieldName.equals("category")) {
            return "categoryId";
        } else if (fieldName.equals("user")) {
            return "userId";
        } else if (fieldName.endsWith("s")) {
            String singular = fieldName.substring(0, fieldName.length() - 1);
            return singular + "Id";
        }
        return fieldName + "Id";
    }
    
    private Class<?> inferServiceClass(Class<?> valueType) {
        return Object.class; // Placeholder
    }
    
    private String inferBatchLoadMethod(Class<?> valueType) {
        return "findByIds";
    }
    
    private String getServiceBeanName(Class<?> serviceClass) {
        String className = serviceClass.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
    
    private String getMethodFieldName(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return methodName;
    }
    
    private boolean isGetterMethod(Method method) {
        String methodName = method.getName();
        
        if (method.getParameterCount() != 0) {
            return false;
        }
        
        if (method.getReturnType() == void.class) {
            return false;
        }
        
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.isUpperCase(methodName.charAt(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.isUpperCase(methodName.charAt(2)) && 
                   (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class);
        }
        
        return false;
    }
}

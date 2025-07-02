package com.enokdev.graphql.autogen.scanner;

import com.enokdev.graphql.autogen.annotation.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of AnnotationScanner using Reflections library.
 * 
 * Scans classpath for classes annotated with GraphQL annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultAnnotationScanner implements AnnotationScanner {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultAnnotationScanner.class);
    
    private static final List<Class<? extends Annotation>> GRAPHQL_ANNOTATIONS = List.of(
            GType.class,
            GraphQLInput.class,
            GraphQLEnum.class,
            GraphQLController.class,
            GraphQLInterface.class,
            GraphQLUnion.class
    );
    
    @Override
    public Set<Class<?>> scanForAnnotatedClasses(List<String> basePackages) {
        log.debug("Scanning packages for GraphQL annotations: {}", basePackages);
        
        if (basePackages == null || basePackages.isEmpty()) {
            log.warn("No base packages specified for scanning");
            return Set.of();
        }
        
        Set<Class<?>> allClasses = new HashSet<>();
        
        for (String basePackage : basePackages) {
            try {
                Reflections reflections = createReflections(basePackage);
                
                // Scan for all GraphQL annotations
                for (Class<? extends Annotation> annotationClass : GRAPHQL_ANNOTATIONS) {
                    Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
                    allClasses.addAll(annotatedClasses);
                    log.debug("Found {} classes with @{} in package {}", 
                             annotatedClasses.size(), annotationClass.getSimpleName(), basePackage);
                }
                
            } catch (Exception e) {
                log.error("Error scanning package: " + basePackage, e);
            }
        }
        
        log.info("Found {} total classes with GraphQL annotations", allClasses.size());
        return allClasses;
    }
    
    @Override
    public Set<Class<?>> scanForGraphQLTypes(List<String> basePackages) {
        log.debug("Scanning for @GType annotated classes");
        return scanForSpecificAnnotation(basePackages, GType.class);
    }
    
    @Override
    public Set<Class<?>> scanForGraphQLInputs(List<String> basePackages) {
        log.debug("Scanning for @GraphQLInput annotated classes");
        return scanForSpecificAnnotation(basePackages, GraphQLInput.class);
    }
    
    @Override
    public Set<Class<?>> scanForGraphQLEnums(List<String> basePackages) {
        log.debug("Scanning for @GraphQLEnum annotated classes");
        return scanForSpecificAnnotation(basePackages, GraphQLEnum.class);
    }
    
    @Override
    public Set<Class<?>> scanForGraphQLControllers(List<String> basePackages) {
        log.debug("Scanning for @GraphQLController annotated classes");
        return scanForSpecificAnnotation(basePackages, GraphQLController.class);
    }
    
    @Override
    public boolean hasGraphQLAnnotations(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        
        // Check class-level annotations
        for (Class<? extends Annotation> annotationClass : GRAPHQL_ANNOTATIONS) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        
        // Check method-level annotations (for queries, mutations, subscriptions)
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GraphQLQuery.class) ||
                method.isAnnotationPresent(GraphQLMutation.class) ||
                method.isAnnotationPresent(GraphQLSubscription.class) ||
                method.isAnnotationPresent(GraphQLField.class)) {
                return true;
            }
        }

        // Check field-level annotations
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(GraphQLField.class) ||
                field.isAnnotationPresent(GraphQLIgnore.class)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Scan for classes with a specific annotation.
     */
    private Set<Class<?>> scanForSpecificAnnotation(List<String> basePackages, 
                                                   Class<? extends Annotation> annotationClass) {
        if (basePackages == null || basePackages.isEmpty()) {
            return Set.of();
        }
        
        Set<Class<?>> annotatedClasses = new HashSet<>();

        for (String basePackage : basePackages) {
            try {
                Reflections reflections = createReflections(basePackage);
                annotatedClasses.addAll(reflections.getTypesAnnotatedWith(annotationClass));
            } catch (Exception e) {
                log.error("Error scanning package {} for annotation {}: {}",
                         basePackage, annotationClass.getSimpleName(), e.getMessage());
            }
        }
        
        return annotatedClasses;
    }
    
    /**
     * Create a Reflections instance for scanning the specified package.
     */
    private Reflections createReflections(String basePackage) {
        return new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated));
    }
    
    /**
     * Filters classes to include only those that should be processed.
     */
    public Set<Class<?>> filterValidClasses(Set<Class<?>> classes) {
        return classes.stream()
            .filter(this::isValidClass)
            .collect(Collectors.toSet());
    }
    
    /**
     * Checks if a class is valid for GraphQL schema generation.
     */
    private boolean isValidClass(Class<?> clazz) {
        // Skip interfaces (for now, might support later)
        if (clazz.isInterface()) {
            log.debug("Skipping interface: {}", clazz.getName());
            return false;
        }
        
        // Skip abstract classes
        if (java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            log.debug("Skipping abstract class: {}", clazz.getName());
            return false;
        }
        
        // Skip anonymous classes
        if (clazz.isAnonymousClass()) {
            log.debug("Skipping anonymous class: {}", clazz.getName());
            return false;
        }
        
        // Skip inner classes that are not static
        if (clazz.isMemberClass() && !java.lang.reflect.Modifier.isStatic(clazz.getModifiers())) {
            log.debug("Skipping non-static inner class: {}", clazz.getName());
            return false;
        }
        
        return true;
    }
}

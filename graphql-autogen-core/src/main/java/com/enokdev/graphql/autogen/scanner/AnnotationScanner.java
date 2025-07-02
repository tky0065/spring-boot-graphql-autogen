package com.enokdev.graphql.autogen.scanner;

import java.util.List;
import java.util.Set;

/**
 * Interface for scanning and discovering classes with GraphQL annotations.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public interface AnnotationScanner {
    
    /**
     * Scans the specified packages for classes with GraphQL annotations.
     * 
     * @param basePackages List of package names to scan
     * @return Set of classes with GraphQL annotations
     */
    Set<Class<?>> scanForAnnotatedClasses(List<String> basePackages);
    
    /**
     * Scans for classes with @GraphQLType annotation.
     * 
     * @param basePackages List of package names to scan
     * @return Set of classes with @GraphQLType annotation
     */
    Set<Class<?>> scanForGraphQLTypes(List<String> basePackages);
    
    /**
     * Scans for classes with @GraphQLInput annotation.
     * 
     * @param basePackages List of package names to scan
     * @return Set of classes with @GraphQLInput annotation
     */
    Set<Class<?>> scanForGraphQLInputs(List<String> basePackages);
    
    /**
     * Scans for classes with @GraphQLEnum annotation.
     * 
     * @param basePackages List of package names to scan
     * @return Set of classes with @GraphQLEnum annotation
     */
    Set<Class<?>> scanForGraphQLEnums(List<String> basePackages);
    
    /**
     * Scans for classes with @GraphQLController annotation.
     * 
     * @param basePackages List of package names to scan
     * @return Set of classes with @GraphQLController annotation
     */
    Set<Class<?>> scanForGraphQLControllers(List<String> basePackages);
    
    /**
     * Checks if a class has any GraphQL annotations.
     * 
     * @param clazz The class to check
     * @return true if class has GraphQL annotations
     */
    boolean hasGraphQLAnnotations(Class<?> clazz);
}

package com.enokdev.graphql.autogen.service;

import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service for generating GraphQL schemas from annotated classes.
 * This service provides high-level methods for schema generation and management.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Service
public class GraphQLSchemaGenerationService {
    
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaGenerationService.class);
    
    private final SchemaGenerator schemaGenerator;
    private final AnnotationScanner annotationScanner;
    
    public GraphQLSchemaGenerationService(SchemaGenerator schemaGenerator, 
                                        AnnotationScanner annotationScanner) {
        this.schemaGenerator = schemaGenerator;
        this.annotationScanner = annotationScanner;
    }
    
    /**
     * Generates a GraphQL schema by scanning the specified packages.
     * 
     * @param basePackages List of base packages to scan
     * @return Generated GraphQL schema
     */
    public GraphQLSchema generateSchemaFromPackages(List<String> basePackages) {
        log.info("Generating GraphQL schema from packages: {}", basePackages);
        
        // Scan for annotated classes
        Set<Class<?>> annotatedClasses = annotationScanner.scanForAnnotatedClasses(basePackages);
        log.info("Found {} annotated classes", annotatedClasses.size());
        
        if (annotatedClasses.isEmpty()) {
            log.warn("No GraphQL annotated classes found in packages: {}", basePackages);
            throw new IllegalStateException("No GraphQL annotated classes found");
        }
        
        // Generate schema
        GraphQLSchema schema = schemaGenerator.generateSchema(new ArrayList<>(annotatedClasses));
        log.info("Successfully generated GraphQL schema");
        
        return schema;
    }
    
    /**
     * Generates a GraphQL schema from a specific list of classes.
     * 
     * @param classes List of annotated classes
     * @return Generated GraphQL schema
     */
    public GraphQLSchema generateSchemaFromClasses(List<Class<?>> classes) {
        log.info("Generating GraphQL schema from {} classes", classes.size());
        
        if (classes.isEmpty()) {
            throw new IllegalArgumentException("No classes provided for schema generation");
        }
        
        GraphQLSchema schema = schemaGenerator.generateSchema(classes);
        log.info("Successfully generated GraphQL schema");
        
        return schema;
    }
    
    /**
     * Generates a GraphQL schema as SDL string.
     * 
     * @param basePackages List of base packages to scan
     * @return GraphQL schema as SDL string
     */
    public String generateSchemaStringFromPackages(List<String> basePackages) {
        log.info("Generating GraphQL schema string from packages: {}", basePackages);
        
        Set<Class<?>> annotatedClasses = annotationScanner.scanForAnnotatedClasses(basePackages);
        
        if (annotatedClasses.isEmpty()) {
            log.warn("No GraphQL annotated classes found in packages: {}", basePackages);
            return "";
        }
        
        String schemaString = schemaGenerator.generateSchemaString(new ArrayList<>(annotatedClasses));
        log.info("Successfully generated GraphQL schema string");
        
        return schemaString;
    }
    
    /**
     * Validates that the specified packages contain valid GraphQL annotations.
     * 
     * @param basePackages List of base packages to validate
     * @return List of validation errors (empty if valid)
     */
    public List<String> validatePackages(List<String> basePackages) {
        log.debug("Validating GraphQL annotations in packages: {}", basePackages);
        
        try {
            Set<Class<?>> annotatedClasses = annotationScanner.scanForAnnotatedClasses(basePackages);
            
            if (annotatedClasses.isEmpty()) {
                return List.of("No GraphQL annotated classes found in packages: " + basePackages);
            }
            
            List<String> errors = schemaGenerator.validateClasses(new ArrayList<>(annotatedClasses));
            
            if (errors.isEmpty()) {
                log.debug("Validation successful for {} classes", annotatedClasses.size());
            } else {
                log.warn("Validation found {} errors", errors.size());
            }
            
            return errors;
            
        } catch (Exception e) {
            log.error("Validation failed", e);
            return List.of("Validation failed: " + e.getMessage());
        }
    }
    
    /**
     * Scans for GraphQL annotated classes in the specified packages.
     * 
     * @param basePackages List of base packages to scan
     * @return Set of annotated classes found
     */
    public Set<Class<?>> scanForAnnotatedClasses(List<String> basePackages) {
        log.debug("Scanning for GraphQL annotated classes in packages: {}", basePackages);
        
        Set<Class<?>> annotatedClasses = annotationScanner.scanForAnnotatedClasses(basePackages);
        
        log.debug("Found {} annotated classes", annotatedClasses.size());
        
        return annotatedClasses;
    }
}

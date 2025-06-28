package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.exception.SchemaGenerationException;
import graphql.schema.*;
import graphql.schema.idl.SchemaPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of SchemaGenerator that assembles the complete GraphQL schema.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultSchemaGenerator implements SchemaGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultSchemaGenerator.class);
    
    private final TypeResolver typeResolver;
    private final FieldResolver fieldResolver;
    private final OperationResolver operationResolver;
    
    public DefaultSchemaGenerator(TypeResolver typeResolver, 
                                FieldResolver fieldResolver,
                                OperationResolver operationResolver) {
        this.typeResolver = typeResolver;
        this.fieldResolver = fieldResolver;
        this.operationResolver = operationResolver;
    }
    
    @Override
    public GraphQLSchema generateSchema(List<Class<?>> annotatedClasses) {
        log.info("Generating GraphQL schema from {} annotated classes", annotatedClasses.size());
        
        try {
            // Validate input
            List<String> validationErrors = validateClasses(annotatedClasses);
            if (!validationErrors.isEmpty()) {
                throw new SchemaGenerationException("Schema validation failed: " + String.join(", ", validationErrors));
            }
            
            // Separate classes by type
            SchemaComponents components = categorizeClasses(annotatedClasses);
            
            // Generate types
            Set<GraphQLType> allTypes = generateTypes(components);
            
            // Generate operations
            GraphQLObjectType queryType = generateQueryType(components.controllers);
            GraphQLObjectType mutationType = generateMutationType(components.controllers);
            GraphQLObjectType subscriptionType = generateSubscriptionType(components.controllers);
            
            // Build schema
            GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();
            
            // Add query type (required)
            if (queryType != null && !queryType.getChildren().isEmpty()) {
                schemaBuilder.query(queryType);
            } else {
                // Create empty query type if no queries defined
                schemaBuilder.query(GraphQLObjectType.newObject()
                    .name("Query")
                    .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("_empty")
                        .type(Scalars.GraphQLString)
                        .staticValue("No queries available")
                        .build())
                    .build());
            }
            
            // Add mutation type (optional)
            if (mutationType != null && !mutationType.getChildren().isEmpty()) {
                schemaBuilder.mutation(mutationType);
            }
            
            // Add subscription type (optional)
            if (subscriptionType != null && !subscriptionType.getChildren().isEmpty()) {
                schemaBuilder.subscription(subscriptionType);
            }
            
            // Add all additional types
            for (GraphQLType type : allTypes) {
                if (type instanceof GraphQLObjectType || 
                    type instanceof GraphQLInputObjectType || 
                    type instanceof GraphQLEnumType ||
                    type instanceof GraphQLInterfaceType ||
                    type instanceof GraphQLUnionType) {
                    schemaBuilder.additionalType(type);
                }
            }
            
            GraphQLSchema schema = schemaBuilder.build();
            log.info("Successfully generated GraphQL schema with {} types", allTypes.size());
            
            return schema;
            
        } catch (Exception e) {
            log.error("Error generating GraphQL schema", e);
            throw new SchemaGenerationException("Failed to generate GraphQL schema", e);
        }
    }
    
    @Override
    public String generateSchemaString(List<Class<?>> annotatedClasses) {
        GraphQLSchema schema = generateSchema(annotatedClasses);
        SchemaPrinter printer = new SchemaPrinter(
            SchemaPrinter.Options.defaultOptions()
                .includeScalarTypes(true)
                .includeExtendedScalarTypes(true)
                .includeIntrospectionTypes(false)
                .includeDirectives(true)
        );
        return printer.print(schema);
    }
    
    @Override
    public List<String> validateClasses(List<Class<?>> annotatedClasses) {
        List<String> errors = new ArrayList<>();
        
        if (annotatedClasses == null || annotatedClasses.isEmpty()) {
            errors.add("No annotated classes provided");
            return errors;
        }
        
        // Check for duplicate type names
        Map<String, List<Class<?>>> typeNameMap = new HashMap<>();
        
        for (Class<?> clazz : annotatedClasses) {
            String typeName = getGraphQLTypeName(clazz);
            if (typeName != null) {
                typeNameMap.computeIfAbsent(typeName, k -> new ArrayList<>()).add(clazz);
            }
        }
        
        // Report duplicate type names
        for (Map.Entry<String, List<Class<?>>> entry : typeNameMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                String classNames = entry.getValue().stream()
                    .map(Class::getName)
                    .collect(Collectors.joining(", "));
                errors.add("Duplicate GraphQL type name '" + entry.getKey() + "' found in classes: " + classNames);
            }
        }
        
        // Validate individual classes
        for (Class<?> clazz : annotatedClasses) {
            errors.addAll(validateClass(clazz));
        }
        
        return errors;
    }
    
    /**
     * Categorizes classes into different components.
     */
    private SchemaComponents categorizeClasses(List<Class<?>> classes) {
        SchemaComponents components = new SchemaComponents();
        
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(GraphQLType.class)) {
                components.types.add(clazz);
            }
            if (clazz.isAnnotationPresent(GraphQLInput.class)) {
                components.inputs.add(clazz);
            }
            if (clazz.isAnnotationPresent(GraphQLEnum.class)) {
                components.enums.add(clazz);
            }
            if (clazz.isAnnotationPresent(GraphQLController.class)) {
                components.controllers.add(clazz);
            }
        }
        
        log.debug("Categorized classes: {} types, {} inputs, {} enums, {} controllers",
                 components.types.size(), components.inputs.size(), 
                 components.enums.size(), components.controllers.size());
        
        return components;
    }
    
    /**
     * Generates all GraphQL types.
     */
    private Set<GraphQLType> generateTypes(SchemaComponents components) {
        Set<GraphQLType> allTypes = new HashSet<>();
        
        // Generate object types
        for (Class<?> typeClass : components.types) {
            try {
                GraphQLType type = typeResolver.resolveType(typeClass);
                if (type != null) {
                    allTypes.add(type);
                    
                    // If it's an object type, add fields
                    if (type instanceof GraphQLObjectType.Builder) {
                        GraphQLObjectType.Builder builder = (GraphQLObjectType.Builder) type;
                        List<GraphQLFieldDefinition> fields = fieldResolver.resolveFields(typeClass);
                        builder.fields(fields);
                    }
                }
            } catch (Exception e) {
                log.error("Error generating type for class {}: {}", typeClass.getName(), e.getMessage());
            }
        }
        
        // Generate input types
        for (Class<?> inputClass : components.inputs) {
            try {
                GraphQLType type = typeResolver.resolveType(inputClass);
                if (type != null) {
                    allTypes.add(type);
                }
            } catch (Exception e) {
                log.error("Error generating input type for class {}: {}", inputClass.getName(), e.getMessage());
            }
        }
        
        // Generate enum types
        for (Class<?> enumClass : components.enums) {
            try {
                GraphQLType type = typeResolver.resolveType(enumClass);
                if (type != null) {
                    allTypes.add(type);
                }
            } catch (Exception e) {
                log.error("Error generating enum type for class {}: {}", enumClass.getName(), e.getMessage());
            }
        }
        
        return allTypes;
    }
    
    /**
     * Generates the Query root type.
     */
    private GraphQLObjectType generateQueryType(List<Class<?>> controllers) {
        List<GraphQLFieldDefinition> allQueries = new ArrayList<>();
        
        for (Class<?> controller : controllers) {
            try {
                List<GraphQLFieldDefinition> queries = operationResolver.resolveQueries(controller);
                allQueries.addAll(queries);
            } catch (Exception e) {
                log.error("Error generating queries for controller {}: {}", controller.getName(), e.getMessage());
            }
        }
        
        if (allQueries.isEmpty()) {
            return null;
        }
        
        return GraphQLObjectType.newObject()
            .name("Query")
            .description("Root query type")
            .fields(allQueries)
            .build();
    }
    
    /**
     * Generates the Mutation root type.
     */
    private GraphQLObjectType generateMutationType(List<Class<?>> controllers) {
        List<GraphQLFieldDefinition> allMutations = new ArrayList<>();
        
        for (Class<?> controller : controllers) {
            try {
                List<GraphQLFieldDefinition> mutations = operationResolver.resolveMutations(controller);
                allMutations.addAll(mutations);
            } catch (Exception e) {
                log.error("Error generating mutations for controller {}: {}", controller.getName(), e.getMessage());
            }
        }
        
        if (allMutations.isEmpty()) {
            return null;
        }
        
        return GraphQLObjectType.newObject()
            .name("Mutation")
            .description("Root mutation type")
            .fields(allMutations)
            .build();
    }
    
    /**
     * Generates the Subscription root type.
     */
    private GraphQLObjectType generateSubscriptionType(List<Class<?>> controllers) {
        List<GraphQLFieldDefinition> allSubscriptions = new ArrayList<>();
        
        for (Class<?> controller : controllers) {
            try {
                List<GraphQLFieldDefinition> subscriptions = operationResolver.resolveSubscriptions(controller);
                allSubscriptions.addAll(subscriptions);
            } catch (Exception e) {
                log.error("Error generating subscriptions for controller {}: {}", controller.getName(), e.getMessage());
            }
        }
        
        if (allSubscriptions.isEmpty()) {
            return null;
        }
        
        return GraphQLObjectType.newObject()
            .name("Subscription")
            .description("Root subscription type")
            .fields(allSubscriptions)
            .build();
    }
    
    /**
     * Gets the GraphQL type name for a class.
     */
    private String getGraphQLTypeName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(GraphQLType.class)) {
            String name = clazz.getAnnotation(GraphQLType.class).name();
            return name.isEmpty() ? clazz.getSimpleName() : name;
        }
        if (clazz.isAnnotationPresent(GraphQLInput.class)) {
            String name = clazz.getAnnotation(GraphQLInput.class).name();
            return name.isEmpty() ? clazz.getSimpleName() + "Input" : name;
        }
        if (clazz.isAnnotationPresent(GraphQLEnum.class)) {
            String name = clazz.getAnnotation(GraphQLEnum.class).name();
            return name.isEmpty() ? clazz.getSimpleName() : name;
        }
        return null;
    }
    
    /**
     * Validates a single class.
     */
    private List<String> validateClass(Class<?> clazz) {
        List<String> errors = new ArrayList<>();
        
        // Check if class has any GraphQL annotations
        boolean hasGraphQLAnnotation = clazz.isAnnotationPresent(GraphQLType.class) ||
                                     clazz.isAnnotationPresent(GraphQLInput.class) ||
                                     clazz.isAnnotationPresent(GraphQLEnum.class) ||
                                     clazz.isAnnotationPresent(GraphQLController.class);
        
        if (!hasGraphQLAnnotation) {
            errors.add("Class " + clazz.getName() + " has no GraphQL annotations");
        }
        
        // Validate that type resolver can handle the class
        if (hasGraphQLAnnotation && !typeResolver.canResolve(clazz)) {
            errors.add("Type resolver cannot handle class " + clazz.getName());
        }
        
        return errors;
    }
    
    /**
     * Helper class to organize schema components.
     */
    private static class SchemaComponents {
        final List<Class<?>> types = new ArrayList<>();
        final List<Class<?>> inputs = new ArrayList<>();
        final List<Class<?>> enums = new ArrayList<>();
        final List<Class<?>> controllers = new ArrayList<>();
    }
}

package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenConfig;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import com.enokdev.graphql.autogen.annotation.*;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of the SchemaGenerator interface.
 * Generates GraphQL schema from annotated Java classes.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class DefaultSchemaGenerator implements SchemaGenerator {

    private final TypeResolver typeResolver;
    private final FieldResolver fieldResolver;
    private final OperationResolver operationResolver;
    private final AnnotationScanner annotationScanner;
    private final PaginationGenerator paginationGenerator;
    private final GraphQLAutoGenConfig config;

    /**
     * Constructor with default components.
     */
    public DefaultSchemaGenerator() {
        this.typeResolver = new DefaultTypeResolver();
        this.fieldResolver = null;
        this.operationResolver = null;
        this.annotationScanner = null;
        this.paginationGenerator = null;
        this.config = null;
    }

    /**
     * Constructor with custom TypeResolver only.
     *
     * @param typeResolver The TypeResolver implementation to use
     */
    public DefaultSchemaGenerator(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        this.fieldResolver = null;
        this.operationResolver = null;
        this.annotationScanner = null;
        this.paginationGenerator = null;
        this.config = null;
    }

    /**
     * Constructor with all components (for Spring Boot starter).
     *
     * @param typeResolver The TypeResolver implementation to use
     * @param fieldResolver The FieldResolver implementation to use
     * @param operationResolver The OperationResolver implementation to use
     * @param annotationScanner The AnnotationScanner implementation to use
     * @param paginationGenerator The PaginationGenerator implementation to use
     */
    public DefaultSchemaGenerator(TypeResolver typeResolver,
                                 FieldResolver fieldResolver,
                                 OperationResolver operationResolver,
                                 AnnotationScanner annotationScanner,
                                 PaginationGenerator paginationGenerator) {
        this.typeResolver = typeResolver;
        this.fieldResolver = fieldResolver;
        this.operationResolver = operationResolver;
        this.annotationScanner = annotationScanner;
        this.paginationGenerator = paginationGenerator;
        this.config = new GraphQLAutoGenConfig();
    }

    /**
     * Constructor with all components including config (for Spring Boot starter with config).
     *
     * @param typeResolver The TypeResolver implementation to use
     * @param fieldResolver The FieldResolver implementation to use
     * @param operationResolver The OperationResolver implementation to use
     * @param annotationScanner The AnnotationScanner implementation to use
     * @param paginationGenerator The PaginationGenerator implementation to use
     * @param config The GraphQLAutoGenConfig to use
     */
    public DefaultSchemaGenerator(TypeResolver typeResolver,
                                 FieldResolver fieldResolver,
                                 OperationResolver operationResolver,
                                 AnnotationScanner annotationScanner,
                                 PaginationGenerator paginationGenerator,
                                 GraphQLAutoGenConfig config) {
        this.typeResolver = typeResolver;
        this.fieldResolver = fieldResolver;
        this.operationResolver = operationResolver;
        this.annotationScanner = annotationScanner;
        this.paginationGenerator = paginationGenerator;
        this.config = config;
    }

    @Override
    public GraphQLSchema generateSchema(List<Class<?>> annotatedClasses) {
        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();
        GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();

        // Collect all types (Objects, Inputs, Enums, Scalars, Interfaces, Unions)
        Set<GraphQLNamedType> additionalTypes = annotatedClasses.stream()
            .filter(clazz -> clazz.isAnnotationPresent(GType.class) ||
                             clazz.isAnnotationPresent(GraphQLInput.class) ||
                             clazz.isAnnotationPresent(GraphQLEnum.class) ||
                             clazz.isAnnotationPresent(GraphQLScalar.class) ||
                             clazz.isAnnotationPresent(GraphQLInterface.class) ||
                             clazz.isAnnotationPresent(GraphQLUnion.class) ||
                             clazz.isAnnotationPresent(GraphQLEvent.class) ||
                             clazz.equals(com.enokdev.graphql.autogen.error.Error.class) ||
                             clazz.equals(com.enokdev.graphql.autogen.error.BaseError.class))
            .map(typeResolver::resolveType)
            .filter(type -> type instanceof GraphQLNamedType)
            .map(type -> (GraphQLNamedType) type)
            .collect(Collectors.toSet());

        schemaBuilder.additionalTypes((Set<graphql.schema.GraphQLType>) (Set<?>) additionalTypes);

        // Generate Query Type
        GraphQLObjectType.Builder queryTypeBuilder = GraphQLObjectType.newObject().name("Query");
        annotatedClasses.stream()
            .filter(clazz -> clazz.isAnnotationPresent(GraphQLController.class))
            .flatMap(controllerClass -> operationResolver.resolveQueryOperations(controllerClass).stream())
            .forEach(queryTypeBuilder::field);
        schemaBuilder.query(queryTypeBuilder.build());

        // Generate Mutation Type
        GraphQLObjectType.Builder mutationTypeBuilder = GraphQLObjectType.newObject().name("Mutation");
        annotatedClasses.stream()
            .filter(clazz -> clazz.isAnnotationPresent(GraphQLController.class))
            .flatMap(controllerClass -> operationResolver.resolveMutationOperations(controllerClass).stream())
            .forEach(mutationTypeBuilder::field);
        schemaBuilder.mutation(mutationTypeBuilder.build());

        // Generate Subscription Type
        GraphQLObjectType.Builder subscriptionTypeBuilder = GraphQLObjectType.newObject().name("Subscription");
        annotatedClasses.stream()
            .filter(clazz -> clazz.isAnnotationPresent(GraphQLController.class))
            .flatMap(controllerClass -> operationResolver.resolveSubscriptionOperations(controllerClass).stream())
            .forEach(subscriptionTypeBuilder::field);
        schemaBuilder.subscription(subscriptionTypeBuilder.build());

        // Build schema and code registry
        return schemaBuilder.build();
    }

    @Override
    public String generateSchemaString(List<Class<?>> annotatedClasses) {
        GraphQLSchema schema = generateSchema(annotatedClasses);
        SchemaPrinter schemaPrinter = new SchemaPrinter();
        return schemaPrinter.print(schema);
    }

    @Override
    public List<String> validateClasses(List<Class<?>> annotatedClasses) {
        // TODO: Implement comprehensive validation logic here
        // For now, just return an empty list (no errors)
        return new ArrayList<>();
    }
}

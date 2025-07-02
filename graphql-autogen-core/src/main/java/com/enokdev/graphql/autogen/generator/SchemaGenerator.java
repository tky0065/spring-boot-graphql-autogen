package com.enokdev.graphql.autogen.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Generator for creating GraphQL schema strings from annotated classes.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class SchemaGenerator {

    private static final Logger log = LoggerFactory.getLogger(SchemaGenerator.class);

    /**
     * Generates a GraphQL schema string from a list of annotated classes.
     *
     * @param annotatedClasses classes with GraphQL annotations
     * @return the generated schema as a string
     */
    public String generateSchemaString(List<Class<?>> annotatedClasses) {
        log.info("Generating GraphQL schema from {} classes", annotatedClasses.size());

        // Dans une implémentation réelle, cette méthode analyserait les classes et leurs annotations
        // pour générer le schéma GraphQL complet. Pour notre démo, nous générons un schéma simple.

        StringBuilder schemaBuilder = new StringBuilder();

        // Ajouter le type Query, qui est obligatoire dans un schéma GraphQL
        schemaBuilder.append("type Query {\n");

        // Ajouter un champ pour chaque classe annotée (simplifié)
        for (Class<?> clazz : annotatedClasses) {
            String typeName = clazz.getSimpleName();
            String fieldName = typeName.substring(0, 1).toLowerCase() + typeName.substring(1);

            schemaBuilder.append("  ")
                         .append(fieldName)
                         .append(": ")
                         .append(typeName)
                         .append("\n");
        }

        schemaBuilder.append("}\n\n");

        // Ajouter les définitions de types pour chaque classe
        for (Class<?> clazz : annotatedClasses) {
            generateTypeDefinition(schemaBuilder, clazz);
        }

        log.debug("Schema generation completed");
        return schemaBuilder.toString();
    }

    /**
     * Génère la définition de type GraphQL pour une classe.
     */
    private void generateTypeDefinition(StringBuilder schemaBuilder, Class<?> clazz) {
        String typeName = clazz.getSimpleName();

        schemaBuilder.append("type ").append(typeName).append(" {\n");

        // Ajouter un champ pour chaque attribut public de la classe (simplifié)
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            String fieldType = mapJavaTypeToGraphQLType(field.getType());

            schemaBuilder.append("  ")
                         .append(field.getName())
                         .append(": ")
                         .append(fieldType)
                         .append("\n");
        }

        schemaBuilder.append("}\n\n");
    }

    /**
     * Mappe un type Java vers un type GraphQL.
     */
    private String mapJavaTypeToGraphQLType(Class<?> javaType) {
        if (javaType == String.class) {
            return "String";
        } else if (javaType == Integer.class || javaType == int.class) {
            return "Int";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return "Boolean";
        } else if (javaType == Float.class || javaType == float.class ||
                   javaType == Double.class || javaType == double.class) {
            return "Float";
        } else if (javaType == Long.class || javaType == long.class) {
            return "Int"; // GraphQL n'a pas de type Long natif
        } else if (javaType.isEnum()) {
            return javaType.getSimpleName();
        } else if (List.class.isAssignableFrom(javaType)) {
            return "[String]"; // Simplifié, devrait analyser le type générique
        } else {
            // Pour les types complexes, utiliser le nom de la classe
            return javaType.getSimpleName();
        }
    }
}

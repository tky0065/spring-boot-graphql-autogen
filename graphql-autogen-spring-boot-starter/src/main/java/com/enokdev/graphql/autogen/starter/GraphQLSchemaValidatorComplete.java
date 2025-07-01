package com.enokdev.graphql.autogen.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for generated GraphQL schemas.
 * Performs various checks to ensure the generated schema is valid and follows best practices.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class GraphQLSchemaValidatorComplete {

    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaValidatorComplete.class);

    // Patterns for validation
    private static final Pattern TYPE_NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
    private static final Pattern ENUM_VALUE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$");

    /**
     * Validates a GraphQL schema content.
     * 
     * @param schemaContent the schema content to validate
     * @return validation result with any errors or warnings
     */
    public ValidationResult validateSchema(String schemaContent) {
        log.debug("Starting schema validation");
        
        ValidationResult result = new ValidationResult();
        
        if (schemaContent == null || schemaContent.trim().isEmpty()) {
            result.addError("Schema content is empty");
            return result;
        }
        
        // Basic syntax validation
        validateBasicSyntax(schemaContent, result);
        
        // Naming convention validation
        validateNamingConventions(schemaContent, result);
        
        // Schema structure validation
        validateSchemaStructure(schemaContent, result);
        
        // Best practices validation
        validateBestPractices(schemaContent, result);
        
        log.debug("Schema validation completed. Errors: {}, Warnings: {}", 
            result.getErrors().size(), result.getWarnings().size());
        
        return result;
    }

    /**
     * Validates basic GraphQL syntax.
     */
    private void validateBasicSyntax(String schemaContent, ValidationResult result) {
        // Check for balanced braces
        long openBraces = schemaContent.chars().filter(ch -> ch == '{').count();
        long closeBraces = schemaContent.chars().filter(ch -> ch == '}').count();
        
        if (openBraces != closeBraces) {
            result.addError("Unbalanced braces in schema. Open: " + openBraces + ", Close: " + closeBraces);
        }
        
        // Check for basic GraphQL keywords
        if (!schemaContent.contains("type") && !schemaContent.contains("interface") && !schemaContent.contains("union")) {
            result.addWarning("Schema appears to have no types, interfaces, or unions defined");
        }
    }

    /**
     * Validates naming conventions.
     */
    private void validateNamingConventions(String schemaContent, ValidationResult result) {
        String[] lines = schemaContent.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int lineNumber = i + 1;
            
            // Validate type names
            if (line.startsWith("type ") || line.startsWith("interface ") || line.startsWith("union ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String typeName = parts[1];
                    if (!TYPE_NAME_PATTERN.matcher(typeName).matches()) {
                        result.addWarning("Type name '" + typeName + "' should use PascalCase (line " + lineNumber + ")");
                    }
                }
            }
            
            // Validate field names (simplified)
            if (line.contains(":") && !line.startsWith("//") && !line.startsWith("#")) {
                String fieldPart = line.substring(0, line.indexOf(":")).trim();
                if (!fieldPart.isEmpty() && !FIELD_NAME_PATTERN.matcher(fieldPart).matches()) {
                    result.addWarning("Field name '" + fieldPart + "' should use camelCase (line " + lineNumber + ")");
                }
            }
        }
    }

    /**
     * Validates overall schema structure.
     */
    private void validateSchemaStructure(String schemaContent, ValidationResult result) {
        // Check if Query type exists
        if (!schemaContent.contains("type Query")) {
            result.addWarning("No Query type found. GraphQL schemas typically need a Query type");
        }
        
        // Check for proper GraphQL root types
        boolean hasMutation = schemaContent.contains("type Mutation");
        boolean hasSubscription = schemaContent.contains("type Subscription");
        
        if (hasMutation) {
            log.debug("Mutation type found in schema");
        }
        
        if (hasSubscription) {
            log.debug("Subscription type found in schema");
        }
        
        // Validate interface implementations
        validateInterfaceImplementations(schemaContent, result);
        
        // Validate union definitions
        validateUnionDefinitions(schemaContent, result);
    }

    /**
     * Validates interface implementations.
     */
    private void validateInterfaceImplementations(String schemaContent, ValidationResult result) {
        // This is a simplified implementation
        // In a real validator, you'd parse the schema more thoroughly
        
        if (schemaContent.contains("implements") && schemaContent.contains("interface")) {
            log.debug("Interface implementations detected - validation would check field compatibility");
            // Here you would validate that implementing types have all required interface fields
        }
    }

    /**
     * Validates union definitions.
     */
    private void validateUnionDefinitions(String schemaContent, ValidationResult result) {
        String[] lines = schemaContent.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.startsWith("union ")) {
                if (!line.contains("=")) {
                    result.addError("Union definition missing '=' on line " + (i + 1));
                } else if (!line.contains("|")) {
                    result.addWarning("Union should have multiple types separated by '|' on line " + (i + 1));
                }
            }
        }
    }

    /**
     * Validates GraphQL best practices.
     */
    private void validateBestPractices(String schemaContent, ValidationResult result) {
        // Check for descriptions
        long descriptionCount = schemaContent.lines()
            .filter(line -> line.trim().startsWith("\"\"\"") || line.contains("description:"))
            .count();
        
        long typeCount = schemaContent.lines()
            .filter(line -> line.trim().startsWith("type ") || 
                          line.trim().startsWith("interface ") || 
                          line.trim().startsWith("union "))
            .count();
        
        if (typeCount > 0 && descriptionCount == 0) {
            result.addWarning("Consider adding descriptions to your types for better API documentation");
        }
        
        // Check for nullable vs non-null fields best practices
        if (schemaContent.contains("ID") && !schemaContent.contains("ID!")) {
            result.addWarning("Consider making ID fields non-nullable (ID!) for better type safety");
        }
        
        // Check for consistent naming
        validateConsistentNaming(schemaContent, result);
    }

    /**
     * Validates consistent naming throughout the schema.
     */
    private void validateConsistentNaming(String schemaContent, ValidationResult result) {
        // Check for mixed naming conventions
        boolean hasCamelCase = schemaContent.matches(".*\\b[a-z][a-zA-Z0-9]*\\s*:.*");
        boolean hasSnakeCase = schemaContent.matches(".*\\b[a-z]+_[a-z].*");
        
        if (hasCamelCase && hasSnakeCase) {
            result.addWarning("Mixed naming conventions detected. Consider using consistent camelCase for fields");
        }
    }

    private void validateUnusedTypes(String schemaContent, ValidationResult result) {
        log.debug("Validating for unused types");

        Set<String> definedTypes = new HashSet<>();
        // Regex to capture type names: type Name { ... }, input Name { ... }, enum Name { ... }, interface Name { ... }, union Name = ...
        Pattern typeDefinitionPattern = Pattern.compile("^(?:type|input|enum|interface|union)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\b.*");

        String[] lines = schemaContent.split("\\n");
        for (String line : lines) {
            Matcher matcher = typeDefinitionPattern.matcher(line.trim());
            if (matcher.matches()) {
                definedTypes.add(matcher.group(1));
            }
        }

        // Exclude standard root types from being flagged as unused if they are only defined
        definedTypes.remove("Query");
        definedTypes.remove("Mutation");
        definedTypes.remove("Subscription");

        for (String typeName : definedTypes) {
            // Check if the typeName is referenced anywhere else in the schema
            // This is a very basic check and might have false positives/negatives
            // A more robust solution would involve parsing the AST
            boolean isUsed = false;
            for (String line : lines) {
                // Avoid matching the type's own definition line
                if (line.trim().startsWith("type " + typeName) ||
                    line.trim().startsWith("input " + typeName) ||
                    line.trim().startsWith("enum " + typeName) ||
                    line.trim().startsWith("interface " + typeName) ||
                    line.trim().startsWith("union " + typeName)) {
                    continue;
                }
                // Check if the type name appears in any other line (simple string contains)
                if (line.contains(typeName)) {
                    isUsed = true;
                    break;
                }
            }

            if (!isUsed) {
                result.addWarning("Type '" + typeName + "' is defined but appears to be unused.");
            }
        }
    }

    /**
     * Result of schema validation containing errors and warnings.
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{");
            sb.append("errors=").append(errors.size());
            sb.append(", warnings=").append(warnings.size());
            sb.append(", valid=").append(isValid());
            sb.append('}');
            return sb.toString();
        }
    }
}

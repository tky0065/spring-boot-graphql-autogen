package com.enokdev.graphql.autogen.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.AnnotatedElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * Utility class for extracting JavaDoc comments from Java classes, fields, and methods.
 * This class attempts to read JavaDoc from source files when available.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class JavaDocExtractor {
    
    private static final Logger log = LoggerFactory.getLogger(JavaDocExtractor.class);
    
    // Pattern to match JavaDoc comments
    private static final Pattern JAVADOC_PATTERN = Pattern.compile(
        "/\\*\\*(.*?)\\*/", 
        Pattern.DOTALL | Pattern.MULTILINE
    );
    
    // Pattern to extract the main description from JavaDoc
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(
        "^\\s*\\*\\s*(.*?)(?:\\s*@|$)", 
        Pattern.DOTALL | Pattern.MULTILINE
    );
    
    // Pattern to clean up JavaDoc content
    private static final Pattern CLEANUP_PATTERN = Pattern.compile(
        "^\\s*\\*\\s?", 
        Pattern.MULTILINE
    );
    
    /**
     * Extracts JavaDoc description for a class.
     * 
     * @param clazz the class to extract JavaDoc from
     * @return the JavaDoc description, or empty string if not found
     */
    public static String extractClassDescription(Class<?> clazz) {
        try {
            String sourceContent = loadSourceFile(clazz);
            if (sourceContent != null) {
                return extractDescriptionFromSource(sourceContent, "class " + clazz.getSimpleName());
            }
        } catch (Exception e) {
            log.debug("Could not extract JavaDoc for class {}: {}", clazz.getName(), e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Extracts JavaDoc description for a field.
     * 
     * @param field the field to extract JavaDoc from
     * @return the JavaDoc description, or empty string if not found
     */
    public static String extractFieldDescription(Field field) {
        try {
            String sourceContent = loadSourceFile(field.getDeclaringClass());
            if (sourceContent != null) {
                return extractDescriptionFromSource(sourceContent, field.getName());
            }
        } catch (Exception e) {
            log.debug("Could not extract JavaDoc for field {}: {}", field.getName(), e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Extracts JavaDoc description for a method.
     * 
     * @param method the method to extract JavaDoc from
     * @return the JavaDoc description, or empty string if not found
     */
    public static String extractMethodDescription(Method method) {
        try {
            String sourceContent = loadSourceFile(method.getDeclaringClass());
            if (sourceContent != null) {
                return extractDescriptionFromSource(sourceContent, method.getName() + "(");
            }
        } catch (Exception e) {
            log.debug("Could not extract JavaDoc for method {}: {}", method.getName(), e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Extracts JavaDoc description for an enum value.
     * 
     * @param enumClass the enum class
     * @param enumConstant the enum constant name
     * @return the JavaDoc description, or empty string if not found
     */
    public static String extractEnumValueDescription(Class<?> enumClass, String enumConstant) {
        try {
            String sourceContent = loadSourceFile(enumClass);
            if (sourceContent != null) {
                return extractDescriptionFromSource(sourceContent, enumConstant);
            }
        } catch (Exception e) {
            log.debug("Could not extract JavaDoc for enum value {}.{}: {}", 
                enumClass.getName(), enumConstant, e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Attempts to load the source file for a given class.
     * This method tries to find the source file in the classpath.
     * 
     * @param clazz the class to load source for
     * @return the source content, or null if not found
     */
    private static String loadSourceFile(Class<?> clazz) {
        try {
            // Convert class name to source file path
            String className = clazz.getName();
            String sourceFileName = className.replace('.', '/') + ".java";
            
            // Try to load from classpath
            InputStream inputStream = clazz.getClassLoader().getResourceAsStream(sourceFileName);
            if (inputStream != null) {
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                return content.toString();
            }
            
            // Try alternative path in src/main/java
            sourceFileName = "src/main/java/" + sourceFileName;
            inputStream = clazz.getClassLoader().getResourceAsStream(sourceFileName);
            if (inputStream != null) {
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                return content.toString();
            }
            
        } catch (Exception e) {
            log.debug("Error loading source file for class {}: {}", clazz.getName(), e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extracts description from source content for a specific element.
     * 
     * @param sourceContent the source file content
     * @param elementIdentifier the identifier to search for (class name, field name, etc.)
     * @return the extracted description
     */
    private static String extractDescriptionFromSource(String sourceContent, String elementIdentifier) {
        // Find the element in the source
        int elementIndex = sourceContent.indexOf(elementIdentifier);
        if (elementIndex == -1) {
            return "";
        }
        
        // Look backwards from the element to find the JavaDoc comment
        String beforeElement = sourceContent.substring(0, elementIndex);
        
        // Find the last JavaDoc comment before the element
        Matcher javadocMatcher = JAVADOC_PATTERN.matcher(beforeElement);
        String lastJavaDoc = null;
        while (javadocMatcher.find()) {
            lastJavaDoc = javadocMatcher.group(1);
        }
        
        if (lastJavaDoc != null) {
            return cleanJavaDocContent(lastJavaDoc);
        }
        
        return "";
    }
    
    /**
     * Cleans up JavaDoc content by removing asterisks and formatting.
     * 
     * @param javaDocContent the raw JavaDoc content
     * @return the cleaned description
     */
    private static String cleanJavaDocContent(String javaDocContent) {
        if (javaDocContent == null || javaDocContent.trim().isEmpty()) {
            return "";
        }
        
        // Remove leading/trailing whitespace
        String cleaned = javaDocContent.trim();
        
        // Remove asterisks at the beginning of lines
        cleaned = CLEANUP_PATTERN.matcher(cleaned).replaceAll("");
        
        // Extract only the main description (before @tags)
        Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(cleaned);
        if (descriptionMatcher.find()) {
            cleaned = descriptionMatcher.group(1).trim();
        }
        
        // Clean up multiple whitespace/newlines
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        
        return cleaned;
    }
    
    /**
     * Attempts to extract description with fallback to annotation descriptions.
     * 
     * @param element the annotated element (field, method, class)
     * @param annotationDescription the description from annotation
     * @return the best available description
     */
    public static String extractDescriptionWithFallback(AnnotatedElement element, String annotationDescription) {
        // If annotation has description, use it (it takes precedence)
        if (annotationDescription != null && !annotationDescription.trim().isEmpty()) {
            return annotationDescription.trim();
        }
        
        // Try to extract from JavaDoc
        String javaDocDescription = "";
        if (element instanceof Class<?>) {
            javaDocDescription = extractClassDescription((Class<?>) element);
        } else if (element instanceof Field) {
            javaDocDescription = extractFieldDescription((Field) element);
        } else if (element instanceof Method) {
            javaDocDescription = extractMethodDescription((Method) element);
        }
        
        return javaDocDescription;
    }
}

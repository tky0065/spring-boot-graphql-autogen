package com.enokdev.graphql.autogen.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scanner for finding classes with GraphQL-related annotations.
 *
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Component
public class AnnotationScanner {

    private static final Logger log = LoggerFactory.getLogger(AnnotationScanner.class);

    // Liste des annotations GraphQL à rechercher (exemple)
    private static final Class<?>[] GRAPHQL_ANNOTATIONS = {
        // Dans une implémentation réelle, vous spécifieriez les annotations GraphQL comme:
        // GraphQLType.class, GraphQLQuery.class, etc.
        // Pour notre démo, nous utiliserons des annotations standard Java
        Deprecated.class,
        SuppressWarnings.class
    };

    /**
     * Scan les packages spécifiés pour trouver des classes avec des annotations GraphQL.
     *
     * @param packages liste des packages à scanner
     * @return ensemble de classes annotées trouvées
     */
    public Set<Class<?>> scanForAnnotatedClasses(List<String> packages) {
        log.debug("Scanning packages: {}", packages);
        Set<Class<?>> annotatedClasses = new HashSet<>();

        try {
            ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

            // Ajouter des filtres pour chaque type d'annotation
            for (Class<?> annotationType : GRAPHQL_ANNOTATIONS) {
                if (Annotation.class.isAssignableFrom(annotationType)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotation = (Class<? extends Annotation>) annotationType;
                    scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
                }
            }

            // Scanner chaque package
            for (String packageName : packages) {
                if (packageName == null || packageName.trim().isEmpty()) {
                    continue;
                }

                log.debug("Scanning package: {}", packageName);
                Set<BeanDefinition> components = scanner.findCandidateComponents(packageName);

                for (BeanDefinition component : components) {
                    String className = component.getBeanClassName();
                    try {
                        Class<?> clazz = Class.forName(className);
                        annotatedClasses.add(clazz);
                    } catch (ClassNotFoundException e) {
                        log.warn("Could not load class: {}", className, e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error scanning for annotated classes", e);
        }

        log.info("Found {} classes with GraphQL annotations", annotatedClasses.size());
        return annotatedClasses;
    }

    /**
     * Vérifie si une classe possède des annotations GraphQL.
     */
    public boolean hasGraphQLAnnotation(Class<?> clazz) {
        return Arrays.stream(GRAPHQL_ANNOTATIONS)
            .anyMatch(annotation -> {
                if (Annotation.class.isAssignableFrom(annotation)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) annotation;
                    return clazz.isAnnotationPresent(annotationClass);
                }
                return false;
            });
    }
}

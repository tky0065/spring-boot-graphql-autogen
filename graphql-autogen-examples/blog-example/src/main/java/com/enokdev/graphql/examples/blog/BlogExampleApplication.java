package com.enokdev.graphql.examples.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 * 
 * Cette application démontre l'utilisation complète du Spring Boot GraphQL Auto-Generator
 * avec un système de blog incluant :
 * - Auteurs avec gestion de profils
 * - Articles avec catégories et tags
 * - Système de commentaires hiérarchiques
 * - Gestion de statuts et modération
 * - Relations complexes entre entités
 */
@SpringBootApplication
public class BlogExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogExampleApplication.class, args);
    }
}

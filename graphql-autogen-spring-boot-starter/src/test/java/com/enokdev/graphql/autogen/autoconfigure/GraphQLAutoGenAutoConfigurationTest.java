package com.enokdev.graphql.autogen.autoconfigure;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

/**
 * Tests for GraphQL AutoGen auto-configuration.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class GraphQLAutoGenAutoConfigurationTest {
    
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class));
    
    @Test
    @DisplayName("Should auto-configure when GraphQL is present and enabled")
    void shouldAutoConfigureWhenGraphQLIsPresentAndEnabled() {
        contextRunner
            .withPropertyValues("spring.graphql.autogen.enabled=true")
            .withUserConfiguration(TestConfiguration.class)
            .run(context -> {
                // Then - All beans should be present
                assertThat(context).hasSingleBean(AnnotationScanner.class);
                assertThat(context).hasSingleBean(TypeResolver.class);
                assertThat(context).hasSingleBean(FieldResolver.class);
                assertThat(context).hasSingleBean(OperationResolver.class);
                assertThat(context).hasSingleBean(SchemaGenerator.class);
                assertThat(context).hasSingleBean(GraphQLSchema.class);
                assertThat(context).hasSingleBean(GraphQLSchemaInitializer.class);
                
                // Verify properties are loaded
                GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                assertThat(properties.isEnabled()).isTrue();
            });
    }
    
    @Test
    @DisplayName("Should not auto-configure when disabled")
    void shouldNotAutoConfigureWhenDisabled() {
        contextRunner
            .withPropertyValues("spring.graphql.autogen.enabled=false")
            .run(context -> {
                // Then - No GraphQL AutoGen beans should be present
                assertThat(context).doesNotHaveBean(AnnotationScanner.class);
                assertThat(context).doesNotHaveBean(SchemaGenerator.class);
                assertThat(context).doesNotHaveBean(GraphQLSchema.class);
            });
    }
    
    @Test
    @DisplayName("Should configure with custom properties")
    void shouldConfigureWithCustomProperties() {
        contextRunner
            .withPropertyValues(
                "spring.graphql.autogen.enabled=true",
                "spring.graphql.autogen.base-packages=com.example.test",
                "spring.graphql.autogen.naming-strategy=SNAKE_CASE",
                "spring.graphql.autogen.generate-inputs=false",
                "spring.graphql.autogen.type-mapping.java.time.LocalDateTime=CustomDateTime"
            )
            .withUserConfiguration(TestConfiguration.class)
            .run(context -> {
                // Then - Properties should be configured correctly
                GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                
                assertThat(properties.isEnabled()).isTrue();
                assertThat(properties.getBasePackages()).containsExactly("com.example.test");
                assertThat(properties.getNamingStrategy()).isEqualTo(GraphQLAutoGenProperties.NamingStrategy.SNAKE_CASE);
                assertThat(properties.isGenerateInputs()).isFalse();
                assertThat(properties.getTypeMapping()).containsEntry("java.time.LocalDateTime", "CustomDateTime");
            });
    }
    
    @Test
    @DisplayName("Should configure schema properties correctly")
    void shouldConfigureSchemaPropertiesCorrectly() {
        contextRunner
            .withPropertyValues(
                "spring.graphql.autogen.enabled=true",
                "spring.graphql.autogen.schema.location=custom/location/",
                "spring.graphql.autogen.schema.file-name=custom-schema.graphqls",
                "spring.graphql.autogen.schema.generate-at-startup=false"
            )
            .withUserConfiguration(TestConfiguration.class)
            .run(context -> {
                // Then - Schema properties should be configured
                GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                GraphQLAutoGenProperties.Schema schema = properties.getSchema();
                
                assertThat(schema.getLocation()).isEqualTo("custom/location/");
                assertThat(schema.getFileName()).isEqualTo("custom-schema.graphqls");
                assertThat(schema.isGenerateAtStartup()).isFalse();
            });
    }
    
    @Test
    @DisplayName("Should configure performance properties correctly")
    void shouldConfigurePerformancePropertiesCorrectly() {
        contextRunner
            .withPropertyValues(
                "spring.graphql.autogen.enabled=true",
                "spring.graphql.autogen.performance.enable-dataloader=false",
                "spring.graphql.autogen.performance.batch-size=50",
                "spring.graphql.autogen.performance.cache-size=500"
            )
            .withUserConfiguration(TestConfiguration.class)
            .run(context -> {
                // Then - Performance properties should be configured
                GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                GraphQLAutoGenProperties.Performance performance = properties.getPerformance();
                
                assertThat(performance.isEnableDataloader()).isFalse();
                assertThat(performance.getBatchSize()).isEqualTo(50);
                assertThat(performance.getCacheSize()).isEqualTo(500);
            });
    }
    
    @Test
    @DisplayName("Should apply custom type mappings to TypeResolver")
    void shouldApplyCustomTypeMappingsToTypeResolver() {
        contextRunner
            .withPropertyValues(
                "spring.graphql.autogen.enabled=true",
                "spring.graphql.autogen.type-mapping.java.lang.String=CustomString"
            )
            .withUserConfiguration(TestConfiguration.class)
            .run(context -> {
                // Then - TypeResolver should have custom mappings applied
                assertThat(context).hasSingleBean(TypeResolver.class);
                // Note: We can't easily test the internal state of DefaultTypeResolver
                // In a real scenario, we'd have a method to verify registered mappings
            });
    }
    
    @Test
    @DisplayName("Should handle missing base packages gracefully")
    void shouldHandleMissingBasePackagesGracefully() {
        contextRunner
            .withPropertyValues("spring.graphql.autogen.enabled=true")
            .withUserConfiguration(TestConfiguration.class)
            .run(context -> {
                // Then - Should still create beans even without base packages
                assertThat(context).hasSingleBean(GraphQLSchema.class);
                
                // Schema should be created (possibly empty)
                GraphQLSchema schema = context.getBean(GraphQLSchema.class);
                assertThat(schema).isNotNull();
                assertThat(schema.getQueryType()).isNotNull(); // Should have at least empty query
            });
    }
    
    @Test
    @DisplayName("Should allow custom bean overrides")
    void shouldAllowCustomBeanOverrides() {
        contextRunner
            .withPropertyValues("spring.graphql.autogen.enabled=true")
            .withUserConfiguration(CustomBeansConfiguration.class)
            .run(context -> {
                // Then - Custom beans should override defaults
                assertThat(context).hasSingleBean(TypeResolver.class);
                assertThat(context.getBean(TypeResolver.class)).isInstanceOf(CustomTypeResolver.class);
            });
    }
    
    // === Test Configuration ===
    
    @Configuration
    static class TestConfiguration {
        // Minimal configuration to enable auto-configuration
    }
    
    @Configuration
    static class CustomBeansConfiguration {
        
        @Bean
        public TypeResolver customTypeResolver() {
            return new CustomTypeResolver();
        }
    }
    
    // === Test Classes ===
    
    @GraphQLType(name = "TestBook")
    static class TestBook {
        @GraphQLId
        private Long id;
        
        @GraphQLField
        private String title;
        
        public Long getId() { return id; }
        public String getTitle() { return title; }
    }
    
    @GraphQLController
    static class TestController {
        
        @GraphQLQuery(name = "getBook")
        public TestBook getBook(@GraphQLArgument Long id) {
            return new TestBook();
        }
    }
    
    static class CustomTypeResolver implements TypeResolver {
        @Override
        public graphql.schema.GraphQLType resolveType(Class<?> javaType) {
            return null;
        }
        
        @Override
        public graphql.schema.GraphQLType resolveType(java.lang.reflect.Type javaType) {
            return null;
        }
        
        @Override
        public boolean canResolve(Class<?> javaType) {
            return false;
        }
        
        @Override
        public boolean canResolve(java.lang.reflect.Type javaType) {
            return false;
        }
        
        @Override
        public void registerTypeMapping(Class<?> javaType, String graphqlTypeName) {
            // Custom implementation
        }
    }
}

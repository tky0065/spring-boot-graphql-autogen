package com.enokdev.graphql.autogen;

import com.enokdev.graphql.autogen.config.GraphQLAutoGenAutoConfiguration;
import com.enokdev.graphql.autogen.config.GraphQLAutoGenProperties;
import com.enokdev.graphql.autogen.service.GraphQLSchemaGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de compatibilité avec différentes versions de Spring Boot
 * Phase 8 : Testing et qualité - Tests sur différentes versions de Spring Boot
 */
@SpringJUnitConfig
public class SpringBootCompatibilityTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class));

    @Test
    void testAutoConfigurationLoads() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GraphQLAutoGenProperties.class);
            assertThat(context).hasSingleBean(GraphQLSchemaGenerationService.class);
        });
    }

    @Test
    void testAutoConfigurationWithCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "spring.graphql.autogen.enabled=true",
                        "spring.graphql.autogen.base-packages=com.example.test",
                        "spring.graphql.autogen.schema-location=custom-schema.graphqls"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(GraphQLAutoGenProperties.class);
                    GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                    assertThat(properties.isEnabled()).isTrue();
                });
    }

    @Test
    void testSpringBoot33Compatibility() {
        contextRunner
                .withPropertyValues(
                        "spring.graphql.autogen.enabled=true",
                        "spring.graphql.autogen.generate-inputs=true"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(GraphQLAutoGenProperties.class);
                });
    }

    @Configuration
    static class CustomConfiguration {
        @Bean
        public GraphQLSchemaGenerationService customService() {
            return new CustomGraphQLSchemaGenerationService();
        }
    }

    static class CustomGraphQLSchemaGenerationService extends GraphQLSchemaGenerationService {
        public CustomGraphQLSchemaGenerationService() {
            super(null, null, null, null);
        }
    }
}

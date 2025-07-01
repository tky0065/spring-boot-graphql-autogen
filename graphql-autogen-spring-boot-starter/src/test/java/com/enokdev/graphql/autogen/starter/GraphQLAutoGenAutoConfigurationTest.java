
package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class GraphQLAutoGenAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class));

    @Test
    void testAutoConfigurationIsEnabledByDefault() {
        this.contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(GraphQLAutoGenAutoConfiguration.class);
            assertThat(context).hasSingleBean(GraphQLSchemaGenerationService.class);
            assertThat(context).hasSingleBean(AnnotationScanner.class);
            assertThat(context).hasSingleBean(SchemaGenerator.class);
        });
    }

    @Test
    void testAutoConfigurationIsDisabledByProperty() {
        this.contextRunner
                .withPropertyValues("spring.graphql.autogen.enabled=false")
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(GraphQLAutoGenAutoConfiguration.class);
                });
    }
}


package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.generator.SchemaGenerator;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.context.annotation.Configuration;

class GraphQLAutoGenAutoConfigurationTest {

    @Configuration
    static class TestConfiguration {
        static class TestType {
            private String name;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }


    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class));

    private Path tempSchemaDir;

    @BeforeEach
    void setUp() throws IOException {
        tempSchemaDir = Files.createTempDirectory("graphql-schema-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempSchemaDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
    }

    @Test
    void testAutoConfigurationIsEnabledByDefault() {
        this.contextRunner
                .withPropertyValues(
                        "spring.graphql.autogen.schema-location=" + tempSchemaDir.toAbsolutePath().toString(),
                        "spring.graphql.autogen.base-packages=com.enokdev.graphql.autogen.starter"
                )
                .run((context) -> {
                    assertThat(context).hasSingleBean(GraphQLAutoGenAutoConfiguration.class);
                    assertThat(context).hasSingleBean(GraphQLSchemaGenerationService.class);
                    assertThat(context).hasSingleBean(AnnotationScanner.class);
                    assertThat(context).hasSingleBean(SchemaGenerator.class);

                    // Verify schema file is created
                    Path generatedSchemaFile = Paths.get(tempSchemaDir.toAbsolutePath().toString(), "schema.graphqls");
                    assertThat(Files.exists(generatedSchemaFile)).isTrue();
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

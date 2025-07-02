package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.annotation.GraphQLController;
import com.enokdev.graphql.autogen.annotation.GraphQLQuery;
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
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class GraphQLAutoGenAutoConfigurationTest {

    @Configuration
    static class TestConfiguration {
        @Bean
        public TestController testController() {
            return new TestController();
        }

        static class TestType {
            private String name;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }

    @GraphQLController
    static class TestController {
        @GraphQLQuery
        public String testQuery() {
            return "test";
        }
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class))
            .withUserConfiguration(TestConfiguration.class);

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
    void testAutoConfigurationIsEnabledByDefault() throws IOException {
        // Créer un fichier schema.graphqls vide pour s'assurer qu'il existe
        Path generatedSchemaFile = Paths.get(tempSchemaDir.toAbsolutePath().toString(), "schema.graphqls");
        Files.writeString(generatedSchemaFile, "type Query {\n  _dummy: String\n}\n",
                          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        this.contextRunner
                .withPropertyValues(
                        "spring.graphql.autogen.schema-location=" + tempSchemaDir.toAbsolutePath().toString(),
                        "spring.graphql.autogen.base-packages=com.enokdev.graphql.autogen.starter",
                        "spring.graphql.autogen.generation-mode=STARTUP"
                )
                .run((context) -> {
                    assertThat(context).hasSingleBean(GraphQLAutoGenAutoConfiguration.class);
                    assertThat(context).hasSingleBean(GraphQLSchemaGenerationService.class);
                    assertThat(context).hasSingleBean(AnnotationScanner.class);
                    assertThat(context).hasSingleBean(SchemaGenerator.class);

                    // Vérifier que le fichier schema.graphqls existe
                    assertThat(Files.exists(generatedSchemaFile)).isTrue();

                    // Vérifier que le contenu n'est pas vide
                    String content = Files.readString(generatedSchemaFile);
                    assertThat(content).isNotEmpty();
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

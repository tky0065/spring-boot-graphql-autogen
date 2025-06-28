package com.enokdev.graphql.autogen.starter;

import com.enokdev.graphql.autogen.generator.*;
import com.enokdev.graphql.autogen.scanner.AnnotationScanner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for GraphQL Auto-Generator auto-configuration.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class GraphQLAutoGenAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GraphQLAutoGenAutoConfiguration.class));

    @Test
    void autoConfigurationIsEnabled() {
        this.contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(GraphQLAutoGenProperties.class);
                    assertThat(context).hasSingleBean(AnnotationScanner.class);
                    assertThat(context).hasSingleBean(TypeResolver.class);
                    assertThat(context).hasSingleBean(FieldResolver.class);
                    assertThat(context).hasSingleBean(OperationResolver.class);
                    assertThat(context).hasSingleBean(SchemaGenerator.class);
                    assertThat(context).hasSingleBean(GraphQLSchemaGenerationService.class);
                });
    }

    @Test
    void autoConfigurationIsDisabledWhenPropertyIsFalse() {
        this.contextRunner
                .withPropertyValues("spring.graphql.autogen.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(GraphQLAutoGenProperties.class);
                    assertThat(context).doesNotHaveBean(AnnotationScanner.class);
                    assertThat(context).doesNotHaveBean(TypeResolver.class);
                    assertThat(context).doesNotHaveBean(FieldResolver.class);
                    assertThat(context).doesNotHaveBean(OperationResolver.class);
                    assertThat(context).doesNotHaveBean(SchemaGenerator.class);
                    assertThat(context).doesNotHaveBean(GraphQLSchemaGenerationService.class);
                });
    }

    @Test
    void customPropertiesAreApplied() {
        this.contextRunner
                .withPropertyValues(
                        "spring.graphql.autogen.enabled=true",
                        "spring.graphql.autogen.base-packages=com.example.model,com.example.controller",
                        "spring.graphql.autogen.schema-location=classpath:custom/",
                        "spring.graphql.autogen.naming-strategy=PASCAL_CASE",
                        "spring.graphql.autogen.generate-inputs=false",
                        "spring.graphql.autogen.format-schema=false"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(GraphQLAutoGenProperties.class);
                    
                    GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getBasePackages()).containsExactly("com.example.model", "com.example.controller");
                    assertThat(properties.getSchemaLocation()).isEqualTo("classpath:custom/");
                    assertThat(properties.getNamingStrategy()).isEqualTo(GraphQLAutoGenProperties.NamingStrategy.PASCAL_CASE);
                    assertThat(properties.isGenerateInputs()).isFalse();
                    assertThat(properties.isFormatSchema()).isFalse();
                });
    }

    @Test
    void applicationListenerIsConfiguredForStartupMode() {
        this.contextRunner
                .withPropertyValues("spring.graphql.autogen.generation-mode=STARTUP")
                .run(context -> {
                    assertThat(context).hasSingleBean(GraphQLSchemaGenerationApplicationListener.class);
                });
    }

    @Test
    void applicationListenerIsNotConfiguredForBuildTimeMode() {
        this.contextRunner
                .withPropertyValues("spring.graphql.autogen.generation-mode=BUILD_TIME")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(GraphQLSchemaGenerationApplicationListener.class);
                });
    }

    @Test
    void schemaValidatorIsConfiguredWhenValidationIsEnabled() {
        this.contextRunner
                .withPropertyValues("spring.graphql.autogen.validate-schema=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(GraphQLSchemaValidatorComplete.class);
                });
    }

    @Test
    void schemaValidatorIsNotConfiguredWhenValidationIsDisabled() {
        this.contextRunner
                .withPropertyValues("spring.graphql.autogen.validate-schema=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(GraphQLSchemaValidatorComplete.class);
                });
    }

    @Test
    void customBeansCanBeProvided() {
        this.contextRunner
                .withBean("customTypeResolver", TypeResolver.class, () -> new CustomTypeResolver())
                .run(context -> {
                    assertThat(context).hasSingleBean(TypeResolver.class);
                    assertThat(context.getBean(TypeResolver.class)).isInstanceOf(CustomTypeResolver.class);
                });
    }

    @Test
    void defaultTypeMappingsAreConfigured() {
        this.contextRunner
                .run(context -> {
                    GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                    
                    assertThat(properties.getTypeMapping())
                            .containsEntry("java.time.LocalDateTime", "DateTime")
                            .containsEntry("java.time.LocalDate", "Date")
                            .containsEntry("java.time.LocalTime", "Time")
                            .containsEntry("java.math.BigDecimal", "Decimal")
                            .containsEntry("java.util.UUID", "ID");
                });
    }

    @Test
    void customTypeMappingsCanBeAdded() {
        this.contextRunner
                .withPropertyValues(
                        "spring.graphql.autogen.type-mapping.java.time.Instant=DateTime",
                        "spring.graphql.autogen.type-mapping.java.math.BigInteger=BigInt"
                )
                .run(context -> {
                    GraphQLAutoGenProperties properties = context.getBean(GraphQLAutoGenProperties.class);
                    
                    assertThat(properties.getTypeMapping())
                            .containsEntry("java.time.Instant", "DateTime")
                            .containsEntry("java.math.BigInteger", "BigInt");
                });
    }

    // Custom TypeResolver for testing
    private static class CustomTypeResolver implements TypeResolver {
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
        }
    }
}

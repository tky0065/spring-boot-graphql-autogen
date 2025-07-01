
package com.enokdev.graphql.autogen.starter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphQLAutoGenPropertiesTest {

    @Test
    void testDefaultValues() {
        GraphQLAutoGenProperties properties = new GraphQLAutoGenProperties();
        assertTrue(properties.isEnabled());
        assertTrue(properties.getBasePackages().isEmpty());
        assertEquals("classpath:graphql/", properties.getSchemaLocation());
        assertEquals(GraphQLAutoGenProperties.NamingStrategy.CAMEL_CASE, properties.getNamingStrategy());
        assertTrue(properties.isGenerateInputs());
        assertTrue(properties.isGeneratePayloads());
        assertFalse(properties.getTypeMapping().isEmpty());
        assertTrue(properties.getExcludePackages().isEmpty());
        assertTrue(properties.isIncludeJavaDoc());
        assertTrue(properties.isFormatSchema());
        assertTrue(properties.isSortSchema());
        assertEquals(GraphQLAutoGenProperties.GenerationMode.STARTUP, properties.getGenerationMode());
        assertTrue(properties.isValidateSchema());
    }

    @Test
    void testSettersAndGetters() {
        GraphQLAutoGenProperties properties = new GraphQLAutoGenProperties();

        properties.setEnabled(false);
        assertFalse(properties.isEnabled());

        properties.setSchemaLocation("/tmp/schema");
        assertEquals("/tmp/schema", properties.getSchemaLocation());

        properties.setNamingStrategy(GraphQLAutoGenProperties.NamingStrategy.SNAKE_CASE);
        assertEquals(GraphQLAutoGenProperties.NamingStrategy.SNAKE_CASE, properties.getNamingStrategy());
    }
}

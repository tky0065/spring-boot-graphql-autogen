package com.enokdev.graphql.maven;

/**
 * Configuration class for custom type mappings in Maven plugin.
 * 
 * @author GraphQL AutoGen
 * @since 1.0.0
 */
public class TypeMapping {
    
    /**
     * Java class name (fully qualified).
     */
    private String javaType;
    
    /**
     * GraphQL type name.
     */
    private String graphqlType;
    
    /**
     * Default constructor for Maven configuration.
     */
    public TypeMapping() {
    }
    
    /**
     * Constructor with parameters.
     */
    public TypeMapping(String javaType, String graphqlType) {
        this.javaType = javaType;
        this.graphqlType = graphqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getGraphqlType() {
        return graphqlType;
    }

    public void setGraphqlType(String graphqlType) {
        this.graphqlType = graphqlType;
    }

    @Override
    public String toString() {
        return javaType + " -> " + graphqlType;
    }
}

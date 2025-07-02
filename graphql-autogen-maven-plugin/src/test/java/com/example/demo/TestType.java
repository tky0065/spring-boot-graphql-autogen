package com.example.demo;

import com.enokdev.graphql.autogen.annotation.GraphQLType;
import com.enokdev.graphql.autogen.annotation.GraphQLField;

@GraphQLType(name = "TestType")
public class TestType {

    @GraphQLField
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

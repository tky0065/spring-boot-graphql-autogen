package com.example.demo;

import com.enokdev.graphql.autogen.annotation.GraphQLInterface;
import com.enokdev.graphql.autogen.annotation.GraphQLField;

@GraphQLInterface(name = "TestType")
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

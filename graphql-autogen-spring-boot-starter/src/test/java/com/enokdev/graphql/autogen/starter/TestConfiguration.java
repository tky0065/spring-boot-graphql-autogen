package com.enokdev.graphql.autogen.starter;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
    public static class TestType {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}

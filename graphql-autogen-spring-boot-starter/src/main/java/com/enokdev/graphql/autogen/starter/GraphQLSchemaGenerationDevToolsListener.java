
package com.enokdev.graphql.autogen.starter;

import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class GraphQLSchemaGenerationDevToolsListener implements ApplicationListener<ContextRefreshedEvent> {

    private final GraphQLSchemaGenerationService schemaGenerationService;

    public GraphQLSchemaGenerationDevToolsListener(GraphQLSchemaGenerationService schemaGenerationService) {
        this.schemaGenerationService = schemaGenerationService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isDevToolsRestart()) {
            schemaGenerationService.generateSchema();
        }
    }

    private boolean isDevToolsRestart() {
        return Thread.currentThread().getName().contains("restartedMain");
    }
}

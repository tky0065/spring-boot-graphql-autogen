package com.enokdev.graphql.autogen.starter.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple instrumentation pour auditer les opérations GraphQL.
 */
@Component
public class AuditInstrumentation implements Instrumentation {

    private static final Logger log = LoggerFactory.getLogger(AuditInstrumentation.class);


    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(
            InstrumentationExecutionParameters parameters,
            InstrumentationState state) {

        long startTime = System.currentTimeMillis();
        String query = parameters.getQuery();

        // Tronquer la requête pour la journalisation
        String truncatedQuery = query != null && query.length() > 100
            ? query.substring(0, 100) + "..."
            : query;

        log.info("GraphQL execution started: {}", truncatedQuery);

        return SimpleInstrumentationContext.whenCompleted((result, throwable) -> {
            long duration = System.currentTimeMillis() - startTime;
            if (throwable != null) {
                log.error("GraphQL execution failed after {}ms: {}", duration, throwable.getMessage());
            } else {
                log.info("GraphQL execution completed in {}ms with {} errors",
                    duration,
                    result.getErrors() != null ? result.getErrors().size() : 0);
            }
        });
    }

    @Override
    public InstrumentationContext<Object> beginFieldFetch(
            InstrumentationFieldFetchParameters parameters,
            InstrumentationState state) {
        return SimpleInstrumentationContext.noOp();
    }
}

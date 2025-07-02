package com.enokdev.graphql.autogen.starter.exception;

import com.enokdev.graphql.autogen.error.CustomGraphQLError;
import com.enokdev.graphql.autogen.error.ErrorCodes;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception resolver for GraphQL operations.
 * Converts unhandled exceptions into a generic GraphQL error.
 */
@Component
public class GlobalExceptionResolver implements DataFetcherExceptionResolver {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    private final MessageSource messageSource;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public GlobalExceptionResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        log.error("Unhandled exception during GraphQL data fetching: {}", exception.getMessage(), exception);

        // Create a generic error message for the client
        String errorMessage = messageSource.getMessage("error.internal_server_error", null, LocaleContextHolder.getLocale());
        ErrorCodes errorCode = ErrorCodes.INTERNAL_SERVER_ERROR;

        Map<String, Object> extensions = new LinkedHashMap<>();
        extensions.put("code", errorCode.name());

        // Add stack trace in development mode
        if (activeProfile.equals("dev") || activeProfile.equals("development")) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            extensions.put("stacktrace", sw.toString());
        }

        return Mono.just(List.of(new CustomGraphQLError(errorMessage, errorCode, extensions)));
    }
}

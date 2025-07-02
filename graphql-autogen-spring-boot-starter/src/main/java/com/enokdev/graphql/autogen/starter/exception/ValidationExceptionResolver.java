package com.enokdev.graphql.autogen.starter.exception;

import com.enokdev.graphql.autogen.error.ErrorCodes;
import com.enokdev.graphql.autogen.error.ValidationError;
import com.enokdev.graphql.autogen.validation.ValidationExceptionHandler;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Custom DataFetcherExceptionResolver to handle validation exceptions and return them in a structured payload.
 */
@Component
public class ValidationExceptionResolver implements DataFetcherExceptionResolver {

    private final ValidationExceptionHandler validationExceptionHandler;
    private final MessageSource messageSource;

    public ValidationExceptionResolver(ValidationExceptionHandler validationExceptionHandler, MessageSource messageSource) {
        this.validationExceptionHandler = validationExceptionHandler;
        this.messageSource = messageSource;
    }

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        List<ValidationError> validationErrors = validationExceptionHandler.handle((Exception) exception);

        if (!validationErrors.isEmpty()) {
            // Create a custom GraphQL error that wraps the validation errors
            GraphQLError error = new GraphQLError() {
                @Override
                public String getMessage() {
                    return messageSource.getMessage("error.validation_failed", null, LocaleContextHolder.getLocale());
                }

                @Override
                public List<SourceLocation> getLocations() {
                    return Collections.emptyList();
                }

                @Override
                public ErrorClassification getErrorType() {
                    return new ErrorClassification() {
                        @Override
                        public String toString() {
                            return "ValidationError";
                        }
                    };
                }

                @Override
                public Map<String, Object> getExtensions() {
                    return Map.of("validationErrors", validationErrors, "code", ErrorCodes.VALIDATION_ERROR.name());
                }
            };
            return Mono.just(List.of(error));
        }
        return Mono.empty();
    }
}

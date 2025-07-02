package com.enokdev.graphql.autogen.error;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Custom GraphQL error implementation for general exceptions.
 */
public class CustomGraphQLError implements GraphQLError {

    private final String message;
    private final String errorCode;
    private final Map<String, Object> extensions;

    public CustomGraphQLError(String message, ErrorCodes errorCode, Map<String, Object> extensions) {
        this.message = message;
        this.errorCode = errorCode.name();
        this.extensions = extensions;
    }

    public CustomGraphQLError(String message, ErrorCodes errorCode) {
        this(message, errorCode, Collections.emptyMap());
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return Collections.emptyList();
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorClassification.errorClassification("DataFetchingException");
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

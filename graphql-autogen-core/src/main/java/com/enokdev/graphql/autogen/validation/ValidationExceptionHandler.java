package com.enokdev.graphql.autogen.validation;

import com.enokdev.graphql.autogen.error.ValidationError;

import java.util.List;

/**
 * Interface for handling validation exceptions and converting them into a list of ValidationErrors.
 */
public interface ValidationExceptionHandler {

    /**
     * Handles a given exception and extracts validation errors from it.
     * @param ex The exception to handle.
     * @return A list of ValidationError objects.
     */
    List<ValidationError> handle(Exception ex);

    /**
     * Default implementation that returns an empty list.
     */
    class DefaultValidationExceptionHandler implements ValidationExceptionHandler {
        @Override
        public List<ValidationError> handle(Exception ex) {
            return List.of();
        }
    }
}

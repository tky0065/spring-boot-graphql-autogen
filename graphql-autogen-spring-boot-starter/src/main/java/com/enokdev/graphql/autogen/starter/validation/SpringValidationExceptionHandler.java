package com.enokdev.graphql.autogen.starter.validation;

import com.enokdev.graphql.autogen.error.ErrorCodes;
import com.enokdev.graphql.autogen.error.ValidationError;
import com.enokdev.graphql.autogen.validation.ValidationExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles Spring's MethodArgumentNotValidException and converts it into a list of ValidationErrors.
 */
@Component
public class SpringValidationExceptionHandler implements ValidationExceptionHandler {

    @Override
    public List<ValidationError> handle(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) ex;
            return validationException.getBindingResult().getAllErrors().stream()
                    .map(error -> {
                        String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                        return new ValidationError(fieldName, error.getDefaultMessage(), ErrorCodes.VALIDATION_ERROR);
                    })
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

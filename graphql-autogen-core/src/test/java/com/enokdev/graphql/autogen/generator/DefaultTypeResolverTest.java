package com.enokdev.graphql.autogen.generator;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.autogen.exception.TypeResolutionException;
import graphql.Scalars;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tests for DefaultTypeResolver.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
class DefaultTypeResolverTest {
    
    private DefaultTypeResolver typeResolver;
    
    @BeforeEach
    void setUp() {
        typeResolver = new DefaultTypeResolver();
    }
    
    @Test
    @DisplayName("Should resolve basic scalar types")
    void shouldResolveBasicScalarTypes() {
        // When & Then
        assertThat(typeResolver.resolveType(String.class)).isEqualTo(Scalars.GraphQLString);
        assertThat(typeResolver.resolveType(Integer.class)).isEqualTo(Scalars.GraphQLInt);
        assertThat(typeResolver.resolveType(int.class)).isEqualTo(Scalars.GraphQLInt);
        assertThat(typeResolver.resolveType(Long.class)).isEqualTo(Scalars.GraphQLBigInteger);
        assertThat(typeResolver.resolveType(Boolean.class)).isEqualTo(Scalars.GraphQLBoolean);
        assertThat(typeResolver.resolveType(Float.class)).isEqualTo(Scalars.GraphQLFloat);
        assertThat(typeResolver.resolveType(Double.class)).isEqualTo(Scalars.GraphQLFloat);
        assertThat(typeResolver.resolveType(BigDecimal.class)).isEqualTo(Scalars.GraphQLBigDecimal);
    }
    
    @Test
    @DisplayName("Should resolve custom scalar types")
    void shouldResolveCustomScalarTypes() {
        // When
        GraphQLType localDateTimeType = typeResolver.resolveType(LocalDateTime.class);
        GraphQLType uuidType = typeResolver.resolveType(UUID.class);
        
        // Then
        assertThat(localDateTimeType).isInstanceOf(GraphQLScalarType.class);
        assertThat(localDateTimeType.getName()).isEqualTo("DateTime");
        
        assertThat(uuidType).isInstanceOf(GraphQLScalarType.class);
        assertThat(uuidType.getName()).isEqualTo("ID");
    }
    
    @Test
    @DisplayName("Should resolve GraphQL annotated types")
    void shouldResolveGraphQLAnnotatedTypes() {
        // When
        GraphQLType bookType = typeResolver.resolveType(TestBook.class);
        GraphQLType inputType = typeResolver.resolveType(TestBookInput.class);
        GraphQLType enumType = typeResolver.resolveType(TestStatus.class);
        
        // Then
        assertThat(bookType).isInstanceOf(GraphQLObjectType.class);
        assertThat(bookType.getName()).isEqualTo("Book");
        
        assertThat(inputType).isInstanceOf(GraphQLInputObjectType.class);
        assertThat(inputType.getName()).isEqualTo("BookInput");
        
        assertThat(enumType).isInstanceOf(GraphQLEnumType.class);
        assertThat(enumType.getName()).isEqualTo("Status");
    }
    
    @Test
    @DisplayName("Should resolve array types")
    void shouldResolveArrayTypes() {
        // When
        GraphQLType stringArrayType = typeResolver.resolveType(String[].class);
        GraphQLType intArrayType = typeResolver.resolveType(int[].class);
        
        // Then
        assertThat(stringArrayType).isInstanceOf(GraphQLList.class);
        assertThat(intArrayType).isInstanceOf(GraphQLList.class);
    }
    
    @Test
    @DisplayName("Should handle type caching")
    void shouldHandleTypeCaching() {
        // When
        GraphQLType firstCall = typeResolver.resolveType(String.class);
        GraphQLType secondCall = typeResolver.resolveType(String.class);
        
        // Then
        assertThat(firstCall).isSameAs(secondCall);
    }
    
    @Test
    @DisplayName("Should register custom type mappings")
    void shouldRegisterCustomTypeMappings() {
        // Given
        class CustomType {}
        
        // When
        typeResolver.registerTypeMapping(CustomType.class, "CustomScalar");
        GraphQLType resolvedType = typeResolver.resolveType(CustomType.class);
        
        // Then
        assertThat(resolvedType).isInstanceOf(GraphQLScalarType.class);
        assertThat(resolvedType.getName()).isEqualTo("CustomScalar");
    }
    
    @Test
    @DisplayName("Should check if types can be resolved")
    void shouldCheckIfTypesCanBeResolved() {
        // When & Then
        assertThat(typeResolver.canResolve(String.class)).isTrue();
        assertThat(typeResolver.canResolve(TestBook.class)).isTrue();
        assertThat(typeResolver.canResolve(TestStatus.class)).isTrue();
        assertThat(typeResolver.canResolve(Object.class)).isFalse(); // No annotations
    }
    
    @Test
    @DisplayName("Should throw exception for unresolvable types")
    void shouldThrowExceptionForUnresolvableTypes() {
        // When & Then
        assertThatThrownBy(() -> typeResolver.resolveType(Object.class))
            .isInstanceOf(TypeResolutionException.class)
            .hasMessageContaining("Cannot resolve Java type to GraphQL type");
    }
    
    @Test
    @DisplayName("Should throw exception for null types")
    void shouldThrowExceptionForNullTypes() {
        // When & Then
        assertThatThrownBy(() -> typeResolver.resolveType((Class<?>) null))
            .isInstanceOf(TypeResolutionException.class)
            .hasMessageContaining("Java type cannot be null");
    }
    
    // === Test Classes ===
    
    @GraphQLType(name = "Book", description = "A test book")
    static class TestBook {
        @GraphQLId
        private Long id;
        
        @GraphQLField
        private String title;
        
        @GraphQLField
        private TestStatus status;
    }
    
    @GraphQLInput(name = "BookInput", description = "Input for book")
    static class TestBookInput {
        @GraphQLInputField(required = true)
        private String title;
        
        @GraphQLInputField
        private TestStatus status;
    }
    
    @GraphQLEnum(name = "Status", description = "Test status enum")
    enum TestStatus {
        @GraphQLEnumValue(description = "Active status")
        ACTIVE,
        
        @GraphQLEnumValue(description = "Inactive status")
        INACTIVE,
        
        @GraphQLIgnore
        DELETED
    }
}

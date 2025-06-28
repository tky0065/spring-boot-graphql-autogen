package com.enokdev.example.library.dto;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.validation.constraints.*;

/**
 * Input DTO for creating a new author.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@GraphQLInput(name = "CreateAuthorInput", description = "Input for creating a new author")
public class CreateAuthorInput {
    
    @GraphQLInputField(required = true, description = "First name of the author")
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    
    @GraphQLInputField(required = true, description = "Last name of the author")
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    
    @GraphQLInputField(description = "Email address of the author")
    @Email(message = "Email must be valid")
    private String email;
    
    @GraphQLInputField(description = "Biography of the author")
    @Size(max = 5000, message = "Biography must not exceed 5000 characters")
    private String biography;
    
    @GraphQLInputField(description = "Birth year of the author")
    @Min(value = 1900, message = "Birth year must be reasonable")
    @Max(value = 2010, message = "Birth year cannot be too recent")
    private Integer birthYear;
    
    // Constructors
    public CreateAuthorInput() {}
    
    public CreateAuthorInput(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
}

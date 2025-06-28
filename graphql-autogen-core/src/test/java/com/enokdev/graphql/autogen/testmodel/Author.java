package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLField;
import com.enokdev.graphql.autogen.annotation.GraphQLType;

/**
 * Author entity that implements GraphQL interfaces.
 * Used in union type demonstrations.
 */
@GraphQLType(name = "Author", description = "An author that implements Node and Searchable interfaces")
public class Author implements Node, Searchable {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String name;
    private String biography;
    private Integer booksCount;
    private Double searchScore;

    /**
     * The full name of the author.
     */
    @GraphQLField(description = "The author's full name")
    public String getName() {
        return name;
    }

    /**
     * The author's biography.
     */
    @GraphQLField(description = "The author's biography")
    public String getBiography() {
        return biography;
    }

    /**
     * The number of books written by this author.
     */
    @GraphQLField(description = "Number of books written")
    public Integer getBooksCount() {
        return booksCount;
    }

    // Implementation of Node interface
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getUpdatedAt() {
        return updatedAt;
    }

    // Implementation of Searchable interface
    @Override
    public Double getSearchScore() {
        return searchScore;
    }

    @Override
    public String getDisplayText() {
        return name + " (" + booksCount + " books)";
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setBooksCount(Integer booksCount) {
        this.booksCount = booksCount;
    }

    public void setSearchScore(Double searchScore) {
        this.searchScore = searchScore;
    }
}

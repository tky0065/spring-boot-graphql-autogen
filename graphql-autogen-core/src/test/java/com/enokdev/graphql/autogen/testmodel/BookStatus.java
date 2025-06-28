package com.enokdev.graphql.autogen.testmodel;

import com.enokdev.graphql.autogen.annotation.GraphQLEnum;
import com.enokdev.graphql.autogen.annotation.GraphQLEnumValue;
import com.enokdev.graphql.autogen.annotation.GraphQLIgnore;

/**
 * Represents the current status of a book in the library system.
 * This enum helps track the availability and condition of books.
 */
@GraphQLEnum(name = "BookStatus")
public enum BookStatus {

    /**
     * The book is available for borrowing.
     * It's currently on the shelf and in good condition.
     */
    @GraphQLEnumValue(description = "Book is available for checkout")
    AVAILABLE,

    /**
     * The book is currently borrowed by a library member.
     * It should be returned by the due date.
     */
    @GraphQLEnumValue(description = "Book is currently checked out")
    BORROWED,

    /**
     * The book is reserved for a specific member.
     * It cannot be borrowed by others until the reservation expires.
     */
    @GraphQLEnumValue(description = "Book is reserved for a member")
    RESERVED,

    /**
     * The book is damaged and needs repair.
     * It cannot be borrowed until it's fixed.
     */
    @GraphQLEnumValue(description = "Book is damaged and under repair")
    DAMAGED,

    /**
     * The book has been lost or stolen.
     * It's no longer available in the library's collection.
     */
    @GraphQLEnumValue(description = "Book is lost or missing")
    LOST,

    /**
     * Internal status used for system processing.
     * This status is not exposed in the public API.
     */
    @GraphQLIgnore
    INTERNAL_PROCESSING
}

package com.enokdev.graphql.examples.blog.controller;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.examples.blog.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur GraphQL pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@Component
@GraphQLController(prefix = "blog")
public class BlogGraphQLController {

    // ========== QUERIES ==========

    @GraphQLQuery(name = "posts")
    public Page<Post> getPosts(
            @GraphQLArgument(name = "page", required = false) Integer page,
            @GraphQLArgument(name = "size", required = false) Integer size) {
        
        Pageable pageable = PageRequest.of(
            page != null ? page : 0, 
            size != null ? size : 10,
            Sort.by(Sort.Direction.DESC, "publishedAt")
        );
        // Service call would be here in real implementation
        return Page.empty(pageable);
    }

    @GraphQLQuery(name = "postBySlug")
    public Optional<Post> getPostBySlug(
            @GraphQLArgument(name = "slug", required = true) String slug) {
        // Service call would be here
        return Optional.empty();
    }

    @GraphQLQuery(name = "authors")
    public List<Author> getAuthors() {
        // Service call would be here
        return List.of();
    }

    @GraphQLQuery(name = "categories")
    public List<Category> getCategories() {
        // Service call would be here
        return List.of();
    }

    // ========== MUTATIONS ==========

    @GraphQLMutation(name = "createPost")
    public Post createPost(
            @GraphQLArgument(name = "input", required = true) CreatePostInput input) {
        // Service call would be here
        return new Post();
    }

    @GraphQLMutation(name = "addComment")
    public Comment addComment(
            @GraphQLArgument(name = "input", required = true) CreateCommentInput input) {
        // Service call would be here
        return new Comment();
    }

    // ========== SUBSCRIPTIONS ==========

    @GraphQLSubscription(name = "newPostPublished")
    public Post newPostPublished() {
        return null; // Implementation with WebSockets
    }
}

// Input classes
@GraphQLInput(name = "CreatePostInput")
class CreatePostInput {
    private String title;
    private String content;
    private Long authorId;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

@GraphQLInput(name = "CreateCommentInput")
class CreateCommentInput {
    private String content;
    private Long postId;
    private String authorName;
    private String authorEmail;
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
}

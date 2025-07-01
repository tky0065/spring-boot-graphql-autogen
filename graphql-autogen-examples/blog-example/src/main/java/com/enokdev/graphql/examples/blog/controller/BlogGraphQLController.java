package com.enokdev.graphql.examples.blog.controller;

import com.enokdev.graphql.autogen.annotation.*;
import com.enokdev.graphql.examples.blog.dto.CreateCommentInput;
import com.enokdev.graphql.examples.blog.dto.CreatePostInput;
import com.enokdev.graphql.examples.blog.entity.*;
import com.enokdev.graphql.examples.blog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur GraphQL pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@Component
@GraphQLController(prefix = "blog")
public class BlogGraphQLController {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TagRepository tagRepository;

    // ========== QUERIES ==========

    @GraphQLQuery(name = "posts")
    @GraphQLPagination(type = GraphQLPagination.PaginationType.OFFSET)
    public Page<Post> getPosts(
            @GraphQLArgument(name = "page", required = false) Integer page,
            @GraphQLArgument(name = "size", required = false) Integer size) {
        
        Pageable pageable = PageRequest.of(
            page != null ? page : 0, 
            size != null ? size : 10,
            Sort.by(Sort.Direction.DESC, "publishedAt")
        );
        return postRepository.findAll(pageable);
    }

    @GraphQLQuery(name = "postBySlug")
    public Optional<Post> getPostBySlug(
            @GraphQLArgument(name = "slug", required = true) String slug) {
        return postRepository.findBySlug(slug);
    }

    @GraphQLQuery(name = "authors")
    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    @GraphQLQuery(name = "categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    // ========== MUTATIONS ==========

    @GraphQLMutation(name = "createPost")
    public Post createPost(
            @GraphQLArgument(name = "input", required = true) CreatePostInput input) {
        Author author = authorRepository.findById(input.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Post post = new Post(input.getTitle(), input.getContent(), author);
        return postRepository.save(post);
    }

    @GraphQLMutation(name = "addComment")
    public Comment addComment(
            @GraphQLArgument(name = "input", required = true) CreateCommentInput input) {
        Post post = postRepository.findById(input.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Author author = authorRepository.findById(input.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Comment comment = new Comment(input.getContent(), post, author);
        return commentRepository.save(comment);
    }

    // ========== SUBSCRIPTIONS ==========

    @GraphQLSubscription(name = "newPostPublished")
    public Post newPostPublished() {
        return null; // Implementation with WebSockets
    }
}

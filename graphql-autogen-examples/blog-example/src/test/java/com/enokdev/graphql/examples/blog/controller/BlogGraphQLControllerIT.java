
package com.enokdev.graphql.examples.blog.controller;

import com.enokdev.graphql.examples.blog.entity.Author;
import com.enokdev.graphql.examples.blog.entity.Category;
import com.enokdev.graphql.examples.blog.entity.Comment;
import com.enokdev.graphql.examples.blog.entity.Post;
import com.enokdev.graphql.examples.blog.entity.AuthorStatus;
import com.enokdev.graphql.examples.blog.entity.CommentStatus;
import com.enokdev.graphql.examples.blog.entity.PostStatus;
import com.enokdev.graphql.examples.blog.repository.*;
import com.enokdev.graphql.examples.blog.dto.CreatePostInput;
import com.enokdev.graphql.examples.blog.dto.CreateCommentInput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlTest(BlogGraphQLController.class)
@Import({AuthorRepository.class, CategoryRepository.class, PostRepository.class, CommentRepository.class, TagRepository.class})
class BlogGraphQLControllerIT {

    @Autowired
    private GraphQlTester graphQlTester;

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

    private Author author1;
    private Author author2;
    private Category techCategory;
    private Category travelCategory;
    private Post post1;
    private Post post2;
    private Comment comment1;

    @BeforeEach
    void setUp() {
        // Clear all repositories before each test
        commentRepository.deleteAll();
        postRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
        tagRepository.deleteAll();

        // Create Authors
        author1 = new Author("John", "Doe", "john.doe@example.com", "johndoe");
        author1.setBio("Passionate writer and tech enthusiast.");
        author1.setStatus(AuthorStatus.ACTIVE);
        authorRepository.save(author1);

        author2 = new Author("Jane", "Smith", "jane.smith@example.com", "janesmith");
        author2.setBio("Loves to write about nature and travel.");
        author2.setStatus(AuthorStatus.ACTIVE);
        authorRepository.save(author2);

        // Create Categories
        techCategory = new Category("Technology", "Articles about latest tech trends");
        techCategory.setSlug("technology");
        categoryRepository.save(techCategory);

        travelCategory = new Category("Travel", "Explore the world with us");
        travelCategory.setSlug("travel");
        categoryRepository.save(travelCategory);

        // Create Posts
        post1 = new Post("Getting Started with GraphQL", "This is the content of the first post.", author1);
        post1.setCategory(techCategory);
        post1.setStatus(PostStatus.PUBLISHED);
        post1.setPublishedAt(LocalDateTime.now().minusDays(5));
        postRepository.save(post1);

        post2 = new Post("My Adventure in the Alps", "This is the content of the second post.", author2);
        post2.setCategory(travelCategory);
        post2.setStatus(PostStatus.PUBLISHED);
        post2.setPublishedAt(LocalDateTime.now().minusDays(10));
        postRepository.save(post2);

        // Create Comments
        comment1 = new Comment("Great article!", post1, author2);
        comment1.setStatus(CommentStatus.APPROVED);
        commentRepository.save(comment1);
    }

    @Test
    void testGetPosts() {
        this.graphQlTester.documentName("posts")
                .execute()
                .path("posts.content")
                .entityList(Post.class)
                .hasSize(2)
                .contains(post1, post2);
    }

    @Test
    void testGetPostBySlug() {
        this.graphQlTester.documentName("postBySlug")
                .variable("slug", post1.getSlug())
                .execute()
                .path("postBySlug.title").entity(String.class).isEqualTo(post1.getTitle());
    }

    @Test
    void testGetAuthors() {
        this.graphQlTester.documentName("authors")
                .execute()
                .path("authors")
                .entityList(Author.class)
                .hasSize(2)
                .contains(author1, author2);
    }

    @Test
    void testGetCategories() {
        this.graphQlTester.documentName("categories")
                .execute()
                .path("categories")
                .entityList(Category.class)
                .hasSize(2)
                .contains(techCategory, travelCategory);
    }

    @Test
    void testCreatePost() {
        CreatePostInput input = new CreatePostInput();
        input.setTitle("New Test Post");
        input.setContent("Content of the new test post.");
        input.setAuthorId(author1.getId());

        this.graphQlTester.documentName("createPost")
                .variable("input", input)
                .execute()
                .path("createPost.title").entity(String.class).isEqualTo("New Test Post");

        assertThat(postRepository.count()).isEqualTo(3);
    }

    @Test
    void testAddComment() {
        CreateCommentInput input = new CreateCommentInput();
        input.setContent("A new comment.");
        input.setPostId(post1.getId());
        input.setAuthorId(author2.getId());

        this.graphQlTester.documentName("addComment")
                .variable("input", input)
                .execute()
                .path("addComment.content").entity(String.class).isEqualTo("A new comment.");

        assertThat(commentRepository.count()).isEqualTo(2);
    }
}


package com.enokdev.graphql.examples.blog.config;

import com.enokdev.graphql.examples.blog.entity.*;
import com.enokdev.graphql.examples.blog.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    public DataLoader(AuthorRepository authorRepository,
                      CategoryRepository categoryRepository,
                      PostRepository postRepository,
                      CommentRepository commentRepository,
                      TagRepository tagRepository) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create Authors
        Author author1 = new Author("John", "Doe", "john.doe@example.com", "johndoe");
        author1.setBio("Passionate writer and tech enthusiast.");
        author1.setStatus(AuthorStatus.ACTIVE);
        authorRepository.save(author1);

        Author author2 = new Author("Jane", "Smith", "jane.smith@example.com", "janesmith");
        author2.setBio("Loves to write about nature and travel.");
        author2.setStatus(AuthorStatus.ACTIVE);
        authorRepository.save(author2);

        // Create Categories
        Category techCategory = new Category("Technology", "Articles about latest tech trends");
        techCategory.setSlug("technology");
        categoryRepository.save(techCategory);

        Category travelCategory = new Category("Travel", "Explore the world with us");
        travelCategory.setSlug("travel");
        categoryRepository.save(travelCategory);

        // Create Tags
        Tag javaTag = new Tag("Java");
        Tag graphqlTag = new Tag("GraphQL");
        Tag springTag = new Tag("Spring Boot");
        Tag adventureTag = new Tag("Adventure");
        Tag natureTag = new Tag("Nature");

        tagRepository.saveAll(List.of(javaTag, graphqlTag, springTag, adventureTag, natureTag));

        // Create Posts
        Post post1 = new Post("Getting Started with GraphQL", "This is the content of the first post.", author1);
        post1.setCategory(techCategory);
        post1.setStatus(PostStatus.PUBLISHED);
        post1.setPublishedAt(LocalDateTime.now().minusDays(5));
        post1.setTags(Set.of(graphqlTag, springTag));
        postRepository.save(post1);

        Post post2 = new Post("My Adventure in the Alps", "This is the content of the second post.", author2);
        post2.setCategory(travelCategory);
        post2.setStatus(PostStatus.PUBLISHED);
        post2.setPublishedAt(LocalDateTime.now().minusDays(10));
        post2.setTags(Set.of(adventureTag, natureTag));
        postRepository.save(post2);

        Post post3 = new Post("Spring Boot Best Practices", "Content for Spring Boot best practices.", author1);
        post3.setCategory(techCategory);
        post3.setStatus(PostStatus.DRAFT);
        postRepository.save(post3);

        // Create Comments
        Comment comment1 = new Comment("Great article!", post1, author2);
        comment1.setStatus(CommentStatus.APPROVED);
        commentRepository.save(comment1);

        Comment comment2 = new Comment("Very insightful.", post1, author1);
        comment2.setStatus(CommentStatus.PENDING);
        commentRepository.save(comment2);

        System.out.println("Blog example data loaded!");
    }
}

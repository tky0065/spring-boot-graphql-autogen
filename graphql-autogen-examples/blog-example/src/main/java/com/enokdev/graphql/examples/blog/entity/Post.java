package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité Post pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 * 
 * Démontre l'utilisation des annotations GraphQL sur une entité JPA représentant un article de blog.
 */
@Entity
@Table(name = "posts")
@GraphQLType(name = "Post", description = "Article de blog avec contenu riche et métadonnées")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    @GraphQLField(description = "Titre de l'article", nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le slug est obligatoire")
    @GraphQLField(description = "Slug unique pour l'URL de l'article", nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "L'extrait ne peut pas dépasser 500 caractères")
    @GraphQLField(description = "Extrait ou résumé de l'article")
    private String excerpt;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    @NotBlank(message = "Le contenu est obligatoire")
    @GraphQLField(description = "Contenu complet de l'article en Markdown", nullable = false)
    private String content;

    @Column(name = "featured_image")
    @GraphQLField(description = "URL de l'image mise en avant")
    private String featuredImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @GraphQLField(description = "Statut de publication de l'article", nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "view_count", nullable = false)
    @GraphQLField(description = "Nombre de vues de l'article", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    @GraphQLField(description = "Nombre de likes sur l'article", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "reading_time")
    @GraphQLField(description = "Temps de lecture estimé en minutes")
    private Integer readingTime;

    @Column(name = "published_at")
    @GraphQLField(description = "Date de publication de l'article")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @GraphQLField(description = "Date de création de l'article", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @GraphQLField(description = "Date de dernière modification", nullable = false)
    private LocalDateTime updatedAt;

    // Relations

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @GraphQLField(description = "Auteur de l'article", nullable = false)
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @GraphQLField(description = "Catégorie principale de l'article")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @GraphQLField(description = "Tags associés à l'article")
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @GraphQLField(description = "Commentaires sur cet article")
    private List<Comment> comments = new ArrayList<>();

    // Constructeurs

    public Post() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Post(String title, String content, Author author) {
        this();
        this.title = title;
        this.content = content;
        this.author = author;
        this.slug = generateSlug(title);
        this.readingTime = calculateReadingTime(content);
    }

    // Méthodes calculées (seront exposées automatiquement en GraphQL)

    /**
     * Nombre total de commentaires
     */
    @GraphQLField(description = "Nombre total de commentaires sur cet article")
    public Long getCommentsCount() {
        return (long) comments.size();
    }

    /**
     * Commentaires approuvés uniquement
     */
    @GraphQLField(description = "Commentaires approuvés sur cet article")
    public List<Comment> getApprovedComments() {
        return comments.stream()
                .filter(comment -> comment.getStatus() == CommentStatus.APPROVED)
                .toList();
    }

    /**
     * Vérifie si l'article est publié
     */
    @GraphQLField(description = "Indique si l'article est publié")
    public Boolean isPublished() {
        return status == PostStatus.PUBLISHED && publishedAt != null;
    }

    /**
     * Vérifie si l'article est récent (moins de 7 jours)
     */
    @GraphQLField(description = "Indique si l'article a été publié récemment (moins de 7 jours)")
    public Boolean isRecent() {
        if (publishedAt == null) return false;
        return publishedAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * URL complète de l'article
     */
    @GraphQLField(description = "URL complète de l'article")
    public String getUrl() {
        return "/blog/" + slug;
    }

    /**
     * Prévisualisation du contenu
     */
    @GraphQLField(description = "Prévisualisation du contenu (100 premiers caractères)")
    public String getContentPreview() {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    // Méthodes utilitaires

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private Integer calculateReadingTime(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        int wordsPerMinute = 200;
        int wordCount = content.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil((double) wordCount / wordsPerMinute));
    }

    // Callbacks JPA

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.readingTime = calculateReadingTime(this.content);
        
        if (this.status == PostStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.slug == null && this.title != null) {
            this.slug = generateSlug(this.title);
        }
        this.readingTime = calculateReadingTime(this.content);
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(Integer readingTime) {
        this.readingTime = readingTime;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Méthodes utilitaires pour les relations

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return id != null && id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", status=" + status +
                ", viewCount=" + viewCount +
                ", publishedAt=" + publishedAt +
                '}';
    }
}

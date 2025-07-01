package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Tag pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@Entity
@Table(name = "tags")
@GType(name = "Tag", description = "Tag pour organiser et filtrer les articles")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom du tag est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @GraphQLField(description = "Nom du tag", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le slug est obligatoire")
    @GraphQLField(description = "Slug unique pour l'URL du tag", nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    @Size(max = 300, message = "La description ne peut pas dépasser 300 caractères")
    @GraphQLField(description = "Description du tag")
    private String description;

    @Column(name = "color_code")
    @GraphQLField(description = "Code couleur hexadécimal pour l'affichage du tag")
    private String colorCode;

    @Column(name = "is_active", nullable = false)
    @GraphQLField(description = "Indique si le tag est actif", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @GraphQLField(description = "Date de création du tag", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @GraphQLField(description = "Date de dernière modification")
    private LocalDateTime updatedAt;

    // Relations

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @GraphQLField(description = "Articles associés à ce tag")
    private Set<Post> posts = new HashSet<>();

    // Constructeurs

    public Tag() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Tag(String name) {
        this();
        this.name = name;
        this.slug = generateSlug(name);
    }

    public Tag(String name, String description) {
        this(name);
        this.description = description;
    }

    // Méthodes calculées

    @GraphQLField(description = "Nombre d'articles utilisant ce tag")
    public Long getPostCount() {
        return posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .count();
    }

    @GraphQLField(description = "Popularité du tag basée sur le nombre d'articles et de vues")
    public Double getPopularityScore() {
        if (posts.isEmpty()) {
            return 0.0;
        }
        
        long totalViews = posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .mapToLong(Post::getViewCount)
                .sum();
                
        return (double) totalViews / posts.size();
    }

    @GraphQLField(description = "Indique si le tag est populaire (plus de 5 articles)")
    public Boolean isPopular() {
        return getPostCount() > 5;
    }

    @GraphQLField(description = "URL complète du tag")
    public String getUrl() {
        return "/tag/" + slug;
    }

    @GraphQLField(description = "Articles récents avec ce tag (derniers 30 jours)")
    public Set<Post> getRecentPosts() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .filter(post -> post.getPublishedAt() != null && post.getPublishedAt().isAfter(thirtyDaysAgo))
                .collect(java.util.stream.Collectors.toSet());
    }

    // Méthodes utilitaires

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.slug == null && this.name != null) {
            this.slug = generateSlug(this.name);
        }
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    // Méthodes utilitaires pour les relations

    public void addPost(Post post) {
        posts.add(post);
        post.getTags().add(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.getTags().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id != null && id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", isActive=" + isActive +
                ", postCount=" + posts.size() +
                '}';
    }
}

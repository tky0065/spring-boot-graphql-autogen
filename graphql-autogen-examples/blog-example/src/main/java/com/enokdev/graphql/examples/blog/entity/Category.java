package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Category pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@Entity
@Table(name = "categories")
@GraphQLType(name = "Category", description = "Catégorie d'articles de blog")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @GraphQLField(description = "Nom de la catégorie", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le slug est obligatoire")
    @GraphQLField(description = "Slug unique pour l'URL de la catégorie", nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @GraphQLField(description = "Description de la catégorie")
    private String description;

    @Column(name = "color_code")
    @GraphQLField(description = "Code couleur hexadécimal pour l'affichage")
    private String colorCode;

    @Column(name = "icon_name")
    @GraphQLField(description = "Nom de l'icône représentant la catégorie")
    private String iconName;

    @Column(name = "display_order")
    @GraphQLField(description = "Ordre d'affichage de la catégorie")
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @GraphQLField(description = "Indique si la catégorie est active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @GraphQLField(description = "Date de création de la catégorie", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @GraphQLField(description = "Date de dernière modification")
    private LocalDateTime updatedAt;

    // Relations

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @GraphQLField(description = "Articles dans cette catégorie")
    private List<Post> posts = new ArrayList<>();

    // Parent-enfant pour catégories hiérarchiques
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @GraphQLField(description = "Catégorie parente")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @GraphQLField(description = "Sous-catégories")
    private List<Category> children = new ArrayList<>();

    // Constructeurs

    public Category() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
        this.slug = generateSlug(name);
    }

    // Méthodes calculées

    @GraphQLField(description = "Nombre total d'articles dans cette catégorie")
    public Long getPostCount() {
        return posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .count();
    }

    @GraphQLField(description = "Nombre total d'articles incluant les sous-catégories")
    public Long getTotalPostCount() {
        long count = getPostCount();
        for (Category child : children) {
            count += child.getTotalPostCount();
        }
        return count;
    }

    @GraphQLField(description = "Indique si cette catégorie a des sous-catégories")
    public Boolean hasChildren() {
        return !children.isEmpty();
    }

    @GraphQLField(description = "Chemin complet de la catégorie (parent/enfant)")
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " / " + name;
    }

    @GraphQLField(description = "URL complète de la catégorie")
    public String getUrl() {
        return "/category/" + slug;
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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    // Méthodes utilitaires pour les relations

    public void addPost(Post post) {
        posts.add(post);
        post.setCategory(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setCategory(null);
    }

    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

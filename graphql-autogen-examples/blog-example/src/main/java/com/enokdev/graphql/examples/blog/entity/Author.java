package com.enokdev.graphql.examples.blog.entity;

import com.enokdev.graphql.autogen.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Author pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 * 
 * Démontre l'utilisation des annotations GraphQL sur une entité JPA représentant un auteur.
 */
@Entity
@Table(name = "authors")
@GraphQLType(name = "Author", description = "Auteur de blog avec profil complet")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @GraphQLField(description = "Prénom de l'auteur", nullable = false)
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @GraphQLField(description = "Nom de famille de l'auteur", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @GraphQLField(description = "Adresse email unique de l'auteur", nullable = false)
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 30, message = "Le nom d'utilisateur doit contenir entre 3 et 30 caractères")
    @GraphQLField(description = "Nom d'utilisateur unique pour l'auteur", nullable = false)
    private String username;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "La biographie ne peut pas dépasser 1000 caractères")
    @GraphQLField(description = "Biographie de l'auteur")
    private String bio;

    @Column(name = "avatar_url")
    @GraphQLField(description = "URL de l'avatar de l'auteur")
    private String avatarUrl;

    @Column(name = "website_url")
    @GraphQLField(description = "Site web personnel de l'auteur")
    private String websiteUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @GraphQLField(description = "Statut actuel de l'auteur", nullable = false)
    private AuthorStatus status = AuthorStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @GraphQLField(description = "Date de création du profil auteur", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @GraphQLField(description = "Date de dernière mise à jour du profil")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    @GraphQLField(description = "Date de dernière connexion")
    private LocalDateTime lastLogin;

    // Relations

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @GraphQLField(description = "Articles écrits par cet auteur")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @GraphQLField(description = "Commentaires écrits par cet auteur")
    private List<Comment> comments = new ArrayList<>();

    // Constructeurs

    public Author() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Author(String firstName, String lastName, String email, String username) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
    }

    // Méthodes calculées (seront exposées automatiquement en GraphQL)

    /**
     * Nom complet de l'auteur
     */
    @GraphQLField(description = "Nom complet de l'auteur (prénom + nom)")
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Nombre total d'articles publiés
     */
    @GraphQLField(description = "Nombre total d'articles publiés par cet auteur")
    public Long getPublishedPostsCount() {
        return posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .count();
    }

    /**
     * Nombre total de commentaires
     */
    @GraphQLField(description = "Nombre total de commentaires écrits par cet auteur")
    public Long getCommentsCount() {
        return (long) comments.size();
    }

    /**
     * Vérifie si l'auteur est actif
     */
    @GraphQLField(description = "Indique si l'auteur est actuellement actif")
    public Boolean isActive() {
        return status == AuthorStatus.ACTIVE;
    }

    // Callbacks JPA

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public AuthorStatus getStatus() {
        return status;
    }

    public void setStatus(AuthorStatus status) {
        this.status = status;
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

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Méthodes utilitaires

    public void addPost(Post post) {
        posts.add(post);
        post.setAuthor(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setAuthor(null);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setAuthor(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setAuthor(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return id != null && id.equals(author.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}

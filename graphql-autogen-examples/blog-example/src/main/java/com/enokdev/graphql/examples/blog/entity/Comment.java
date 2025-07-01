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
 * Entité Comment pour l'exemple Blog
 * Phase 9 : Documentation et exemples - Exemple API de blog
 */
@Entity
@Table(name = "comments")
@GType(name = "Comment", description = "Commentaire sur un article de blog avec système de réponses")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLId
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(min = 5, max = 2000, message = "Le commentaire doit contenir entre 5 et 2000 caractères")
    @GraphQLField(description = "Contenu du commentaire", nullable = false)
    private String content;

    @Column(name = "author_name")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @GraphQLField(description = "Nom de l'auteur du commentaire (pour les utilisateurs non-inscrits)")
    private String authorName;

    @Column(name = "author_email")
    @Email(message = "Format d'email invalide")
    @GraphQLField(description = "Email de l'auteur du commentaire (pour les utilisateurs non-inscrits)")
    private String authorEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @GraphQLField(description = "Statut de modération du commentaire", nullable = false)
    private CommentStatus status = CommentStatus.PENDING;

    @Column(name = "like_count", nullable = false)
    @GraphQLField(description = "Nombre de likes sur le commentaire", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    @GraphQLField(description = "Date de création du commentaire", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @GraphQLField(description = "Date de dernière modification")
    private LocalDateTime updatedAt;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @GraphQLField(description = "Article commenté", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @GraphQLField(description = "Auteur inscrit du commentaire (si applicable)")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @GraphQLField(description = "Commentaire parent (pour les réponses)")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @GraphQLField(description = "Réponses à ce commentaire")
    private List<Comment> replies = new ArrayList<>();

    // Constructeurs
    public Comment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Comment(String content, Post post) {
        this();
        this.content = content;
        this.post = post;
    }

    // Méthodes calculées
    @GraphQLField(description = "Nom d'affichage de l'auteur du commentaire")
    public String getDisplayName() {
        if (author != null) {
            return author.getFullName();
        }
        return authorName != null ? authorName : "Anonyme";
    }

    @GraphQLField(description = "Nombre de réponses à ce commentaire")
    public Long getRepliesCount() {
        return (long) replies.size();
    }

    @GraphQLField(description = "Indique si le commentaire est approuvé")
    public Boolean isApproved() {
        return status == CommentStatus.APPROVED;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }

    public CommentStatus getStatus() { return status; }
    public void setStatus(CommentStatus status) { this.status = status; }

    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public Comment getParent() { return parent; }
    public void setParent(Comment parent) { this.parent = parent; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return id != null && id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

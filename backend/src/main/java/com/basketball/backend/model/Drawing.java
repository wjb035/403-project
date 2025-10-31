package com.basketball.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "drawings")
public class Drawing {

    // auto-generated incremental ID for each drawing stored in db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Many to one relatioship between user and drawing
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // fields
    private String imageUrl;
    private String description;
    private int likesCount;

    // unix timestamp for when the drawing was created
    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    // ACCESSORS AND MUTATORS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getLikesCount() { return likesCount; }

    public void setCreatedAt(Timestamp createdAt) { this.createdAt  = createdAt; }
    public Timestamp getCreatedAt() { return createdAt; }




}

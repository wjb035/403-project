package com.basketball.backend.model;

import jakarta.persistence.*;
import java.sql.Timestamp;


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

    // Connect the drawing to the current prompt
    @ManyToOne
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    // fields
    private String imageUrl;
    private int likesCount;

    // unix timestamp for when the drawing was created
    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    // ACCESSORS AND MUTATORS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Prompt getPrompt() { return prompt; }
    public void setPrompt(Prompt prompt) { this.prompt = prompt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }


    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getLikesCount() { return likesCount; }

    public void setCreatedAt(Timestamp createdAt) { this.createdAt  = createdAt; }
    public Timestamp getCreatedAt() { return createdAt; }




}

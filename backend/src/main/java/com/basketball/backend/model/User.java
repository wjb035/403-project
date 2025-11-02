package com.basketball.backend.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

// This class defines the User entity that maps to the "user" table in MySQL.
@Entity
@Table(name = "user")
public class User {

    // auto-generated incremental ID for each user stored in db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username must be unique and not be empty
    @Column(unique = true, nullable = false)
    private String username;
    private String bio;
    private String profilePicture;
    private String createdAt;

    // password must not be empty
    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<User> followers;

    @ManyToMany(mappedBy = "followers")
    private List<User> following = new ArrayList<>();

    // default empty constructor (required by JPA)
    public User() {}

    // custom constructor to create a user with username and password
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // accessors and mutators for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }


    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<User> getFollowers() { return followers; }
    public void setFollowers(List<User> followers) { this.followers = followers; }

    public List<User> getFollowing() { return following; }
    public void setFollowing(List<User> following) { this.following = following; }
}


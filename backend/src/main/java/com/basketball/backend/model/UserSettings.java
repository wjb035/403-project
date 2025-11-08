package com.basketball.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    // auto-generated incremental ID for each user_settings row stored in db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User that is accessing the settings
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Theme and notifaciotn settings
    private String theme = "light";
    private Boolean notificationsEnabled = true;

    // Points to the most recent follower's user entity
    @ManyToOne
    @JoinColumn(name = "most_recent_follower_id")
    private User mostRecentFollower;

    // Points to the most recent followers most recent post
    @ManyToOne
    @JoinColumn(name = "most_recent_follower_post_id")
    private Drawing mostRecentFollowerPost;

    // Points to the current users most recent post
    @ManyToOne
    @JoinColumn(name = "most_recent_liked_post_id")
    private Drawing mostRecentLikedPost;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public User getMostRecentFollower() { return mostRecentFollower; }
    public void getMostRecentFollower(User user) { this.mostRecentFollower = user; }

    public Drawing getMostRecentFollowerPost() { return mostRecentFollowerPost; }
    public void setMostRecentFollower(Drawing drawing) { this.mostRecentFollowerPost = drawing; }

    public Drawing getMostRecentLikedPost() { return mostRecentLikedPost; }
    public void setMostRecentLikedPost(Drawing drawing) { this.mostRecentLikedPost = drawing; }




}

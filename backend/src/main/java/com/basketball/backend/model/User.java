package com.basketball.backend.model;

import jakarta.persistence.*;

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

    // password must not be empty
    @Column(nullable = false)
    private String password;

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
}


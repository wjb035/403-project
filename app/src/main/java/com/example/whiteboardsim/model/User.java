package com.example.whiteboardsim.model;
// This file represents all of the data returned by the Spring Boot API
public class User {
    private String username;
    private String password;

    // Empty constructor (for Retrofit and Gson)
    public User() {}

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Accessors
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Mutators
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

}

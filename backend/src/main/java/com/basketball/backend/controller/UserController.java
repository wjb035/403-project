package com.basketball.backend.controller;

import com.basketball.backend.model.User;
import com.basketball.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow access from Android emulator or any client
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // POST /api/users/register
    // registers a new user if the username does not already exist
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Save new user to the database
        return userRepository.save(user);
    }

    // POST /api/users/login
    // Authenticates a user based on username and password.
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Find user by username
        User user = userRepository.findByUsername(username);

        // Verify credentials
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        // Return the user info if login is successful
        return user;
    }

    @PostMapping("/follow/{username}")
    public User followUser(@PathVariable String username, @RequestBody User user) {
        User currentUser = userRepository.findByUsername(username);
        User userToFollow = userRepository.findByUsername(user.getUsername());

        if (currentUser == null || userToFollow == null) {
            throw new RuntimeException("User not found");
        }

        currentUser.getFollowing().add(userToFollow);
        userToFollow.getFollowers().add(currentUser);

        userRepository.save(currentUser);
        userRepository.save(userToFollow);

        return userToFollow;
    }

    @PostMapping("/unfollow/{username}")
    public User unfollowUser(@PathVariable String username, @RequestBody User user) {
        User currentUser = userRepository.findByUsername(username);
        User userToUnfollow = userRepository.findByUsername(user.getUsername());

        if (currentUser == null || userToUnfollow == null) {
            throw new RuntimeException("User not found");
        }

        currentUser.getFollowing().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(userToUnfollow);

        return userToUnfollow;
    }
}

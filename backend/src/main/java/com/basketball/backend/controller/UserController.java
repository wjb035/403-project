package com.basketball.backend.controller;

import com.basketball.backend.model.Drawing;
import com.basketball.backend.model.Prompt;
import com.basketball.backend.model.User;
import com.basketball.backend.repository.UserRepository;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow access from Android emulator or any client
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    @GetMapping("/display")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Search by username
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH MAPPING FOR UBIO CHANGES
    public ResponseEntity<User> patchUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates
    ) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();
        User user = optionalUser.get();

        if (updates.containsKey("username")) user.setUsername(updates.get("username"));
        if (updates.containsKey("bio")) user.setBio(updates.get("bio"));

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

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

    // Upload drawings to database
    @PostMapping("/uploadProfilePicture")
    public ResponseEntity<User> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId
    ) {
        try {
            // Upload to firebase
            Bucket bucket = StorageClient.getInstance().bucket();
            // Generate a unique name for each upload
            String uniqueName = "profile_pictures/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            ;
            Blob blob = bucket.create(uniqueName, file.getBytes(), file.getContentType());

            // Generate download url
            String downloadUrl = String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    URLEncoder.encode(uniqueName, StandardCharsets.UTF_8)
            );

            // Get user
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            // Update user's profile picture URL
            user.setProfilePicture(downloadUrl);
            User savedUser = userRepository.save(user);

            return ResponseEntity.ok(savedUser);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

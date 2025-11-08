package com.basketball.backend.controller;

import com.basketball.backend.model.Drawing;
import com.basketball.backend.model.DrawingLike;
import com.basketball.backend.model.User;
import com.basketball.backend.repository.DrawingRepository;
import com.basketball.backend.repository.DrawingLikeRepository;
import com.basketball.backend.repository.UserRepository;
import com.basketball.backend.repository.PromptRepository;
import com.basketball.backend.model.Prompt;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow access from Android emulator or any client
public class DrawingController {

    @Autowired
    private DrawingRepository drawingRepository;
    @Autowired
    private DrawingLikeRepository drawingLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PromptRepository promptRepository;


    // GET all drawings for leaderboard (sorted by likes or timestamp)
    @GetMapping("/leaderboard/likes")
    public List<Drawing> getLeaderboardByLikes() {
        return drawingRepository.findAllByOrderByLikesCountDesc();
    }

    @GetMapping("/leaderboard/new")
    public List<Drawing> getLeaderboardByTimestamp() {
        return drawingRepository.findAllByOrderByCreatedAtDesc();
    }

    // stores a drawing to the user's profile
    @PostMapping("/storeDrawing")
    public Drawing storeDrawing(@RequestBody Drawing drawing) {
        return drawingRepository.save(drawing);
    }

    // Image liking functionality
    @PostMapping("/like/{drawingId}/{userId}")
    public Drawing likeDrawing(@PathVariable Long drawingId, @PathVariable Long userId) {
        Drawing drawing = drawingRepository.findById(drawingId).orElse(null);
        if (drawing == null) {
            throw new RuntimeException("Drawing not found");
        }

        // Check if the user already liked
        if (drawingLikeRepository.findByUserIdAndDrawingId(userId, drawingId).isPresent()) {
            throw new RuntimeException("User already liked this drawing");
        }

        // Record the like
        DrawingLike like = new DrawingLike();
        like.setUserId(userId);
        like.setDrawingId(drawingId);
        drawingLikeRepository.save(like);

        // Update the cached count
        drawing.setLikesCount(drawing.getLikesCount() + 1);
        return drawingRepository.save(drawing);
    }

    // Image disliking functionality
    @PostMapping("/unlike/{drawingId}/{userId}")
    public Drawing unlikeDrawing(@PathVariable Long drawingId, @PathVariable Long userId) {
        Drawing drawing = drawingRepository.findById(drawingId).orElse(null);
        if (drawing == null) {
            throw new RuntimeException("Drawing not found");
        }

        // Remove the like from the drawingLike repository
        drawingLikeRepository.findByUserIdAndDrawingId(userId, drawingId)
                .ifPresent(like -> drawingLikeRepository.delete(like));

        // Update the cached count
        drawing.setLikesCount(Math.max(0, drawing.getLikesCount() - 1));
        return drawingRepository.save(drawing);
    }

    // Upload drawings to database
    @PostMapping("/uploadDrawing")
    public ResponseEntity<Drawing> uploadDrawing(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("promptId") Long promptId
    ) {
        try {
            // Upload to firebase
            Bucket bucket = StorageClient.getInstance().bucket();
            String blobName = "drawings/" + file.getOriginalFilename();
            Blob blob = bucket.create(blobName, file.getBytes(), file.getContentType());

            // Generate download url
            String encodedName = URLEncoder.encode(blobName, StandardCharsets.UTF_8);
            String downloadUrl = String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    encodedName
            );

            // Get user
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Prompt prompt = promptRepository.findById(promptId).orElseThrow(() -> new RuntimeException("Prompt not found"));

            // Save drawing
            Drawing drawing = new Drawing();
            drawing.setUser(user);
            drawing.setImageUrl(downloadUrl);
            drawing.setLikesCount(0);

            Drawing savedDrawing = drawingRepository.save(drawing);

            return ResponseEntity.ok(savedDrawing);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.basketball.backend.controller;

import com.basketball.backend.model.Drawing;
import com.basketball.backend.repository.DrawingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow access from Android emulator or any client
public class DrawingController {

    @Autowired
    private DrawingRepository drawingRepository;
    @Autowired
    private DrawingLikeRepository drawingLikeRepository;

    // GET all drawings for leaderboard (sorted by likes or timestamp)
    @GetMapping("/leaderboard/likes")
    public List<Drawing> getLeaderboardByLikes() {
        return drawingRepository.findAllByOrderByLikesCountDesc();
    }

    @GetMapping("/leaderboard/new")
    public List<Drawing> getLeaderboardByTimestamp() {
        return drawingRepository.findAllByOrderByCreatedAtDesc();;
    }



    // POST /api/users/register
    // stores a drawing to the user's profile
    @PostMapping("/storeDrawing")
    public Drawing storeDrawing(@RequestBody Drawing drawing) {
        return drawingRepository.save(drawing);
    }

    @PostMapping("/like/{drawingId}/{userId}")
    public Drawing likeDrawing(@PathVariable Long drawingId) {
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

    @PostMapping("/unlike/{drawingId}/{userId}")
    public Drawing unlikeDrawing(@PathVariable Long drawingId) {
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
}
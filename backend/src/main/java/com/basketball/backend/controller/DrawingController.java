package com.basketball.backend.controller;

import com.basketball.backend.model.Drawing;
import com.basketball.backend.repository.DrawingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow access from Android emulator or any client
public class DrawingController {

    @Autowired
    private DrawingRepository drawingRepository;

    // POST /api/users/register
    // stores a drawing to the user's profile
    @PostMapping("/storeDrawing")
    public Drawing storeDrawing(@RequestBody Drawing drawing) {
        return drawingRepository.save(drawing);
    }

    @PostMapping("/like/{drawingId}")
    public Drawing likeDrawing(@PathVariable Long drawingId) {
        Drawing drawing = drawingRepository.findById(drawingId).orElse(null);


        if (drawing == null) {
            throw new RuntimeException("Drawing not found");
        }

        drawing.setLikesCount(drawing.getLikesCount() + 1);

        return drawingRepository.save(drawing);
    }

    @PostMapping("/unlike/{username}")
    public Drawing unlikeDrawing(@PathVariable Long drawingId) {
        Drawing drawing = drawingRepository.findById(drawingId).orElse(null);


        if (drawing == null) {
            throw new RuntimeException("Drawing not found");
        }

        drawing.setLikesCount(Math.max(0, drawing.getLikesCount() - 1));

        return drawingRepository.save(drawing);
    }
}
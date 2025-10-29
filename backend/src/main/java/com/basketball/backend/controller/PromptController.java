package com.basketball.backend.controller;

import com.basketball.backend.model.Prompt;
import com.basketball.backend.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.drawingapp.prompter.WordFetch;
import java.sql.Timestamp;
import java.time.Instant;

import java.util.Map;

@RestController
@RequestMapping("/api/prompts")
@CrossOrigin(origins = "*") // Allow access from Android emulator or any client
public class PromptController {

    @Autowired
    private PromptRepository promptRepository;

    private final WordFetch wordFetch = new WordFetch();

    // GENERATE A DAILY PROMPT every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyPrompt() {
        String promptText = wordFetch.getDrawingPrompt();
        Prompt prompt = new Prompt();
        prompt.setText(promptText);
        prompt.setDateGenerated(Timestamp.from(Instant.now()));
        promptRepository.save(prompt);
    }

    // GET TODAYS PROMPT
    @GetMapping("/today")
    public Prompt getTodayPrompt() {
        return promptRepository.findTopByOrderByDateGeneratedDesc();
    }
}

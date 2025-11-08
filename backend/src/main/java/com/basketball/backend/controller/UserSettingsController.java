package com.basketball.backend.controller;

import com.basketball.backend.model.UserSettings;
import com.basketball.backend.model.User;
import com.basketball.backend.repository.UserRepository;
import com.basketball.backend.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/settings")
@CrossOrigin(origins = "*")
public class UserSettingsController {

    // getting both user and user setting respoitorites
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    @Autowired
    private UserRepository userRepository;

    // get user settings
    @GetMapping("/{userId}")
    public UserSettings getUserSettings(@PathVariable Long userId) {
        return userSettingsRepository.findById(userId).orElseThrow(() -> new RuntimeException("user settings not found"));
    }

    // update theme or notifications
    @PostMapping("/{userId}/update")
    public UserSettings updateUserSettings(@PathVariable Long userId, @RequestBody UserSettings newSettings) {
        UserSettings settings = userSettingsRepository.findById(userId).orElseThrow(() -> new RuntimeException("user settings not found"));

        //update whatever you want here
        if (newSettings.getTheme() != null) {
            settings.setTheme(newSettings.getTheme());
        }

        if (newSettings.getNotificationsEnabled() != null) {
            settings.setNotificationsEnabled(newSettings.getNotificationsEnabled());
        }

        return userSettingsRepository.save(settings);

    }

    // delete account (and settings)
    @DeleteMapping("/{userId}")
    public String deleteUserSettings(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        // DELETE IT
        userRepository.delete(user);

        return "User account deleted successfully";
    }

}

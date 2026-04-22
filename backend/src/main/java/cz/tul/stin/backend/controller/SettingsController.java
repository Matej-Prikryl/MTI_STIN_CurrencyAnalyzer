package cz.tul.stin.backend.controller;

import cz.tul.stin.backend.model.Settings;
import cz.tul.stin.backend.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingsService;

    @GetMapping
    public Settings getSettings() {
        return settingsService.loadSettings();
    }

    @PostMapping
    public void saveSettings(@RequestBody Settings settings) {
        settingsService.saveSettings(settings);
    }
}

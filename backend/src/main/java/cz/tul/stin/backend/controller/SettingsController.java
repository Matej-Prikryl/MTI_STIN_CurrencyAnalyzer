package cz.tul.stin.backend.controller;

import cz.tul.stin.backend.model.Settings;
import cz.tul.stin.backend.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {
    private final SettingsService settingsService;

    @GetMapping
    public Settings getSettings() {
        log.debug("Settings Controller: Fetching settings");
        return settingsService.loadSettings();
    }

    @PostMapping
    public void saveSettings(@RequestBody Settings settings) {
        log.debug("Settings Controller: Saving settings: {}", settings);
        settingsService.saveSettings(settings);
    }
}

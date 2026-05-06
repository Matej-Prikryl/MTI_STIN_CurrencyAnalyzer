package cz.tul.stin.backend.service;

import java.io.File;

import org.springframework.stereotype.Service;

import cz.tul.stin.backend.model.Settings;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@NoArgsConstructor
public class SettingsService {
    private final String FILE_PATH = getSettingsPath();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Settings loadSettings() {
        File file = new File(FILE_PATH);
        log.info("Loading settings from: {}", file.getAbsolutePath());

        if (!file.exists()) {
            log.warn("The file settings.json does not exist, returning default.");
            return new Settings();
        }
        try {
            return objectMapper.readValue(file, Settings.class);
        } catch (Exception e) {
            log.error("Error reading JSON: {}", e.getMessage());
            return new Settings();
        }
    }

    public void saveSettings(Settings settings) {
        try {
            File file = new File(FILE_PATH);
            log.info("Saving settings to: {}", file.getAbsolutePath());
            log.debug("Data to be saved: {}", settings);

            objectMapper.writeValue(file, settings);
            log.info("Settings saved successfully.");
        } catch (Exception e) {
            log.error("Critical error saving settings: ", e);
            throw new RuntimeException("Unable to save settings", e);
        }
    }

    private String getSettingsPath() {
        if (new File("/home").exists()) {
            return "/home/settings.json";
        }
        return "settings.json";
    }
}

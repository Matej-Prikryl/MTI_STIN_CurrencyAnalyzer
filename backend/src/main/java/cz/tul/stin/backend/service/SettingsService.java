package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.Settings;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

@Service
public class SettingsService {
    private final String FILE_PATH = "settings.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Settings loadSettings() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new Settings();
        }
        try {
            return objectMapper.readValue(file, Settings.class);
        } catch (Exception e) {
            return new Settings();
        }
    }

    public void saveSettings(Settings settings) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), settings);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save settings", e);
        }
    }
}

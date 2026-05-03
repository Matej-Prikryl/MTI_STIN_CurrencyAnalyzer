package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.Settings;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

@Service
@Slf4j // Přidejte pro logování
public class SettingsService {
    private final String FILE_PATH = "settings.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Settings loadSettings() {
        File file = new File(FILE_PATH);
        log.info("Načítám nastavení z: {}", file.getAbsolutePath()); // Uvidíte přesnou cestu

        if (!file.exists()) {
            log.warn("Soubor settings.json neexistuje, vracím default.");
            return new Settings();
        }
        try {
            return objectMapper.readValue(file, Settings.class);
        } catch (Exception e) {
            log.error("Chyba při čtení JSON: {}", e.getMessage());
            return new Settings();
        }
    }

    public void saveSettings(Settings settings) {
        try {
            File file = new File(FILE_PATH);
            log.info("Ukládám nastavení do: {}", file.getAbsolutePath());
            log.info("Data k uložení: {}", settings); // Ověřte, že objekt není prázdný

            objectMapper.writeValue(file, settings);
            log.info("Uložení proběhlo úspěšně.");
        } catch (Exception e) {
            log.error("KRITICKÁ CHYBA PŘI UKLÁDÁNÍ: ", e);
            throw new RuntimeException("Unable to save settings", e);
        }
    }
}

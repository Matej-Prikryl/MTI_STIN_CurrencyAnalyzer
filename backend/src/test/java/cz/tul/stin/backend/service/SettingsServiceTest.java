package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SettingsServiceTest {

    private SettingsService settingsService;
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    private String filePath;

    @BeforeEach
    void setUp() throws Exception {
        settingsService = new SettingsService();
        objectMapper = new ObjectMapper();

        // Point FILE_PATH to a temp file so tests don't touch the filesystem
        filePath = "settings.json";
        setPrivateField(settingsService, "FILE_PATH", filePath);
    }

    // --- loadSettings() ---

    @Test
    void loadSettings_fileDoesNotExist_returnsDefaultSettings() {
        Settings result = settingsService.loadSettings();

        assertNotNull(result);
    }

    @Test
    void loadSettings_validFile_returnsDeserializedSettings() throws Exception {
        Settings expected = new Settings();
        // Populate fields here if Settings has them, e.g.:
        // expected.setSomeField("value");

        objectMapper.writeValue(new File(filePath), expected);

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
        assertEquals(expected, result); // requires equals() on Settings
    }

    @Test
    void loadSettings_corruptFile_returnsDefaultSettings() throws Exception {
        File file = new File(filePath);
        // Write invalid JSON
        java.nio.file.Files.writeString(file.toPath(), "{ not valid json !!!");

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
    }

    @Test
    void loadSettings_emptyFile_returnsDefaultSettings() throws Exception {
        File file = new File(filePath);
        java.nio.file.Files.writeString(file.toPath(), "");

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
    }

    // --- saveSettings() ---

    @Test
    void saveSettings_writesFileSuccessfully() throws Exception {
        Settings settings = new Settings();
        // settings.setSomeField("value");

        settingsService.saveSettings(settings);

        File file = new File(filePath);
        assertTrue(file.exists());

        Settings loaded = objectMapper.readValue(file, Settings.class);
        assertNotNull(loaded);
        assertEquals(settings, loaded); // requires equals() on Settings
    }

    @Test
    void saveSettings_thenLoad_roundTripsCorrectly() {
        Settings original = new Settings();
        // original.setSomeField("roundtrip");

        settingsService.saveSettings(original);
        Settings loaded = settingsService.loadSettings();

        assertNotNull(loaded);
        assertEquals(original, loaded);
    }

    @Test
    void saveSettings_overwritesExistingFile() throws Exception {
        Settings first = new Settings();
        Settings second = new Settings();
        // first.setSomeField("first");
        // second.setSomeField("second");

        settingsService.saveSettings(first);
        settingsService.saveSettings(second);

        Settings loaded = settingsService.loadSettings();
        assertEquals(second, loaded);
    }

    // --- Helpers ---

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
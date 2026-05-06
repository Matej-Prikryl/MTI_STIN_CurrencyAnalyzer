package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        filePath = tempDir.resolve("settings.json").toString();
        setPrivateField(settingsService, "FILE_PATH", filePath);
    }

    // --- loadSettings() ---

    @Test
    void loadSettings_fileDoesNotExist_returnsDefaultSettings() throws Exception {
        String nonExistentPath = tempDir.resolve("non_existent.json").toString();
        setPrivateField(settingsService, "FILE_PATH", nonExistentPath);

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
        assertEquals(new Settings(), result);
    }

    @Test
    void loadSettings_validFile_returnsDeserializedSettings() throws Exception {
        Settings expected = new Settings();
        expected.setLanguage("cs");

        objectMapper.writeValue(new File(filePath), expected);

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void loadSettings_corruptFile_returnsDefaultSettings() throws Exception {
        File file = new File(filePath);
        // Write invalid JSON
        java.nio.file.Files.writeString(file.toPath(), "{ not valid json !!!");

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
        assertEquals(new Settings(), result);
    }

    @Test
    void loadSettings_emptyFile_returnsDefaultSettings() throws Exception {
        File file = new File(filePath);
        java.nio.file.Files.writeString(file.toPath(), "");

        Settings result = settingsService.loadSettings();

        assertNotNull(result);
        assertEquals(new Settings(), result);
    }

    // --- saveSettings() ---

    @Test
    void saveSettings_writesFileSuccessfully() throws Exception {
        Settings settings = new Settings();
        settings.setLanguage("de");

        settingsService.saveSettings(settings);

        File file = new File(filePath);
        assertTrue(file.exists());

        Settings loaded = objectMapper.readValue(file, Settings.class);
        assertNotNull(loaded);
        assertEquals(settings, loaded);
    }

    @Test
    void saveSettings_throwsRuntimeExceptionOnError() throws Exception {
        // Point to a directory to cause write failure
        String dirPath = tempDir.resolve("some_dir").toString();
        File dir = new File(dirPath);
        assertTrue(dir.mkdir());
        setPrivateField(settingsService, "FILE_PATH", dirPath);

        assertThrows(RuntimeException.class, () -> settingsService.saveSettings(new Settings()));
    }

    // --- getSettingsPath() ---

    @Test
    void getSettingsPath_returnsCorrectPath() throws Exception {
        Method method = SettingsService.class.getDeclaredMethod("getSettingsPath");
        method.setAccessible(true);
        String path = (String) method.invoke(settingsService);

        if (new File("/home").exists()) {
            assertEquals("/home/settings.json", path);
        } else {
            assertEquals("settings.json", path);
        }
    }

    // --- Helpers ---

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

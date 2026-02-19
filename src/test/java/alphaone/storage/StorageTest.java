package alphaone.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import alphaone.model.Task;
import alphaone.model.ToDo;

public class StorageTest {

    @Test
    public void saveAndLoad_validTasks_roundTripSucceeds() throws Exception {
        Path tempDir = Files.createTempDirectory("storage-test-");
        String originalUserDir = System.getProperty("user.dir");
        String originalAlphaProperty = System.getProperty("alphaone.test");
        System.setProperty("user.dir", tempDir.toAbsolutePath().toString());
        System.setProperty("alphaone.test", "true");
        try {
            Storage storage = new Storage();
            HashMap<Integer, Task> map = new HashMap<>();
            map.put(1, new ToDo("taskone"));
            map.put(2, new ToDo("tasktwo"));
            storage.save(map);

            Storage reloader = new Storage();
            HashMap<Integer, Task> loaded = reloader.load();
            assertEquals(2, loaded.size());
            assertEquals("taskone", loaded.get(1).getDescription());
            assertEquals("tasktwo", loaded.get(2).getDescription());
        } finally {
            restoreAlphaProperty(originalAlphaProperty);
            System.setProperty("user.dir", originalUserDir);
            try {
                deleteRecursively(tempDir);
            } catch (Exception cleanupException) {
                Logger.getAnonymousLogger().warning(
                        "Test cleanup failed: " + cleanupException.getMessage());
            }
        }
    }

    @Test
    public void load_malformedLine_skipsLineAndLogsWarning() throws Exception {
        Path tempDir = Files.createTempDirectory("storage-test-logs-");
        String originalUserDir = System.getProperty("user.dir");
        String originalAlphaProperty = System.getProperty("alphaone.test");
        System.setProperty("user.dir", tempDir.toAbsolutePath().toString());
        System.setProperty("alphaone.test", "true");

        Logger logger = Logger.getLogger(Storage.class.getName());
        TestLogHandler handler = new TestLogHandler();
        Level originalLogLevel = logger.getLevel();
        attachLogHandler(logger, handler);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream capturedOutput = startStdoutCapture();

        try {
            Path dataDir = tempDir.resolve("data");
            Files.createDirectories(dataDir);
            Path storageFile = dataDir.resolve("alphaone.txt");
            writeTestStorageFile(storageFile);

            Storage storage = new Storage();
            HashMap<Integer, Task> loaded = storage.load();
            assertEquals(1, loaded.size());
            assertEquals("alpha", loaded.get(1).getDescription());

            assertMalformedLineWarningWasEmitted(capturedOutput, handler);
        } finally {
            detachLogHandler(logger, handler, originalLogLevel);
            restoreAlphaProperty(originalAlphaProperty);
            System.setProperty("user.dir", originalUserDir);
            System.setOut(originalOut);
            try {
                deleteRecursively(tempDir);
            } catch (Exception cleanupException) {
                Logger.getAnonymousLogger().warning(
                        "Test cleanup failed: " + cleanupException.getMessage());
            }
        }
    }

    private ByteArrayOutputStream startStdoutCapture() {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
        return capturedOutput;
    }

    private void attachLogHandler(Logger logger, TestLogHandler handler) {
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }

    private void detachLogHandler(Logger logger, TestLogHandler handler, Level originalLogLevel) {
        logger.removeHandler(handler);
        logger.setLevel(originalLogLevel);
    }

    private void restoreAlphaProperty(String originalAlphaProperty) {
        if (originalAlphaProperty == null) {
            System.clearProperty("alphaone.test");
        } else {
            System.setProperty("alphaone.test", originalAlphaProperty);
        }
    }

    private void writeTestStorageFile(Path storageFile) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(storageFile, StandardCharsets.UTF_8)) {
            writer.write("this is not serialized");
            writer.newLine();
            writer.write("t!@!true!@!alpha");
            writer.newLine();
        }
    }

    private void assertMalformedLineWarningWasEmitted(ByteArrayOutputStream capturedOutput,
            TestLogHandler handler) {
        String stdout = capturedOutput.toString(StandardCharsets.UTF_8);
        boolean warningWasLogged = handler.messages.stream()
                .anyMatch(m -> m.contains("skipping malformed storage line"));
        boolean warningWasPrinted = stdout.contains("skipping malformed storage line");
        assertTrue(warningWasLogged || warningWasPrinted,
                "Expected warning to be logged or printed");
    }

    private void deleteRecursively(Path tempDir) throws Exception {
        try (Stream<Path> paths = Files.walk(tempDir)) {
            // Delete return value intentionally ignored — best-effort test cleanup only.
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    private static class TestLogHandler extends Handler {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            if (record == null) {
                return;
            }
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }
}

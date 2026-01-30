package alphaone.storage;

import alphaone.model.ToDo;
import alphaone.model.Task;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {

    @Test
    public void saveAndLoad_cycle() throws Exception {
        Path tmp = Files.createTempDirectory("storage-test-");
        String originalUserDir = System.getProperty("user.dir");
        String originalAlpha = System.getProperty("alphaone.test");
        System.setProperty("user.dir", tmp.toAbsolutePath().toString());
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
            if (originalAlpha == null) System.clearProperty("alphaone.test"); else System.setProperty("alphaone.test", originalAlpha);
            System.setProperty("user.dir", originalUserDir);
            // cleanup
            try { Files.walk(tmp).sorted((a,b)->b.compareTo(a)).forEach(p->p.toFile().delete()); } catch (Exception ignored) {}
        }
    }

    @Test
    public void load_skipsMalformedLines_andLogsWarning() throws Exception {
        Path tmp = Files.createTempDirectory("storage-test-logs-");
        String originalUserDir = System.getProperty("user.dir");
        String originalAlpha = System.getProperty("alphaone.test");
        System.setProperty("user.dir", tmp.toAbsolutePath().toString());
        // ensure logger mode so messages go to logger, but also capture System.out in case
        System.setProperty("alphaone.test", "true");

        Logger logger = Logger.getLogger(Storage.class.getName());
        TestLogHandler handler = new TestLogHandler();
        Level oldLevel = logger.getLevel();
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);

        // capture stdout too
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            Path dataDir = tmp.resolve("data");
            Files.createDirectories(dataDir);
            Path file = dataDir.resolve("alphaone.txt");
            try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                bw.write("this is not serialized");
                bw.newLine();
                bw.write("t!@!true!@!alpha");
                bw.newLine();
            }

            Storage storage = new Storage();
            HashMap<Integer, Task> loaded = storage.load();
            // should have skipped malformed line and loaded the valid ToDo
            assertEquals(1, loaded.size());
            assertEquals("alpha", loaded.get(1).getDescription());

            String stdout = baos.toString(StandardCharsets.UTF_8);
            boolean logged = handler.messages.stream().anyMatch(m -> m.contains("skipping malformed storage line"));
            boolean printed = stdout.contains("skipping malformed storage line");
            assertTrue(logged || printed, "Expected warning to be logged or printed");
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(oldLevel);
            if (originalAlpha == null) System.clearProperty("alphaone.test"); else System.setProperty("alphaone.test", originalAlpha);
            System.setProperty("user.dir", originalUserDir);
            System.setOut(originalOut);
            try { Files.walk(tmp).sorted((a,b)->b.compareTo(a)).forEach(p->p.toFile().delete()); } catch (Exception ignored) {}
        }
    }

    private static class TestLogHandler extends Handler {
        public java.util.List<String> messages = new java.util.ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            if (record == null) return;
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }
}

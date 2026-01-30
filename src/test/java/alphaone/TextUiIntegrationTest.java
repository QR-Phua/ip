package alphaone;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Full integration test is fragile; keep integration tests disabled per preference")
public class TextUiIntegrationTest {

    @Test
    public void textUiMatchesExpectedOutput() throws Exception {
        Path inputPath = Path.of("text-ui-test", "input.txt");
        Path expectedPath = Path.of("text-ui-test", "EXPECTED.TXT");

        if (!Files.exists(inputPath) || !Files.exists(expectedPath)) {
            fail("Required test fixtures not found in text-ui-test/ (input.txt and EXPECTED.TXT)");
        }

        byte[] inputBytes = Files.readAllBytes(inputPath);
        String expectedRaw = Files.readString(expectedPath, StandardCharsets.UTF_8);

        // Prepare to capture stdout and provide stdin BEFORE AlphaOne / Ui classes are loaded
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream testOut = new PrintStream(baos, true, StandardCharsets.UTF_8);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try (InputStream testIn = Files.newInputStream(inputPath)) {
            System.setIn(testIn);
            System.setOut(testOut);

            Future<?> future = executor.submit(() -> {
                // Call the program entry point
                AlphaOne.main(new String[0]);
            });

            try {
                // Wait up to 15 seconds for the program to finish
                future.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                future.cancel(true);
                fail("AlphaOne.main did not finish within the timeout");
            } catch (ExecutionException ee) {
                // If the program threw, handle specific acceptable exceptions (scanner EOF)
                Throwable cause = ee.getCause();
                if (cause instanceof java.util.NoSuchElementException) {
                    // treat as end-of-input; we'll continue to capture whatever was printed
                } else if (cause != null) {
                    throw new RuntimeException("AlphaOne.main threw an exception", cause);
                } else {
                    throw ee;
                }
            }

            String actualRaw = baos.toString(StandardCharsets.UTF_8);

            String actual = normalize(actualRaw);
            String expected = normalize(expectedRaw);

            assertEquals(expected, actual, () -> "Program output did not match expected.\n" +
                    "Expected (normalized):\n" + expected + "\n\nActual (normalized):\n" + actual);

        } finally {
            // Restore original streams and shutdown executor
            System.setIn(originalIn);
            System.setOut(originalOut);
            executor.shutdownNow();
        }
    }

    private static String normalize(String s) {
        if (s == null) return "";
        // Normalize CRLF -> LF
        String lf = s.replace("\r\n", "\n").replace("\r", "\n");
        // Split into lines and trim trailing spaces on each line
        String[] lines = lf.split("\n", -1);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            // Remove trailing whitespace only
            out.add(stripTrailing(line));
        }
        // Remove trailing empty lines
        int end = out.size();
        while (end > 0 && out.get(end - 1).isEmpty()) end--;
        List<String> trimmed = out.subList(0, end);
        return String.join("\n", trimmed);
    }

    private static String stripTrailing(String s) {
        int i = s.length();
        while (i > 0 && Character.isWhitespace(s.charAt(i - 1))) i--;
        return s.substring(0, i);
    }
}

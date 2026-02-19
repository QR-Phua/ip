package alphaone.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import alphaone.model.Contact;
import alphaone.model.Deadline;
import alphaone.model.Event;
import alphaone.model.Task;
import alphaone.model.ToDo;
import alphaone.ui.Ui;
import alphaone.util.Constants;

/**
 * Handles persistence for the application by reading/writing tasks and contacts
 * to text files.
 */
public class Storage {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());
    private final Path fileStoragePath = Paths.get(System.getProperty("user.dir"), "data", "alphaone.txt");
    private final Path contactStoragePath = Paths.get(System.getProperty("user.dir"), "data", "alphaone_contacts.txt");

    /**
     * Creates a Storage instance using the default file paths under the current working directory.
     */
    public Storage() {
    }

    /** Returns true when running under the test harness (uses logger instead of UI output). */
    private static boolean isTestEnvironment() {
        return Boolean.getBoolean("alphaone.test");
    }

    private static void logOrPrintWarning(String msg) {
        if (isTestEnvironment()) {
            LOGGER.log(Level.WARNING, msg);
        } else {
            Ui.print(msg);
        }
    }

    private static void logOrPrintError(String msg) {
        if (isTestEnvironment()) {
            LOGGER.log(Level.SEVERE, msg);
        } else {
            Ui.print(msg);
        }
    }

    /* ====== Public orchestration methods (small, single-level) ====== */

    /**
     * Loads tasks from the storage file and rebuilds an in-memory task map.
     *
     * @return a HashMap mapping integer identifiers to Task instances. If no
     *         file exists an empty map is returned.
     */
    public HashMap<Integer, Task> load() {
        try {
            ensureParentAndFileExists(fileStoragePath);
            List<String> lines = readNonBlankLines(fileStoragePath);
            return buildTaskMapFromLines(lines);
        } catch (IOException ioe) {
            logOrPrintError("Error reading stored file, starting fresh");
            return new HashMap<>();
        }
    }

    /**
     * Persists the provided task map to the storage file, replacing its contents.
     *
     * @param taskList the task map to persist; keys and order are retained.
     */
    public void save(HashMap<Integer, Task> taskList) {
        try {
            ensureParentAndFileExists(fileStoragePath);
            List<String> lines = new ArrayList<>();
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                lines.add(entry.getValue().serialiseTask());
            }
            writeLines(fileStoragePath, lines);
        } catch (IOException e) {
            logOrPrintError("Error saving tasklist");
        }
    }

    /**
     * Loads stored contacts from the contact storage file.
     *
     * Each contact is expected as a single line using the same '!@!' separator
     * in the format: name!@!phone
     *
     * @return an ArrayList of Contact instances (empty if none saved)
     */
    public ArrayList<Contact> loadContacts() {
        try {
            ensureParentAndFileExists(contactStoragePath);
            List<String> lines = readNonBlankLines(contactStoragePath);
            return buildContactsFromLines(lines);
        } catch (IOException ioe) {
            logOrPrintError("Error reading contacts file, starting fresh");
            return new ArrayList<>();
        }
    }

    /**
     * Saves contacts to the contacts file, one per line (name!@!phone).
     *
     * @param contacts list of contacts to persist
     */
    public void saveContacts(ArrayList<Contact> contacts) {
        try {
            ensureParentAndFileExists(contactStoragePath);
            List<String> lines = new ArrayList<>();
            for (Contact contact : contacts) {
                lines.add(contact.serialiseContact());
            }
            writeLines(contactStoragePath, lines);
        } catch (IOException e) {
            logOrPrintError("Error saving contacts");
        }
    }

    /* ====== Low-level helpers (single responsibility / single abstraction level) ====== */

    private void ensureParentAndFileExists(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    private List<String> readNonBlankLines(Path path) throws IOException {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        List<String> all = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> filtered = new ArrayList<>(all.size());
        for (String line : all) {
            if (line == null || line.isBlank()) {
                continue;
            }
            filtered.add(line);
        }
        return filtered;
    }

    // Write lines to file, replacing contents.
    private void writeLines(Path path, List<String> lines) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // Build a task map from raw storage lines. Handles per-line parse errors.
    private HashMap<Integer, Task> buildTaskMapFromLines(List<String> lines) {
        HashMap<Integer, Task> taskMap = new HashMap<>();
        int counter = 1;
        for (String line : lines) {
            try {
                Task task = parseTaskLine(line);
                taskMap.put(counter, task);
                counter++;
            } catch (IllegalArgumentException e) {
                logOrPrintWarning("Warning: " + e.getMessage());
            }
        }
        return taskMap;
    }

    // Build a contact list from raw storage lines. Handles per-line parse errors.
    private ArrayList<Contact> buildContactsFromLines(List<String> lines) {
        ArrayList<Contact> contacts = new ArrayList<>();
        for (String line : lines) {
            try {
                Contact contact = parseContactLine(line);
                contacts.add(contact);
            } catch (IllegalArgumentException e) {
                logOrPrintWarning("Warning: " + e.getMessage());
            }
        }
        return contacts;
    }

    // Parse a single task line into a Task; throws IllegalArgumentException for malformed/unknown types.
    private Task parseTaskLine(String line) {
        String[] parts = line.split(Pattern.quote(Constants.STORAGE_SEPARATOR));
        if (parts.length < 3) {
            throw new IllegalArgumentException("skipping malformed storage line: '" + line + "'");
        }
        String type = parts[0].toLowerCase();
        boolean wasDone = Objects.equals(parts[1], "true");

        switch (type) {
        case "t" -> {
            // ToDo entries: T!@!<done>!@!<desc>
            return new ToDo(wasDone, parts[2]);
        }
        case "d" -> {
            // Deadline entries: D!@!<done>!@!<desc>!@!<by>
            if (parts.length < 4) {
                throw new IllegalArgumentException("malformed Deadline entry, skipping: '" + line + "'");
            }
            return new Deadline(wasDone, parts[2], parts[3]);
        }
        case "e" -> {
            // Event entries: E!@!<done>!@!<desc>!@!<from>!@!<to>
            if (parts.length < 5) {
                throw new IllegalArgumentException("malformed Event entry, skipping: '" + line + "'");
            }
            return new Event(wasDone, parts[2], parts[3], parts[4]);
        }
        default -> throw new IllegalArgumentException("unknown task type in storage, skipping: '" + line + "'");
        }
    }

    // Parse a contact line into a Contact; throws IllegalArgumentException for malformed lines.
    private Contact parseContactLine(String line) {
        String[] parts = line.split(Pattern.quote(Constants.STORAGE_SEPARATOR));
        if (parts.length < 2) {
            throw new IllegalArgumentException("skipping malformed contact line: '" + line + "'");
        }
        return new Contact(parts[0], parts[1]);
    }
}

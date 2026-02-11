package alphaone.storage;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import alphaone.model.Contact;
import alphaone.model.Deadline;
import alphaone.model.Event;
import alphaone.model.Task;
import alphaone.model.ToDo;
import alphaone.ui.Ui;

/**
 * Handles persistence for the application by reading/writing tasks and contacts
 * to text files.
 */
public class Storage {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());
    private final Path fileStoragePath = Paths.get(System.getProperty("user.dir"), "data", "alphaone.txt");
    private final Path contactStoragePath = Paths.get(System.getProperty("user.dir"), "data", "alphaone_contacts.txt");

    public Storage() {
    }

    // Helper to decide whether to log (tests) or print (normal run)
    private static boolean useLogger() {
        return Boolean.getBoolean("alphaone.test");
    }

    private static void warn(String msg) {
        if (useLogger()) {
            LOGGER.log(Level.WARNING, msg);
        } else {
            Ui.print(msg);
        }
    }

    private static void severe(String msg) {
        if (useLogger()) {
            LOGGER.log(Level.SEVERE, msg);
        } else {
            Ui.print(msg);
        }
    }

    /**
     * Loads tasks from the storage file and rebuilds an in-memory task map.
     *
     * @return a HashMap mapping integer identifiers to Task instances. If no
     *         file exists an empty map is returned.
     */
    public HashMap<Integer, Task> load() {
        HashMap<Integer, Task> rebuiltTaskList = new HashMap<>();
        int counter = 1;
        try {
            Path parent = fileStoragePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(fileStoragePath)) {
                Files.createFile(fileStoragePath);
                return rebuiltTaskList;
            }
            try (BufferedReader br = Files.newBufferedReader(fileStoragePath, StandardCharsets.UTF_8)) {
                String nextline;
                while ((nextline = br.readLine()) != null) {
                    if (nextline.isBlank()) {
                        continue;
                    }
                    String[] split = nextline.split("!@!");
                    if (split.length < 3) {
                        String warning = "Warning: skipping malformed storage line: '" + nextline + "'";
                        warn(warning);
                        continue;
                    }
                    String type = split[0].toLowerCase();
                    boolean wasDone = split.length > 1 && Objects.equals(split[1], "true");
                    try {
                        switch (type) {
                        case "t" -> {
                            // ToDo expects type, done, description
                            if (split.length < 3) {
                                String warning = "Warning: malformed ToDo entry, skipping: '" + nextline + "'";
                                warn(warning);
                            } else {
                                rebuiltTaskList.put(counter, new ToDo(wasDone, split[2]));
                            }
                        }
                        case "d" -> {
                            // Deadline expects type, done, description, deadline
                            if (split.length < 4) {
                                String warning = "Warning: malformed Deadline entry, skipping: '" + nextline + "'";
                                warn(warning);
                            } else {
                                rebuiltTaskList.put(counter, new Deadline(wasDone, split[2], split[3]));
                            }
                        }
                        case "e" -> {
                            // Event expects type, done, description, start, end
                            if (split.length < 5) {
                                String warning = "Warning: malformed Event entry, skipping: '" + nextline + "'";
                                warn(warning);
                            } else {
                                rebuiltTaskList.put(counter, new Event(wasDone, split[2], split[3], split[4]));
                            }
                        }
                        default -> {
                            String warning = "Warning: unknown task type in storage, skipping: '" + nextline + "'";
                            warn(warning);
                        }
                        }
                    } catch (Exception e) {
                        String warning = "Warning: failed to rebuild task from storage line: '" + nextline + "' -> "
                                + e.getMessage();
                        warn(warning);
                    }
                    counter++;
                }
            }
        } catch (IOException ioe) {
            severe("Error reading stored file, starting fresh");
        }
        return rebuiltTaskList;
    }

    /**
     * Persists the provided task map to the storage file, replacing its contents.
     *
     * @param taskList the task map to persist; keys and order are retained.
     */
    public void save(HashMap<Integer, Task> taskList) {
        try {
            Path parent = fileStoragePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(fileStoragePath)) {
                Files.createFile(fileStoragePath);
            }
            try (BufferedWriter bw = Files.newBufferedWriter(fileStoragePath, StandardCharsets.UTF_8)) {
                for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                    bw.write(entry.getValue().serialiseTask());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            severe("Error saving tasklist");
        }
    }

    /**
     * Load stored contacts from the contact storage file.
     *
     * Each contact is expected as a single line using the same '!@!' separator
     * in the format: name!@!phone
     *
     * @return an ArrayList of Contact instances (empty if none saved)
     */
    public ArrayList<Contact> loadContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            Path parent = contactStoragePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(contactStoragePath)) {
                Files.createFile(contactStoragePath);
                return contacts;
            }
            try (BufferedReader br = Files.newBufferedReader(contactStoragePath, StandardCharsets.UTF_8)) {
                String nextline;
                while ((nextline = br.readLine()) != null) {
                    if (nextline.isBlank()) {
                        continue;
                    }
                    String[] parts = nextline.split("!@!");
                    if (parts.length < 2) {
                        warn("Warning: skipping malformed contact line: '" + nextline + "'");
                        continue;
                    }
                    String name = parts[0];
                    String phone = parts[1];
                    try {
                        ArrayList<String> args = new ArrayList<>();
                        args.add(name);
                        args.add(phone);
                        contacts.add(new Contact(args));
                    } catch (Exception e) {
                        warn("Warning: failed to rebuild contact from line: '" + nextline + "' -> " + e.getMessage());
                    }
                }
            }
        } catch (IOException ioe) {
            severe("Error reading contacts file, starting fresh");
        }
        return contacts;
    }

    /**
     * Save contacts to the contacts file, one per line (name!@!phone)
     *
     * @param contacts list of contacts to persist
     */
    public void saveContacts(ArrayList<Contact> contacts) {
        try {
            Path parent = contactStoragePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(contactStoragePath)) {
                Files.createFile(contactStoragePath);
            }
            try (BufferedWriter bw = Files.newBufferedWriter(contactStoragePath, StandardCharsets.UTF_8)) {
                for (Contact c : contacts) {
                    bw.write(c.serialiseContact());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            severe("Error saving contacts");
        }
    }
}

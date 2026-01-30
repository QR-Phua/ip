package alphaone.storage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import alphaone.model.Task;
import alphaone.model.ToDo;
import alphaone.model.Deadline;
import alphaone.model.Event;

/**
 * Handles persistence for the application by reading/writing tasks to a text file.
 *
 * The Storage class reads a simple line-based serialization and reconstructs
 * task objects. It is robust to malformed lines and will skip invalid entries.
 */
public class Storage {
    private final Path fileStoragePath = Paths.get(System.getProperty("user.dir"), "data", "alphaone.txt");

    public Storage() {
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
            if (parent != null) Files.createDirectories(parent);
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
                        System.out.println("Warning: skipping malformed storage line: '" + nextline + "'");
                        continue;
                    }
                    String type = split[0].toLowerCase();
                    boolean wasDone = split.length > 1 && Objects.equals(split[1], "true");
                    try {
                        switch (type) {
                            case "t" -> {
                                // ToDo expects type, done, description
                                if (split.length < 3) {
                                    System.out.println("Warning: malformed ToDo entry, skipping: '" + nextline + "'");
                                } else {
                                    rebuiltTaskList.put(counter, new ToDo(wasDone, split[2]));
                                }
                            }
                            case "d" -> {
                                // Deadline expects type, done, description, deadline
                                if (split.length < 4) {
                                    System.out.println("Warning: malformed Deadline entry, skipping: '" + nextline + "'");
                                } else {
                                    rebuiltTaskList.put(counter, new Deadline(wasDone, split[2], split[3]));
                                }
                            }
                            case "e" -> {
                                // Event expects type, done, description, start, end
                                if (split.length < 5) {
                                    System.out.println("Warning: malformed Event entry, skipping: '" + nextline + "'");
                                } else {
                                    rebuiltTaskList.put(counter, new Event(wasDone, split[2], split[3], split[4]));
                                }
                            }
                            default -> System.out.println("Warning: unknown task type in storage, skipping: '" + nextline + "'");
                        }
                    } catch (Exception e) {
                        System.out.println("Warning: failed to rebuild task from storage line: '" + nextline + "' -> " + e.getMessage());
                    }
                    counter++;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading stored file, starting fresh");
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
            if (parent != null) Files.createDirectories(parent);
            if (!Files.exists(fileStoragePath)) Files.createFile(fileStoragePath);
            try (BufferedWriter bw = Files.newBufferedWriter(fileStoragePath, StandardCharsets.UTF_8)) {
                for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                    bw.write(entry.getValue().serialiseTask());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving tasklist");
        }
    }
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskList {
    private HashMap<Integer,Task> taskList;
    // use app working dir + data/alphaone.txt so path is predictable and writable
    private final Path fileStoragePath = Paths.get(System.getProperty("user.dir"), "data", "alphaone.txt");
    private int counter = 1;

    public TaskList() {
        taskList = new HashMap<>();
        retrieveTaskList();
    }

    public void getTasks() {
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        if (!taskList.isEmpty()) {
            System.out.printf("You have these tasks in your list:%n");
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                Task currentTask = entry.getValue();
                System.out.printf("%d. %s%n", entry.getKey(), currentTask);
            }
        } else {
            System.out.println("Your task list is currently empty!");
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void addTask(String input, AlphaOne.TaskType type, String... params) {
        Task newTask = null;
        switch (type) {
        case TODO -> newTask = new ToDo(input);
        case DEADLINE -> newTask = new Deadline(input, params[0]);
        case EVENT -> newTask = new Event(input, params[0], params[1]);
        default -> System.out.println("Invalid task type!");
        }
        taskList.put(counter, newTask);
        counter++;
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("New task added to your task list!");
        System.out.println(newTask);
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void deleteTask(int taskNum) {
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        Task deleteTask = taskList.get(taskNum);
        taskList.remove(taskNum);
        System.out.println("The following task has been deleted!");
        System.out.println(deleteTask);
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void markDone(int taskNum) {
        Task markTask = taskList.get(taskNum);
        markTask.markDone();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Task marked done successfully!");
        System.out.println(markTask);
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void unmarkDone(int taskNum) {
        Task unMarkTask = taskList.get(taskNum);
        unMarkTask.markNotDone();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Task unmarked successfully!");
        System.out.println(unMarkTask);
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void taskExistenceChecker(int selectedTask) throws InvalidTaskItemException {
        Task searchTask = taskList.getOrDefault(selectedTask, null);
        if (searchTask == null) {
            throw new InvalidTaskItemException();
        }
    }

    private Task rebuildTask(String... params) {
        Task newTask = null;
        boolean wasDone = Objects.equals(params[1], "true");
        switch (params[0].toLowerCase()) {
        case "t" -> newTask = new ToDo(wasDone, params[2]);
        case "d" -> newTask = new Deadline(wasDone, params[2], params[3]);
        case "e" -> newTask = new Event(wasDone, params[2], params[3], params[4]);
        default -> System.out.println("Error building task!");
        }

        return newTask;
    }

    private void retrieveTaskList() {
        try {
            // ensure parent directory exists
            Path parent = fileStoragePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // ensure file exists (create empty file if missing) and return early
            if (!Files.exists(fileStoragePath)) {
                Files.createFile(fileStoragePath);
                return; // nothing to read
            }

            try (BufferedReader br = Files.newBufferedReader(fileStoragePath, StandardCharsets.UTF_8)) {

                HashMap<Integer, Task> rebuiltTaskList = new HashMap<>();

                String nextline;
                while ((nextline = br.readLine()) != null) { // Read until the end of the file (null)
                    String[] split = nextline.split("!@!");
                    rebuiltTaskList.put(counter, rebuildTask(split));
                    counter++;
                }
                this.taskList = rebuiltTaskList;
            }
        }
        catch (IOException e) {
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
            System.out.println("Error reading stored file!");
            System.out.println("Starting new tasklist!");
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        }
    }

    public void saveTaskList() {
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
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
            System.out.println("Error saved tasklist!");
            System.out.println("Discarding tasklist!");
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        }

    }

}

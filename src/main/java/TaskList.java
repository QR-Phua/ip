import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskList {
    private HashMap<Integer,Task> taskList;
    private final Path fileStoragePath = Paths.get("..","data", "alphaone.txt");
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

    public void retrieveTaskList() {
        try (BufferedReader br = new BufferedReader(
                new FileReader(fileStoragePath.toString()))) {

            HashMap<Integer, Task> rebuiltTaskList = new HashMap<>();

            String nextline;
            while ((nextline = br.readLine()) != null) { // Read until the end of the file (null)
                String[] split = nextline.split("!@!");
                rebuiltTaskList.put(counter, rebuildTask(split));
                counter++;
            }
            this.taskList = rebuiltTaskList;
        }
        catch (IOException e) {
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
            System.out.println("Error reading stored file!");
            System.out.println("Starting new tasklist!");
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        }
    }

    public void saveTaskList() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(fileStoragePath.toString()))) {

            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                bw.write(entry.getValue().serialiseTask());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
            System.out.println("Error saved tasklist!");
            System.out.println("Discarding tasklist!");
            System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        }

    }

}

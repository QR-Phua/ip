package alphaone.model;

import java.util.HashMap;
import java.util.Map;

import alphaone.exception.InvalidTaskItemException;
import alphaone.storage.Storage;
import alphaone.ui.Ui;

/**
 * In-memory container for Tasks with helper operations.
 */
public class TaskList {
    private HashMap<Integer, Task> tasks;
    private int nextTaskId = 1;
    private final Storage storage;

    /**
     * Creates a TaskList without persistence (in-memory only).
     */
    public TaskList() {
        this(null);
    }

    /**
     * Creates a TaskList that uses the provided Storage instance for persistence.
     * If storage is null the TaskList operates in-memory only.
     *
     * @param storage storage instance to load/save tasks, or null for no persistence
     */
    public TaskList(Storage storage) {
        this.storage = storage;
        if (this.storage != null) {
            HashMap<Integer, Task> loaded = this.storage.load();
            if (loaded != null && !loaded.isEmpty()) {
                this.tasks = loaded;
                this.nextTaskId = calculateMaxKey(loaded) + 1;
                return;
            }
        }
        tasks = new HashMap<>();
    }

    /**
     * Returns a formatted string listing all current tasks for display.
     *
     * @return formatted tasks text
     */
    public String formatTasksDisplay() {
        StringBuilder output = new StringBuilder();
        if (!tasks.isEmpty()) {
            output.append("You have these tasks in your list:\n");
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                Task currentTask = entry.getValue();
                output.append(String.format("%d. %s\n", entry.getKey(), currentTask));
            }
            trimTrailingNewline(output);
        } else {
            output.append("Your task list is currently empty!");
        }
        return output.toString();
    }

    /**
     * Prints the tasks display to the UI.
     */
    public void printTasks() {
        Ui.print(formatTasksDisplay());
    }

    /**
     * Adds a task and returns a confirmation message.
     *
     * @param input  task text
     * @param type   task type
     * @param params optional extra params
     * @return confirmation message
     */
    public String buildAddTaskMessage(String input, alphaone.core.AlphaOne.TaskType type, String... params) {
        Task newTask;
        switch (type) {
        case TODO -> newTask = new ToDo(input);
        case DEADLINE -> newTask = new Deadline(input, params[0]);
        case EVENT -> newTask = new Event(input, params[0], params[1]);
        default -> {
            return "Invalid task type!";
        }
        }
        tasks.put(nextTaskId, newTask);
        nextTaskId++;
        if (this.storage != null) {
            this.storage.save(this.tasks);
        }
        return "New task added to your task list!\n" + newTask;
    }

    /**
     * Adds a task and prints the confirmation.
     *
     * @param input  task text
     * @param type   task type
     * @param params optional params
     */
    public void addTask(String input, alphaone.core.AlphaOne.TaskType type, String... params) {
        Ui.print(buildAddTaskMessage(input, type, params));
    }

    /**
     * Deletes a task and returns a confirmation message.
     *
     * @param taskId id of the task to delete
     * @return deletion message
     */
    public String buildDeleteTaskMessage(int taskId) {
        Task taskToDelete = tasks.get(taskId);
        tasks.remove(taskId);
        if (this.storage != null) {
            this.storage.save(this.tasks);
        }
        return "The following task has been deleted!\n" + taskToDelete;
    }

    /**
     * Deletes a task and prints the confirmation.
     *
     * @param taskId id of the task to delete
     */
    public void deleteTask(int taskId) {
        Ui.print(buildDeleteTaskMessage(taskId));
    }

    /**
     * Marks the task as done and returns a confirmation message.
     *
     * @param taskId id of the task to mark
     * @return confirmation message
     */
    public String buildMarkDoneMessage(int taskId) {
        Task taskToMark = tasks.get(taskId);
        taskToMark.markDone();
        if (this.storage != null) {
            this.storage.save(this.tasks);
        }
        return "Task marked done successfully!\n" + taskToMark;
    }

    /**
     * Marks a task as done and prints the confirmation.
     *
     * @param taskId id of the task to mark
     */
    public void markDone(int taskId) {
        Ui.print(buildMarkDoneMessage(taskId));
    }

    /**
     * Unmarks the task and returns a confirmation message.
     *
     * @param taskId id of the task to unmark
     * @return confirmation message
     */
    public String buildUnmarkDoneMessage(int taskId) {
        Task taskToUnmark = tasks.get(taskId);
        taskToUnmark.markNotDone();
        if (this.storage != null) {
            this.storage.save(this.tasks);
        }
        return "Task unmarked successfully!\n" + taskToUnmark;
    }

    /**
     * Unmarks a task and prints the confirmation.
     *
     * @param taskId id of the task to unmark
     */
    public void unmarkDone(int taskId) {
        Ui.print(buildUnmarkDoneMessage(taskId));
    }

    /**
     * Verifies that the specified task id exists in the list.
     *
     * @param taskNumber the id to check.
     * @throws InvalidTaskItemException if the id does not exist.
     */
    public void verifyTaskExists(int taskNumber) throws InvalidTaskItemException {
        if (tasks.getOrDefault(taskNumber, null) == null) {
            throw new InvalidTaskItemException();
        }
    }

    /**
     * Searches tasks by keyword and returns matching tasks keyed by id.
     *
     * @param keyword substring to search for
     * @return map of matching tasks keyed by id
     */
    public HashMap<Integer, Task> searchKeyword(String keyword) {
        HashMap<Integer, Task> matchingTasks = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            Task currentTask = entry.getValue();
            if (currentTask.getDescription() != null && currentTask.getDescription().contains(keyword)) {
                matchingTasks.put(entry.getKey(), currentTask);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns search results text for display.
     *
     * @param keyword search string
     * @return formatted search results
     */
    public String buildSearchResultsMessage(String keyword) {
        HashMap<Integer, Task> matchingTasks = searchKeyword(keyword);
        if (!matchingTasks.isEmpty()) {
            StringBuilder output = new StringBuilder();
            output.append("These are the most relevant tasks\n");
            for (Map.Entry<Integer, Task> entry : matchingTasks.entrySet()) {
                output.append(String.format("%d. %s\n", entry.getKey(), entry.getValue()));
            }
            trimTrailingNewline(output);
            return output.toString();
        } else {
            return "No relevant tasks found!";
        }
    }

    /**
     * Returns the internal task map for persistence.
     *
     * @return internal task map.
     */
    public HashMap<Integer, Task> getInternalMap() {
        return this.tasks;
    }

    /**
     * Replaces the internal task map with the provided one and adjusts the id counter.
     *
     * @param map the replacement map (ignored if null).
     */
    public void setInternalMap(HashMap<Integer, Task> map) {
        if (map == null) {
            return;
        }
        this.tasks = map;
        this.nextTaskId = calculateMaxKey(map) + 1;
        if (this.storage != null) {
            this.storage.save(this.tasks);
        }
    }

    /** Returns the largest key in the given map, or 0 if the map is empty. */
    private int calculateMaxKey(HashMap<Integer, Task> map) {
        int max = 0;
        for (Integer key : map.keySet()) {
            if (key != null && key > max) {
                max = key;
            }
        }
        return max;
    }

    private void trimTrailingNewline(StringBuilder builder) {
        int lastIndex = builder.length() - 1;
        if (lastIndex >= 0 && builder.charAt(lastIndex) == '\n') {
            builder.deleteCharAt(lastIndex);
        }
    }
}

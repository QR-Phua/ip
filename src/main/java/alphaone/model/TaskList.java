package alphaone.model;

import java.util.HashMap;
import java.util.Map;

import alphaone.storage.Storage;
import alphaone.ui.Ui;

/**
 * In-memory container for Tasks with helper operations.
 */
public class TaskList {
    private HashMap<Integer, Task> taskList;
    private int counter = 1;
    private final Storage storage; // may be null for in-memory-only instances

    public TaskList() {
        this(null);
    }

    /**
     * Create a TaskList that uses the provided Storage instance for persistence.
     * If storage is null the TaskList operates in-memory only.
     *
     * @param storage storage instance to load/save tasks, or null for no persistence
     */
    public TaskList(Storage storage) {
        this.storage = storage;
        if (this.storage != null) {
            HashMap<Integer, Task> loaded = this.storage.load();
            if (loaded != null && !loaded.isEmpty()) {
                this.taskList = loaded;
                int max = 0;
                for (Integer k : loaded.keySet()) {
                    if (k != null && k > max) {
                        max = k;
                    }
                }
                this.counter = max + 1;
                return;
            }
        }
        taskList = new HashMap<>();
    }

    /**
     * Returns a raw (unformatted) representation of current tasks. Ui will add borders.
     *
     * @return raw formatted tasks text
     */
    public String getTasksString() {
        StringBuilder sb = new StringBuilder();
        if (!taskList.isEmpty()) {
            sb.append("You have these tasks in your list:\n");
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                Task currentTask = entry.getValue();
                sb.append(String.format("%d. %s\n", entry.getKey(), currentTask));
            }
        } else {
            sb.append("Your task list is currently empty!\n");
        }
        return sb.toString();
    }

    /**
     * Print the raw tasks string to the UI (Ui will add borders).
     */
    public void getTasks() {
        Ui.print(getTasksString());
    }

    /**
     * Add a task and return a raw confirmation message (no borders).
     *
     * @param input task text
     * @param type task type
     * @param params optional extra params
     * @return raw confirmation message
     */
    public String addTaskString(String input, alphaone.AlphaOne.TaskType type, String... params) {
        Task newTask;
        switch (type) {
        case TODO -> newTask = new ToDo(input);
        case DEADLINE -> newTask = new Deadline(input, params[0]);
        case EVENT -> newTask = new Event(input, params[0], params[1]);
        default -> {
            return "Invalid task type!";
        }
        }
        taskList.put(counter, newTask);
        counter++;
        if (this.storage != null) {
            this.storage.save(this.taskList);
        }
        return "New task added to your task list!\n" + newTask;
    }

    /**
     * Legacy: add task and print confirmation.
     *
     * @param input task text
     * @param type task type
     * @param params optional params
     */
    public void addTask(String input, alphaone.AlphaOne.TaskType type, String... params) {
        Ui.print(addTaskString(input, type, params));
    }

    /**
     * Delete a task and return a raw confirmation (no borders).
     *
     * @param taskNum id to delete
     * @return raw deletion message
     */
    public String deleteTaskString(int taskNum) {
        Task deleteTask = taskList.get(taskNum);
        taskList.remove(taskNum);
        if (this.storage != null) {
            this.storage.save(this.taskList);
        }
        return "The following task has been deleted!\n" + deleteTask;
    }

    /**
     * Legacy: delete and print.
     *
     * @param taskNum id to delete
     */
    public void deleteTask(int taskNum) {
        Ui.print(deleteTaskString(taskNum));
    }

    /**
     * Mark the task and return raw confirmation (no borders).
     *
     * @param taskNum id to mark
     * @return raw message
     */
    public String markDoneString(int taskNum) {
        Task markTask = taskList.get(taskNum);
        markTask.markDone();
        if (this.storage != null) {
            this.storage.save(this.taskList);
        }
        return "Task marked done successfully!\n" + markTask;
    }

    /**
     * Legacy: mark and print.
     *
     * @param taskNum id to mark
     */
    public void markDone(int taskNum) {
        Ui.print(markDoneString(taskNum));
    }

    /**
     * Unmark the task and return raw confirmation (no borders).
     *
     * @param taskNum id to unmark
     * @return raw message
     */
    public String unmarkDoneString(int taskNum) {
        Task unMarkTask = taskList.get(taskNum);
        unMarkTask.markNotDone();
        if (this.storage != null) {
            this.storage.save(this.taskList);
        }
        return "Task unmarked successfully!\n" + unMarkTask;
    }

    /**
     * Legacy: unmark and print.
     *
     * @param taskNum id to unmark
     */
    public void unmarkDone(int taskNum) {
        Ui.print(unmarkDoneString(taskNum));
    }

    /**
     * Verify that the specified task id exists in the list.
     *
     * @param selectedTask the id to check.
     * @throws alphaone.exception.InvalidTaskItemException if the id does not exist.
     */
    public void taskExistenceChecker(int selectedTask) throws alphaone.exception.InvalidTaskItemException {
        Task searchTask = taskList.getOrDefault(selectedTask, null);
        if (searchTask == null) {
            throw new alphaone.exception.InvalidTaskItemException();
        }
    }

    /**
     * Search tasks by keyword and return matching map.
     *
     * @param keyword substring to search for
     * @return map of matching tasks keyed by id
     */
    public HashMap<Integer, Task> searchKeyword(String keyword) {
        HashMap<Integer, Task> searchedTaskList = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
            Task currentTask = entry.getValue();
            if (currentTask.getDescription() != null && currentTask.getDescription().contains(keyword)) {
                searchedTaskList.put(entry.getKey(), currentTask);
            }
        }
        return searchedTaskList;
    }

    /**
     * Return raw search results text.
     *
     * @param keyword search string
     * @return raw search results (no borders)
     */
    public String displaySearchResultsString(String keyword) {
        HashMap<Integer, Task> searchedTaskList = searchKeyword(keyword);
        if (!searchedTaskList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("These are the most relevant tasks\n");
            for (Map.Entry<Integer, Task> entry : searchedTaskList.entrySet()) {
                sb.append(String.format("%d. %s\n", entry.getKey(), entry.getValue()));
            }
            return sb.toString();
        } else {
            return "No relevant tasks found!\n";
        }
    }

    /**
     * Returns the internal task map for persistence.
     *
     * @return internal task map.
     */
    public HashMap<Integer, Task> getInternalMap() {
        return this.taskList;
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
        this.taskList = map;
        int max = 0;
        for (Integer k : map.keySet()) {
            if (k != null && k > max) {
                max = k;
            }
        }
        this.counter = max + 1;
        if (this.storage != null) {
            this.storage.save(this.taskList);
        }
    }

}

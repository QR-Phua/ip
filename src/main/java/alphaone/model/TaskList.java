package alphaone.model;

import java.util.HashMap;
import java.util.Map;

import alphaone.ui.Ui;

/**
 * In-memory container for Tasks with helper operations.
 *
 * TaskList stores tasks indexed by integer ids, and offers operations to add,
 * delete, mark/unmark and search tasks.
 */
public class TaskList {
    private HashMap<Integer, Task> taskList;
    private int counter = 1;

    public TaskList() {
        taskList = new HashMap<>();
    }

    /**
     * Displays tasks to standard output in a numbered list.
     */
    public void getTasks() {
        System.out.println(Ui.BORDER);
        if (!taskList.isEmpty()) {
            System.out.println("You have these tasks in your list:");
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                Task currentTask = entry.getValue();
                System.out.println(String.format("%d. %s", entry.getKey(), currentTask));
            }
        } else {
            System.out.println("Your task list is currently empty!");
        }
        System.out.println(Ui.BORDER);
    }

    /**
     * Adds a new task of the specified type to the list.
     *
     * @param input the task description or primary input.
     * @param type the task type to add (TODO, DEADLINE, EVENT).
     * @param params optional additional parameters (e.g. date/time strings).
     */
    public void addTask(String input, alphaone.AlphaOne.TaskType type, String... params) {
        Task newTask = null;
        switch (type) {
        case TODO -> newTask = new ToDo(input);
        case DEADLINE -> newTask = new Deadline(input, params[0]);
        case EVENT -> newTask = new Event(input, params[0], params[1]);
        default -> System.out.println("Invalid task type!");
        }
        taskList.put(counter, newTask);
        counter++;
        System.out.println(Ui.BORDER);
        System.out.println("New task added to your task list!");
        System.out.println(newTask.toString());
        System.out.println(Ui.BORDER);
    }

    /**
     * Deletes the task with the specified id.
     *
     * @param taskNum identifier of the task to remove.
     */
    public void deleteTask(int taskNum) {
        System.out.println(Ui.BORDER);
        Task deleteTask = taskList.get(taskNum);
        taskList.remove(taskNum);
        System.out.println("The following task has been deleted!");
        System.out.println(deleteTask.toString());
        System.out.println(Ui.BORDER);
    }

    /**
     * Marks the specified task as done.
     *
     * @param taskNum identifier of the task to mark.
     */
    public void markDone(int taskNum) {
        Task markTask = taskList.get(taskNum);
        markTask.markDone();
        System.out.println(Ui.BORDER);
        System.out.println("Task marked done successfully!");
        System.out.println(markTask.toString());
        System.out.println(Ui.BORDER);
    }

    /**
     * Marks the specified task as not done.
     *
     * @param taskNum identifier of the task to unmark.
     */
    public void unmarkDone(int taskNum) {
        Task unMarkTask = taskList.get(taskNum);
        unMarkTask.markNotDone();
        System.out.println(Ui.BORDER);
        System.out.println("Task unmarked successfully!");
        System.out.println(unMarkTask.toString());
        System.out.println(Ui.BORDER);
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
     * Searches the task descriptions for the provided keyword.
     *
     * @param keyword substring to search for.
     * @return a map of matching tasks keyed by their ids.
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
     * Displays search results to standard output.
     *
     * @param keyword substring to search for.
     */
    public void displaySearchResults(String keyword) {
        HashMap<Integer, Task> searchedTaskList = searchKeyword(keyword);
        System.out.println(Ui.BORDER);
        if (!searchedTaskList.isEmpty()) {
            System.out.println("These are the most relevant tasks");
            for (Map.Entry<Integer, Task> entry : searchedTaskList.entrySet()) {
                System.out.println(String.format("%d. %s", entry.getKey(), entry.getValue()));
            }
        } else {
            System.out.println("No relevant tasks found!");
        }
        System.out.println(Ui.BORDER);
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
        // update counter to be one greater than the current max key
        int max = 0;
        for (Integer k : map.keySet()) {
            if (k != null && k > max) {
                max = k;
            }
        }
        this.counter = max + 1;
    }

}

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
     * Returns a formatted representation of current tasks.
     *
     * @return a string that contains the same content previously printed by getTasks().
     */
    public String getTasksString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.BORDER).append("\n");
        if (!taskList.isEmpty()) {
            sb.append("You have these tasks in your list:\n");
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                Task currentTask = entry.getValue();
                sb.append(String.format("%d. %s\n", entry.getKey(), currentTask));
            }
        } else {
            sb.append("Your task list is currently empty!\n");
        }
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Displays tasks to standard output in a numbered list.
     */
    public void getTasks() {
        System.out.println(getTasksString());
    }

    /**
     * Adds a new task of the specified type to the list and returns a formatted confirmation.
     *
     * @param input the task description or primary input.
     * @param type the task type to add (TODO, DEADLINE, EVENT).
     * @param params optional additional parameters (e.g. date/time strings).
     * @return the message that would previously be printed to the console.
     */
    public String addTaskString(String input, alphaone.AlphaOne.TaskType type, String... params) {
        Task newTask = null;
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
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.BORDER).append("\n");
        sb.append("New task added to your task list!\n");
        sb.append(newTask.toString()).append("\n");
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Adds a new task and prints the confirmation to standard output (legacy behaviour).
     */
    public void addTask(String input, alphaone.AlphaOne.TaskType type, String... params) {
        System.out.println(addTaskString(input, type, params));
    }

    /**
     * Deletes the task with the specified id and returns a formatted confirmation.
     *
     * @param taskNum identifier of the task to remove.
     * @return message describing the deleted task.
     */
    public String deleteTaskString(int taskNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.BORDER).append("\n");
        Task deleteTask = taskList.get(taskNum);
        taskList.remove(taskNum);
        sb.append("The following task has been deleted!\n");
        sb.append(deleteTask.toString()).append("\n");
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Deletes a task and prints the confirmation to standard output (legacy behaviour).
     */
    public void deleteTask(int taskNum) {
        System.out.println(deleteTaskString(taskNum));
    }

    /**
     * Marks the specified task as done and returns a formatted confirmation.
     *
     * @param taskNum identifier of the task to mark.
     * @return message describing the marked task.
     */
    public String markDoneString(int taskNum) {
        Task markTask = taskList.get(taskNum);
        markTask.markDone();
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.BORDER).append("\n");
        sb.append("Task marked done successfully!\n");
        sb.append(markTask.toString()).append("\n");
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Marks a task as done and prints the confirmation to standard output (legacy behaviour).
     */
    public void markDone(int taskNum) {
        System.out.println(markDoneString(taskNum));
    }

    /**
     * Marks the specified task as not done and returns a formatted confirmation.
     *
     * @param taskNum identifier of the task to unmark.
     * @return message describing the unmarked task.
     */
    public String unmarkDoneString(int taskNum) {
        Task unMarkTask = taskList.get(taskNum);
        unMarkTask.markNotDone();
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.BORDER).append("\n");
        sb.append("Task unmarked successfully!\n");
        sb.append(unMarkTask.toString()).append("\n");
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Unmarks a task and prints the confirmation to standard output (legacy behaviour).
     */
    public void unmarkDone(int taskNum) {
        System.out.println(unmarkDoneString(taskNum));
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
     * Returns a formatted string with search results (no printing).
     *
     * @param keyword substring to search for.
     * @return formatted search results.
     */
    public String displaySearchResultsString(String keyword) {
        HashMap<Integer, Task> searchedTaskList = searchKeyword(keyword);
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.BORDER).append("\n");
        if (!searchedTaskList.isEmpty()) {
            sb.append("These are the most relevant tasks\n");
            for (Map.Entry<Integer, Task> entry : searchedTaskList.entrySet()) {
                sb.append(String.format("%d. %s\n", entry.getKey(), entry.getValue()));
            }
        } else {
            sb.append("No relevant tasks found!\n");
        }
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Displays search results to standard output.
     *
     * @param keyword substring to search for.
     */
    public void displaySearchResults(String keyword) {
        System.out.println(displaySearchResultsString(keyword));
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

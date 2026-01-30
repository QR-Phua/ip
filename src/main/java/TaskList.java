import java.util.HashMap;
import java.util.Map;

public class TaskList {
    private HashMap<Integer,Task> taskList;
    private int counter = 1;

    public TaskList() {
        taskList = new HashMap<>();
    }

    public void getTasks() {
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        if (!taskList.isEmpty()) {
            System.out.println("You have these tasks in your list:");
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                Task currentTask = entry.getValue();
                System.out.println(String.format("%d. %s", entry.getKey(), currentTask));
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
        System.out.println(newTask.toString());
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void deleteTask(int taskNum) {
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        Task deleteTask = taskList.get(taskNum);
        taskList.remove(taskNum);
        System.out.println("The following task has been deleted!");
        System.out.println(deleteTask.toString());
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


    public HashMap<Integer, Task> searchKeyword(String keyword) {
        HashMap<Integer,Task> searchedTaskList = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
            Task currentTask = entry.getValue();
            if (currentTask.getDescription() != null && currentTask.getDescription().contains(keyword)) {
                searchedTaskList.put(entry.getKey(), currentTask);
            }
        }
        return searchedTaskList;
    }

    public void displaySearchResults(String keyword) {
        HashMap<Integer,Task> searchedTaskList = searchKeyword(keyword);
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        if (!searchedTaskList.isEmpty()) {
            System.out.println("These are the most relevant tasks");
            for (Map.Entry<Integer, Task> entry : searchedTaskList.entrySet()) {
                System.out.println(String.format("%d. %s", entry.getKey(), entry.getValue()));
            }
        } else {
            System.out.println("No relevant tasks found!");
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    // expose internal map for persistence and external inspection
    public HashMap<Integer, Task> getInternalMap() {
        return this.taskList;
    }

    // allow external code to replace the internal task map (used when loading from storage)
    public void setInternalMap(HashMap<Integer, Task> map) {
        if (map == null) return;
        this.taskList = map;
        // update counter to be one greater than the current max key
        int max = 0;
        for (Integer k : map.keySet()) {
            if (k != null && k > max) max = k;
        }
        this.counter = max + 1;
    }

}

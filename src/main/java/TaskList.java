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

    public void markDone(Task currentTask) {
        currentTask.markDone();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Task marked done successfully!");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void unmarkDone(Task currentTask) {
        currentTask.markNotDone();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Task unmarked successfully!");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    public void taskExistenceChecker(int selectedTask) throws InvalidTaskItemException {
        Task searchTask = taskList.getOrDefault(selectedTask, null);
        if (searchTask == null) {
            throw new InvalidTaskItemException();
        }
    }

    public Task get(int index) {
        return taskList.get(index);
    }
}

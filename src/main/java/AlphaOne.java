import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AlphaOne {
    private static int counter = 1;
    private static Scanner scanner = new Scanner(System.in);
    private static HashMap<Integer,Task> taskList = new HashMap<>();
    public static void main(String[] args) {
        String logo =
        """
           _      _      _      _      _      _      _      _      _  \s
         _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
        (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
         (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)\s
           _                                                       _  \s
         _( )_      _    _       _            ___                _( )_\s
        (_ o _)    / \\  | |_ __ | |__   __ _ / _ \\ _ __   ___   (_ o _)
         (_,_)    / _ \\ | | '_ \\| '_ \\ / _` | | | | '_ \\ / _ \\   (_,_)\s
           _     / ___ \\| | |_) | | | | (_| | |_| | | | |  __/     _  \s
         _( )_  /_/   \\_\\_| .__/|_| |_|\\__,_|\\___/|_| |_|\\___|   _( )_\s
        (_ o _)           |_|                                   (_ o _)
         (_,_)                                                   (_,_)\s
           _      _      _      _      _      _      _      _      _  \s
         _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
        (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
         (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)\s
        """;
        System.out.println(logo);


        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Hello! I am AlphaOne, your chatbot companion!");
        System.out.println("Tell me what you would like to do!");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");

        while (true) {

            String input = scanner.nextLine();
            String[] commands = input.split("\\s+");
            try {
                if (commands[0].equalsIgnoreCase("bye")) {
                    int expectedLength = 1;
                    commandLengthChecker(expectedLength, commands.length);
                    break;
                } else if (commands[0].equalsIgnoreCase("list")) {
                    int expectedLength = 1;
                    commandLengthChecker(expectedLength, commands.length);
                    getTasks();
                } else if (commands[0].equalsIgnoreCase("mark")) {
                    int expectedLength = 2;
                    commandLengthChecker(expectedLength, commands.length);
                    try {
                        int taskNum =  Integer.parseInt(commands[1]);
                        taskExistenceChecker(taskNum);
                        markDone(taskList.get(taskNum));
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                        System.out.println("Task marked done successfully!");
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                    } catch (InvalidTaskItemException itie) {
                        System.out.println(itie.getMessage());
                    } catch (Exception e) {
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                        System.out.println("Invalid task number!");
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                    }
                } else if (commands[0].equalsIgnoreCase("unmark")) {
                    int expectedLength = 2;
                    commandLengthChecker(expectedLength, commands.length);
                    try {
                        int taskNum =  Integer.parseInt(commands[1]);
                        taskExistenceChecker(taskNum);
                        unmarkDone(taskList.get(taskNum));
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                        System.out.println("Task unmarked successfully!");
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                    } catch (InvalidTaskItemException itie) {
                        System.out.println(itie.getMessage());
                    } catch (Exception e) {
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                        System.out.println("Invalid task number!");
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                    }

                } else {
                    addTask(input);
                }
            } catch (InvalidCommandException ice) {
                System.out.println(ice.getMessage());
            }

        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Thank you for using AlphaOne! ");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void getTasks() {
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        if (!taskList.isEmpty()) {
            System.out.printf("You have these tasks in your list:%n");
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                System.out.printf("%d. [%s] %s%n", entry.getKey(), entry.getValue().getStatus(), entry.getValue().getDescription());
            }
        } else {
            System.out.println("Your task list is currently empty!");
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void addTask(String input) {
        Task newTask = new Task(input);
        taskList.put(counter, newTask);
        counter++;
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("added: " + input);
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void markDone(Task currentTask) {
        currentTask.markDone();
    }

    private static void unmarkDone(Task currentTask) {
        currentTask.markNotDone();
    }

    private static void commandLengthChecker(int expected, int actual) throws InvalidCommandException {
        if (expected != actual) {
            throw new InvalidCommandException();
        }
    }

    private static void taskExistenceChecker(int selectedTask) throws InvalidTaskItemException {
        if (selectedTask < 0 || selectedTask > taskList.size()) {
            throw new InvalidTaskItemException();
        }
    }
}

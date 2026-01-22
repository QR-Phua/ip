import java.util.*;

public class AlphaOne {
    private static int counter = 1;
    private static Scanner scanner = new Scanner(System.in);
    private static HashMap<Integer,Task> taskList = new HashMap<>();
    public enum TaskType {TODO, DEADLINE, EVENT}
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

                    } catch (InvalidTaskItemException itie) {
                        System.out.println(itie.getMessage());
                    } catch (Exception e) {
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                        System.out.println("Invalid task number!");
                        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                    }
                } else if (commands[0].equalsIgnoreCase("todo")) {
                    if (commands.length < 2) {
                        throw new IncompleteDetailsException(TaskType.TODO);
                    }
                    addTask(todoPrep(commands), TaskType.TODO);

                } else if (commands[0].equalsIgnoreCase("deadline")) {
                    if (commands.length < 2) {
                        throw new InvalidCommandException(TaskType.DEADLINE);
                    }
                    ArrayList<String> tidied = descriptionPrep(commands, TaskType.DEADLINE);
                    addTask(tidied.get(0), TaskType.DEADLINE, tidied.get(1));

                } else if (commands[0].equalsIgnoreCase("event")) {
                    ArrayList<String> tidied = descriptionPrep(commands, TaskType.EVENT);
                    addTask(tidied.get(0), TaskType.EVENT, tidied.get(1),  tidied.get(2));
                } else {
                    throw new InvalidCommandException();
                }
            } catch (InvalidCommandException | IncompleteDetailsException exe) {
                System.out.println(exe.getMessage());
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
                Task currentTask = entry.getValue();
                System.out.printf("%d. [%s] [%s] %s%n", entry.getKey(), currentTask.getType(), currentTask.getStatus(), currentTask.getDescription());
            }
        } else {
            System.out.println("Your task list is currently empty!");
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void addTask(String input, TaskType type, String... params) {
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
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void markDone(Task currentTask) {
        currentTask.markDone();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Task marked done successfully!");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void unmarkDone(Task currentTask) {
        currentTask.markNotDone();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Task unmarked successfully!");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
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

    private static String todoPrep(String[] commands) {
        List<String> stringList = new ArrayList<>(Arrays.asList(commands));
        stringList.remove(0);
        return String.join(" ", stringList);
    }

    private static ArrayList<String> descriptionPrep(String[] commands, TaskType taskType) throws InvalidCommandException, IncompleteDetailsException  {
        switch (taskType) {
            case DEADLINE -> {
                List<String> stringList = new ArrayList<>(Arrays.asList(commands));
                stringList.remove(0);
                int finder = stringList.indexOf("/by");
                if (finder == -1 || finder == 0) {
                    throw new InvalidCommandException(taskType);
                }
                List<String> deadlineList = stringList.subList(finder +1, stringList.size());
                if (deadlineList.isEmpty()) {
                    throw new IncompleteDetailsException(taskType);
                }
                String deadline = String.join(" ", deadlineList);

                List<String> descriptionList = stringList.subList(0, finder);
                String description = String.join(" ", descriptionList);

                return new ArrayList<>(Arrays.asList(description, deadline));
            }
            case EVENT -> {
                List<String> stringList = new ArrayList<>(Arrays.asList(commands));
                stringList.remove(0);
                int finderFrom = stringList.indexOf("/from");
                int finderTo = stringList.indexOf("/to");
                if (finderTo == -1 || finderFrom == -1 || finderTo <= finderFrom + 1) {
                    throw new InvalidCommandException(taskType);
                }
                List<String> fromList = stringList.subList(finderFrom +1, finderTo);
                if (fromList.isEmpty()) {
                    throw new IncompleteDetailsException(taskType);
                }
                String fromDesc = String.join(" ", fromList);

                List<String> ToList = stringList.subList(finderTo +1, stringList.size());
                if (ToList.isEmpty()) {
                    throw new IncompleteDetailsException(taskType);
                }
                String toDesc = String.join(" ", ToList);

                List<String> descList = stringList.subList(0, finderFrom);
                String description = String.join(" ", descList);

                return new ArrayList<>(Arrays.asList(description, fromDesc, toDesc));
            }
            default -> throw new InvalidCommandException();
        }

    }
}

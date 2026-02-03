package alphaone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import alphaone.ui.Ui;
import alphaone.parser.Parser;
import alphaone.storage.Storage;
import alphaone.model.Task;
import alphaone.model.TaskList;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidDateTimeException;
import alphaone.exception.InvalidTaskItemException;
import alphaone.exception.IncompleteDetailsException;


/**
 * AlphaOne is the main application class that coordinates user input, parsing,
 * task management and persistence.
 * <p>
 * It holds the application's {@code TaskList} and {@code Storage} instances and
 * contains the interactive loop implemented in {@link #run()}.
 */
public class AlphaOne {
    private final TaskList taskList;
    private final Storage storage;

    public enum TaskType {TODO, DEADLINE, EVENT}
    public enum CommandType {BYE, LIST, UNMARK, MARK, DELETE, FIND}

    public AlphaOne() {
        this.storage = new Storage();
        // taskList will initialize its own storage if required
        this.taskList = new TaskList();
        // Optionally, you might want to merge storage.load() output into taskList here
    }

    /**
     * Start the interactive application loop.
     * <p>
     * This method prints the welcome banner, loads any saved tasks from
     * {@link alphaone.storage.Storage}, processes commands until the user
     * issues the {@code bye} command, and then persists the task list.
     */
    public void run() {
        System.out.println(Ui.printLogo());
        System.out.println(Ui.BORDER);
        System.out.println("Hello! I am AlphaOne, your chatbot companion!");
        System.out.println("Tell me what you would like to do!");
        System.out.println(Ui.BORDER);

        // load tasks from storage if needed (kept for compatibility)
        HashMap<Integer, Task> loaded = storage.load();
        if (!loaded.isEmpty()) {
            this.taskList.setInternalMap(loaded);
        }

        while (true) {
            String input = Ui.readLine();
            String[] commands = Parser.splitInput(input);
            try {
                if (commands[0].equalsIgnoreCase("bye")) {
                    commandLengthChecker(commands.length, CommandType.BYE);
                    break;
                } else if (commands[0].equalsIgnoreCase("list")) {
                    commandLengthChecker(commands.length, CommandType.LIST);
                    taskList.getTasks();
                } else if (commands[0].equalsIgnoreCase("mark")) {
                    commandLengthChecker(commands.length, CommandType.MARK);
                    try {
                        int taskNum = Integer.parseInt(commands[1]);
                        taskList.taskExistenceChecker(taskNum);
                        taskList.markDone(taskNum);
                    } catch (InvalidTaskItemException itie) {
                        System.out.println(itie.getMessage());
                    } catch (Exception e) {
                        System.out.println(Ui.BORDER);
                        System.out.println("Invalid task number!");
                        System.out.println(Ui.BORDER);
                    }
                } else if (commands[0].equalsIgnoreCase("unmark")) {
                    commandLengthChecker(commands.length, CommandType.UNMARK);
                    try {
                        int taskNum = Integer.parseInt(commands[1]);
                        taskList.taskExistenceChecker(taskNum);
                        taskList.unmarkDone(taskNum);

                    } catch (InvalidTaskItemException itie) {
                        System.out.println(itie.getMessage());
                    } catch (Exception e) {
                        System.out.println(Ui.BORDER);
                        System.out.println("Invalid task number!");
                        System.out.println(Ui.BORDER);
                    }
                } else if (commands[0].equalsIgnoreCase("delete")) {
                    commandLengthChecker(commands.length, CommandType.DELETE);
                    try {
                        int taskNum = Integer.parseInt(commands[1]);
                        taskList.taskExistenceChecker(taskNum);
                        taskList.deleteTask(taskNum);
                    } catch (InvalidTaskItemException itie) {
                        System.out.println(itie.getMessage());
                    } catch (Exception e) {
                        System.out.println(Ui.BORDER);
                        System.out.println("Invalid task number!");
                        System.out.println(Ui.BORDER);
                    }
                } else if (commands[0].equalsIgnoreCase("find")) {
                    commandLengthChecker(commands.length, CommandType.FIND);
                    String keyword = Parser.joinFromIndex(commands, 1);
                    if (keyword.isEmpty()) {
                        throw new InvalidCommandException(CommandType.FIND);
                    }
                    taskList.displaySearchResults(keyword);

                } else if (commands[0].equalsIgnoreCase("todo")) {
                    if (commands.length < 2) {
                        throw new IncompleteDetailsException(TaskType.TODO);
                    }
                    taskList.addTask(todoPrep(commands), TaskType.TODO);

                } else if (commands[0].equalsIgnoreCase("deadline")) {
                    if (commands.length < 2) {
                        throw new InvalidCommandException(TaskType.DEADLINE);
                    }
                    ArrayList<String> tidied = Parser.descriptionPrep(commands, TaskType.DEADLINE);
                    taskList.addTask(tidied.get(0), TaskType.DEADLINE, tidied.get(1));

                } else if (commands[0].equalsIgnoreCase("event")) {
                    ArrayList<String> tidied = Parser.descriptionPrep(commands, TaskType.EVENT);
                    taskList.addTask(tidied.get(0), TaskType.EVENT, tidied.get(1), tidied.get(2));
                } else {
                    throw new InvalidCommandException();
                }
            } catch (InvalidCommandException | IncompleteDetailsException | InvalidDateTimeException exe) {
                System.out.println(exe.getMessage());
            }
        }

        // persist to storage
        storage.save(taskList.getInternalMap());
        System.out.println(Ui.BORDER);
        System.out.println("Thank you for using AlphaOne! ");
        System.out.println(Ui.BORDER);
    }

    private void commandLengthChecker(int actual, CommandType type) throws InvalidCommandException {
        int expected;
        switch (type) {
            case BYE, LIST -> expected = 1;
            case MARK, UNMARK, DELETE, FIND-> expected = 2;
            default -> throw new InvalidCommandException();
        }
        if (expected != actual) {
            throw new InvalidCommandException(type);
        }
    }

    private String todoPrep(String[] commands) {
        List<String> stringList = new ArrayList<>(Arrays.asList(commands));
        stringList.remove(0);
        return String.join(" ", stringList);
    }

    // standard entry point so `java AlphaOne` works
    /**
     * Program entry point that creates the application instance and runs it.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        new AlphaOne().run();
    }
}

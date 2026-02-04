package alphaone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidDateTimeException;
import alphaone.exception.InvalidTaskItemException;
import alphaone.model.Task;
import alphaone.model.TaskList;
import alphaone.parser.Parser;
import alphaone.storage.Storage;
import alphaone.ui.Ui;

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
    private boolean isExit = false;

    /**
     * Types of tasks supported by the application.
     *
     * <p>TODO — a task without an associated date/time.
     * DEADLINE — a task with a due date.
     * EVENT — a task with a start and end time.</p>
     */
    public enum TaskType { TODO, DEADLINE, EVENT }
    /**
     * Command keywords recognized by the parser.
     *
     * <p>BYE — exit the application.
     * LIST — display all tasks.
     * UNMARK — mark a task as not done.
     * MARK — mark a task as done.
     * DELETE — remove a task by index.
     * FIND — search tasks by keyword.</p>
     */
    public enum CommandType { BYE, LIST, UNMARK, MARK, DELETE, FIND }
    /**
     * Creates a new AlphaOne application instance.
     *
     * <p>Initialises storage and the task list. The task list will
     * initialise its own storage if required. Storage is used to
     * persist and load tasks from disk. Loading occurs here so GUI instances
     * created via JavaFX will have the persisted tasks available immediately.</p>
     */
    public AlphaOne() {
        this.storage = new Storage();
        // taskList will initialize its own storage if required
        this.taskList = new TaskList();
        // Load persisted tasks immediately so GUI can display them without calling run().
        HashMap<Integer, Task> loaded = storage.load();
        if (!loaded.isEmpty()) {
            this.taskList.setInternalMap(loaded);
        }
    }

    /**
     * Returns the standard startup message that the CLI prints. GUI can use this
     * to show the same welcome content.
     *
     * @return a string containing logo, borders and welcome text.
     */
    public String getStartupMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(Ui.printLogo()).append("\n");
        sb.append(Ui.BORDER).append("\n");
        sb.append("Hello! I am AlphaOne, your chatbot companion!\n");
        sb.append("Tell me what you would like to do!\n");
        sb.append(Ui.BORDER);
        return sb.toString();
    }

    /**
     * Returns the formatted tasks list suitable for display (may indicate empty list).
     *
     * @return formatted task list string
     */
    public String getTaskListString() {
        return taskList.getTasksString();
    }

    /**
     * Returns true if the last processed command was an exit command.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Process a line of input and return a textual response. This centralised
     * method allows both the CLI and GUI to reuse the same command logic.
     *
     * @param input user input string
     * @return textual response to display to the user
     */
    public String getResponse(String input) {
        String[] commands = Parser.splitInput(input);
        try {
            if (commands[0].equalsIgnoreCase("bye")) {
                commandLengthChecker(commands.length, CommandType.BYE);
                // persist to storage before exiting
                storage.save(taskList.getInternalMap());
                isExit = true;
                return Ui.BORDER + "\nThank you for using AlphaOne! \n" + Ui.BORDER;
            } else if (commands[0].equalsIgnoreCase("list")) {
                commandLengthChecker(commands.length, CommandType.LIST);
                return taskList.getTasksString();
            } else if (commands[0].equalsIgnoreCase("mark")) {
                commandLengthChecker(commands.length, CommandType.MARK);
                try {
                    int taskNum = Integer.parseInt(commands[1]);
                    taskList.taskExistenceChecker(taskNum);
                    return taskList.markDoneString(taskNum);
                } catch (InvalidTaskItemException itie) {
                    return itie.getMessage();
                } catch (Exception e) {
                    return Ui.BORDER + "\nInvalid task number!\n" + Ui.BORDER;
                }
            } else if (commands[0].equalsIgnoreCase("unmark")) {
                commandLengthChecker(commands.length, CommandType.UNMARK);
                try {
                    int taskNum = Integer.parseInt(commands[1]);
                    taskList.taskExistenceChecker(taskNum);
                    return taskList.unmarkDoneString(taskNum);

                } catch (InvalidTaskItemException itie) {
                    return itie.getMessage();
                } catch (Exception e) {
                    return Ui.BORDER + "\nInvalid task number!\n" + Ui.BORDER;
                }
            } else if (commands[0].equalsIgnoreCase("delete")) {
                commandLengthChecker(commands.length, CommandType.DELETE);
                try {
                    int taskNum = Integer.parseInt(commands[1]);
                    taskList.taskExistenceChecker(taskNum);
                    return taskList.deleteTaskString(taskNum);
                } catch (InvalidTaskItemException itie) {
                    return itie.getMessage();
                } catch (Exception e) {
                    return Ui.BORDER + "\nInvalid task number!\n" + Ui.BORDER;
                }
            } else if (commands[0].equalsIgnoreCase("find")) {
                commandLengthChecker(commands.length, CommandType.FIND);
                String keyword = Parser.joinFromIndex(commands, 1);
                if (keyword.isEmpty()) {
                    throw new InvalidCommandException(CommandType.FIND);
                }
                return taskList.displaySearchResultsString(keyword);

            } else if (commands[0].equalsIgnoreCase("todo")) {
                if (commands.length < 2) {
                    throw new IncompleteDetailsException(TaskType.TODO);
                }
                return taskList.addTaskString(todoPrep(commands), TaskType.TODO);

            } else if (commands[0].equalsIgnoreCase("deadline")) {
                if (commands.length < 2) {
                    throw new InvalidCommandException(TaskType.DEADLINE);
                }
                ArrayList<String> tidiedDescription = Parser.descriptionPrep(commands, TaskType.DEADLINE);
                return taskList.addTaskString(tidiedDescription.get(0), TaskType.DEADLINE, tidiedDescription.get(1));

            } else if (commands[0].equalsIgnoreCase("event")) {
                ArrayList<String> tidiedDescription = Parser.descriptionPrep(commands, TaskType.EVENT);
                return taskList.addTaskString(tidiedDescription.get(0), TaskType.EVENT,
                        tidiedDescription.get(1), tidiedDescription.get(2));
            } else {
                throw new InvalidCommandException();
            }
        } catch (InvalidCommandException | IncompleteDetailsException | InvalidDateTimeException exe) {
            return exe.getMessage();
        }
    }

    /**
     * Generates a simple response for the user's chat message (used in tests).
     */
    public String getResponseSimple(String input) {
        return "AlphaOne heard: " + input;
    }

    /**
     * Start the interactive application loop (CLI). Uses getResponse so GUI and CLI
     * share the same command processing.
     */
    public void run() {
        System.out.println(getStartupMessage());

        while (true) {
            String input = Ui.readLine();
            String response = getResponse(input);
            System.out.println(response);
            if (isExit) {
                break;
            }
        }
    }

    private void commandLengthChecker(int actual, CommandType type) throws InvalidCommandException {
        int expectedLength = actual;
        switch (type) {
        case BYE, LIST -> expectedLength = 1;
        case MARK, UNMARK, DELETE, FIND-> expectedLength = 2;
        default -> throw new InvalidCommandException();
        }
        if (expectedLength != actual) {
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

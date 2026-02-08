package alphaone;

import java.util.HashMap;

import alphaone.model.Task;
import alphaone.model.TaskList;
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
    private final CommandProcessor commandProcessor;

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
        // create the processor after loading so it has immediate access to tasks
        this.commandProcessor = new CommandProcessor(this.taskList, this.storage);
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
     * Returns true if the last processed command was an exit command.
     */
    public boolean isExit() {
        return commandProcessor.isExit();
    }

    /**
     * Process a line of input and return a textual response. This centralised
     * method allows both the CLI and GUI to reuse the same command logic.
     *
     * @param input user input string
     * @return textual response to display to the user
     */
    public String getResponse(String input) {
        // delegate parsing & execution to the CommandProcessor
        return commandProcessor.process(input);
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
            if (commandProcessor.isExit()) {
                break;
            }
        }
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

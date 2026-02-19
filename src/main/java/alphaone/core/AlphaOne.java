package alphaone.core;

import alphaone.model.ContactList;
import alphaone.model.TaskList;
import alphaone.storage.Storage;
import alphaone.ui.Ui;

/**
 * AlphaOne is the main application class that coordinates user input, parsing,
 * task management and persistence.
 */
public class AlphaOne {
    private final TaskList taskList;
    private final Storage storage;
    private final ContactList contactList;
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
    public enum CommandType { CONTACT_ADD, CONTACT_DELETE, BYE, LIST, UNMARK, MARK, DELETE, FIND }

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
        this.contactList = new ContactList(this.storage);
        this.taskList = new TaskList(this.storage);
        this.commandProcessor = new CommandProcessor(this.taskList, this.storage, this.contactList);
    }

    /**
     * Returns the raw startup message (no borders); the UI layer will add presentation.
     * Includes the ASCII logo — used by the CLI.
     */
    public String getStartupMessage() {
        return Ui.getLogo() + "\n"
                + "Hello! I am AlphaOne, your chatbot companion!\n"
                + "Tell me what you would like to do!\n";
    }

    /**
     * Returns a clean greeting for the GUI — no ASCII logo.
     */
    public String getGuiGreeting() {
        return "Hello! I'm AlphaOne 👋\nWhat can I help you with today?";
    }

    /**
     * Returns true if the last processed command was an exit command.
     *
     * @return true if the last command was a bye/exit command, false otherwise.
     */
    public boolean isExit() {
        return commandProcessor.isExit();
    }

    /**
     * Delegates the given user input to the command processor for execution.
     *
     * @param input the raw user input line to process
     */
    public void handleInput(String input) {
        commandProcessor.process(input);
    }

    /**
     * Starts the interactive application loop (CLI).
     */
    public void run() {
        Ui.print(getStartupMessage());
        while (!commandProcessor.isExit()) {
            String input = Ui.readLine();
            handleInput(input);
        }
    }

    /**
     * Program entry point that creates the application instance and runs it.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        new AlphaOne().run();
    }
}


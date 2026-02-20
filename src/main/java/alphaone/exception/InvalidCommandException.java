package alphaone.exception;

import alphaone.core.AlphaOne;

/**
 * Signals that a user command was invalid or malformed.
 */
public class InvalidCommandException extends Exception {
    private final AlphaOne.TaskType taskType;
    private final AlphaOne.CommandType commandType;

    /**
     * Creates a generic invalid command exception with a default message.
     */
    public InvalidCommandException() {
        super("Invalid command! Please try again.");
        this.taskType = null;
        this.commandType = null;
    }

    /**
     * Creates an InvalidCommandException for a specific task type (e.g. TODO).
     *
     * @param taskType the task type that had a malformed command.
     */
    public InvalidCommandException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
        this.commandType = null;
    }

    /**
     * Creates an InvalidCommandException for a specific command type (e.g. MARK).
     *
     * @param commandType the command type that was invalid.
     */
    public InvalidCommandException(AlphaOne.CommandType commandType) {
        super();
        this.commandType = commandType;
        this.taskType = null;
    }

    /**
     * Returns a user-friendly error message describing what went wrong and how to correct it.
     * The message is tailored to the specific task type or command type that caused the error.
     *
     * @return descriptive error message string
     */
    @Override
    public String getMessage() {
        if (taskType != null) {
            return getTaskTypeMessage(taskType);
        }
        if (commandType != null) {
            return getCommandTypeMessage(commandType);
        }
        return super.getMessage();
    }

    /** Returns the error message for task-type-based command errors. */
    private String getTaskTypeMessage(AlphaOne.TaskType type) {
        switch (type) {
        case TODO -> {
            return "Invalid TODO command! Please try again.\n"
                    + "Example: todo [task description]";
        }
        case DEADLINE -> {
            return "Invalid DEADLINE command! Please try again.\n"
                    + "Example: deadline [task description] /by [task due date (e.g., 2025-02-19)]";
        }
        case EVENT -> {
            return "Invalid EVENT command! Please try again.\n"
                    + "Example: event [event description] /from [date time (e.g., 2026-02-19 1430)]"
                    + " /to [date time (e.g., 2026-02-20 1430)]";
        }
        default -> {
            return super.getMessage();
        }
        }
    }

    /** Returns the error message for command-type-based command errors. */
    private String getCommandTypeMessage(AlphaOne.CommandType type) {
        switch (type) {
        case MARK -> {
            return "Invalid MARK command! Please try again.\n"
                    + "Example: mark [task number]";
        }
        case UNMARK -> {
            return "Invalid UNMARK command! Please try again.\n"
                    + "Example: unmark [task number]";
        }
        case DELETE -> {
            return "Invalid DELETE command! Please try again.\n"
                    + "Example: delete [task number]";
        }
        case BYE -> {
            return "Invalid BYE command! No other parameters required.\n"
                    + "Example: bye";
        }
        case LIST -> {
            return "Invalid LIST command! No other parameters required.\n"
                    + "Example: list";
        }
        case FIND -> {
            return "Invalid FIND command! Please enter keyword(s)\n"
                    + "Example: find assignment";
        }
        case CONTACT -> {
            return """
                    Invalid CONTACT command! Available actions: add, remove, list
                    Examples:
                      contact add John Doe 91234567
                      contact remove John Doe
                      contact list""";
        }
        case CONTACT_ADD -> {
            return """
                    Invalid CONTACT ADD command!
                    Format: contact add [name] [phone]
                    Example: contact add John Doe 91234567""";
        }
        case CONTACT_DELETE -> {
            return """
                    Invalid CONTACT REMOVE command!
                    Format: contact remove [name]
                    Example: contact remove John Doe""";
        }
        default -> {
            return super.getMessage();
        }
        }
    }
}

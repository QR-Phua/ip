package alphaone.exception;

import alphaone.AlphaOne;

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

    @Override
    public String getMessage() {
        if (taskType != null) {
            switch (taskType) {
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
        } else if (commandType != null) {
            switch (commandType) {
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
            default -> {
                return super.getMessage();
            }
            }
        }

        return super.getMessage();
    }
}

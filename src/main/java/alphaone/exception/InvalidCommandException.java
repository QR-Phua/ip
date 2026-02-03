package alphaone.exception;

import alphaone.AlphaOne;
import alphaone.ui.Ui;

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
        super(Ui.BORDER + "\n" + "Invalid command! Please try again." + "\n" + Ui.BORDER);
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
                return Ui.BORDER + "\n" + "Invalid TODO command! Please try again.\n"
                        + "Example: todo [task description]" + "\n" + Ui.BORDER;
            }
            case DEADLINE -> {
                return Ui.BORDER + "\n" + "Invalid DEADLINE command! Please try again.\n"
                        + "Example: deadline [task description] /by [task due date]" + "\n" + Ui.BORDER;
            }
            case EVENT -> {
                return Ui.BORDER + "\n" + "Invalid EVENT command! Please try again.\n"
                        + "Example: event [event description] /from [date time] /to [date time]" + "\n" + Ui.BORDER;
            }
            default -> {
                return super.getMessage();
            }
            }
        } else if (commandType != null) {
            switch (commandType) {
            case MARK -> {
                return Ui.BORDER + "\n" + "Invalid MARK command! Please try again.\n"
                        + "Example: mark [task number]" + "\n" + Ui.BORDER;
            }
            case UNMARK -> {
                return Ui.BORDER + "\n" + "Invalid UNMARK command! Please try again.\n"
                        + "Example: unmark [task number]" + "\n" + Ui.BORDER;
            }
            case DELETE -> {
                return Ui.BORDER + "\n" + "Invalid DELETE command! Please try again.\n"
                        + "Example: delete [task number]" + "\n" + Ui.BORDER;
            }
            case BYE -> {
                String msg = "Invalid BYE command! No other parameters required." + "\n" + "Example: bye";
                return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
            }
            case LIST -> {
                String msg = "Invalid LIST command! No other parameters required." + "\n" + "Example: list";
                return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
            } case FIND -> {
                String msg = "Invalid FIND command! Please enter keyword(s)" + "\n" + "Example: find assignment";
                return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
            }
            default -> {
                return super.getMessage();
            }
            }
        }

        return super.getMessage();
    }
}

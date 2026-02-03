package alphaone.exception;

import alphaone.AlphaOne;
import alphaone.ui.Ui;

/**
 * Signals that a provided date/time string is invalid for the expected task type.
 */
public class InvalidDateTimeException extends Exception {
    private final AlphaOne.TaskType taskType;

    /**
     * Construct an InvalidDateTimeException indicating the task type that required
     * a different format.
     *
     * @param taskType the type of task for which validation failed.
     */
    public InvalidDateTimeException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
    }

    @Override
    public String getMessage() {
        switch (taskType) {
        case DEADLINE -> {
            String msg = "Datetime information provided is invalid!" + "\n"
                    + "For Deadline tasks, use YYYY-MM-DD";
            return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
        }
        case EVENT -> {
            String msg = "Datetime information provided is invalid!" + "\n"
                    + "For Event tasks, use YYYY-MM-DD HHMM";
            return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
        }
        default -> {
            String msg = "Datetime information provided is invalid!";
            return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
        }
        }
    }

}

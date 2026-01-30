package alphaone.exception;

import alphaone.AlphaOne;
import alphaone.ui.Ui;

/**
 * Signals that a command required additional details (e.g. /by, /from, /to) that were missing.
 */
public class IncompleteDetailsException extends Exception{
    private final AlphaOne.TaskType taskType;

    /**
     * Create an IncompleteDetailsException indicating which task type was missing details.
     *
     * @param taskType the task type with incomplete details.
     */
    public IncompleteDetailsException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
    }

    @Override
    public String getMessage() {
        switch (taskType) {
            case TODO -> {
                String msg = "Incomplete details to create task!" + "\n"
                        + "Please add in what you would like to do?" + "\n"
                        + "Example: todo cook a feast";
                return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
            }
            case DEADLINE -> {
                String msg = "Incomplete details to create task!" + "\n"
                        + "Please add in what you would like to do followed with /by to set the deadline." + "\n"
                        + "Example: deadline write report /by tomorrow evening";
                return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
            }
            case EVENT -> {
                String msg = "Incomplete details to create task!" + "\n"
                        + "Please add in what you would like to do followed with /from and /to to set the duration?"
                        + "\n" + "Example: event attend wedding on saturday /from 12pm /to 6pm";
                return Ui.BORDER + "\n" + msg + "\n" + Ui.BORDER;
            }
            default -> {
                return Ui.BORDER + "\n" + "Incomplete details to create task!" + "\n" + Ui.BORDER;
            }
        }
    }
}

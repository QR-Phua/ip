package alphaone.exception;

import alphaone.AlphaOne;

/**
 * Signals that a command required additional details (e.g. /by, /from, /to) that were missing.
 */
public class IncompleteDetailsException extends Exception {
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
            return "Incomplete details to create task!\n"
                    + "Please add in what you would like to do?\n"
                    + "Example: todo cook a feast";
        }
        case DEADLINE -> {
            return "Incomplete details to create task!\n"
                    + "Please add in what you would like to do followed with /by to set the deadline.\n"
                    + "Example: deadline write report /by 2025-02-19";
        }
        case EVENT -> {
            return "Incomplete details to create task!\n"
                    + "Please add in what you would like to do followed with /from and /to to set the duration?\n"
                    + "Example: event attend wedding on saturday /from 2026-02-19 1430 /to 2026-02-20 1430";
        }
        default -> {
            return "Incomplete details to create task!";
        }
        }
    }
}

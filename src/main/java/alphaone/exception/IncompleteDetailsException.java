package alphaone.exception;

import alphaone.core.AlphaOne;

/**
 * Signals that a command required additional details (e.g. /by, /from, /to) that were missing.
 */
public class IncompleteDetailsException extends Exception {
    private final AlphaOne.TaskType taskType;

    /**
     * Creates an IncompleteDetailsException indicating which task type was missing details.
     *
     * @param taskType the task type with incomplete details.
     */
    public IncompleteDetailsException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
    }

    /**
     * Returns a user-friendly error message explaining which required details were missing
     * and provides a usage example for the relevant task type.
     *
     * @return descriptive error message string
     */
    @Override
    public String getMessage() {
        switch (taskType) {
        case TODO -> {
            return """
                    Incomplete details to create todo task!
                    Please add in todo description.
                    Example: todo cook a feast""";
        }
        case DEADLINE -> {
            return """
                    Incomplete details to create deadline task!
                    Please add in deadline description followed with /by to set the deadline.
                    Example: deadline write report /by 2025-02-19""";
        }
        case EVENT -> {
            return """
                    Incomplete details to create event task!
                    Please add in event description followed with /from and /to to set the duration.
                    Example: event attend wedding on Saturday /from 2026-02-19 1430 /to 2026-02-20 1430""";
        }
        default -> {
            return "Incomplete details to create task!";
        }
        }
    }
}

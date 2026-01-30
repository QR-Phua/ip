package alphaone.exception;

import alphaone.AlphaOne;

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
                return ("""
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    Incomplete details to create task!
                    Please add in what you would like to do?
                    Example: todo cook a feast
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    """).stripTrailing();
            }
            case DEADLINE -> {
                return ("""
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    Incomplete details to create task!
                    Please add in what you would like to do followed with /by to set the deadline.
                    Example: deadline write report /by tomorrow evening
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    """).stripTrailing();
            }
            case EVENT -> {
                return ("""
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    Incomplete details to create task!
                    Please add in what you would like to do followed with /from and /to to set the duration?
                    Example: event attend wedding on saturday /from 12pm /to 6pm
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    """).stripTrailing();
            }
            default -> {
                return ("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Incomplete details to create task!
                %s
                +––––––––––––––––––––––––––––––––––––––––––––––+""").stripTrailing();
            }
        }
    }
}

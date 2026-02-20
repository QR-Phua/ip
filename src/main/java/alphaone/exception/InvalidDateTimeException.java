package alphaone.exception;

import alphaone.core.AlphaOne;

/**
 * Signals that a provided date/time string is invalid for the expected task type.
 */
public class InvalidDateTimeException extends Exception {

    /** Distinguishes the kind of datetime error so the correct message is produced. */
    public enum Reason { FORMAT, EVENT_ORDER }

    private final AlphaOne.TaskType taskType;
    private final Reason reason;

    /**
     * Constructs an InvalidDateTimeException for a format validation failure.
     *
     * @param taskType the type of task for which validation failed.
     */
    public InvalidDateTimeException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
        this.reason = Reason.FORMAT;
    }

    /**
     * Constructs an InvalidDateTimeException with a specific failure reason.
     *
     * @param taskType the type of task for which validation failed.
     * @param reason   the specific reason for the failure.
     */
    public InvalidDateTimeException(AlphaOne.TaskType taskType, Reason reason) {
        super();
        this.taskType = taskType;
        this.reason = reason;
    }

    /**
     * Returns a user-friendly error message describing the datetime failure.
     *
     * @return descriptive error message string
     */
    @Override
    public String getMessage() {
        if (reason == Reason.EVENT_ORDER) {
            return "Event start time must be before end time.\n"
                    + "Example: event meeting /from 2026-03-10 1400 /to 2026-03-10 1600";
        }
        switch (taskType) {
        case DEADLINE -> {
            return "Datetime information provided is invalid!\n"
                    + "For Deadline tasks, use YYYY-MM-DD (e.g., 2025-02-19)";
        }
        case EVENT -> {
            return "Datetime information provided is invalid!\n"
                    + "For Event tasks, use YYYY-MM-DD HHMM (e.g., 2026-02-19 1430)";
        }
        default -> {
            return "Datetime information provided is invalid!";
        }
        }
    }

}

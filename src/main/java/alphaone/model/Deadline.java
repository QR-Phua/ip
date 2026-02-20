package alphaone.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import alphaone.util.Constants;

/**
 * Task that has an associated deadline date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("MMMM dd, yyyy", Locale.ENGLISH);
    private final LocalDate deadlineDate;

    /**
     * Creates a Deadline with a description and a date string.
     *
     * @param description  task description.
     * @param rawDate      date string in ISO format (YYYY-MM-DD).
     */
    public Deadline(String description, String rawDate) {
        super(description);
        this.deadlineDate = LocalDate.parse(rawDate);
    }

    /**
     * Creates a Deadline from stored state.
     *
     * @param wasDone      whether task was previously completed.
     * @param description  the stored description.
     * @param rawDate      the stored date string.
     */
    public Deadline(boolean wasDone, String description, String rawDate) {
        super(description);
        this.deadlineDate = LocalDate.parse(rawDate);
        if (wasDone) {
            this.markDone();
        }
    }

    /**
     * Returns the short type identifier for Deadline.
     *
     * @return "D"
     */
    @Override
    public String getType() {
        return "D";
    }

    /**
     * Returns the parsed deadline as LocalDate.
     *
     * @return the deadline date.
     */
    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    /**
     * Returns a human-readable string of the form {@code [D] [status] description (by: date)}.
     *
     * @return formatted display string
     */
    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (by: %s)", this.getType(),
                this.getStatusIcon(), super.getDescription(), deadlineDate.format(DISPLAY_DATE_FORMATTER));
    }

    /**
     * Serialises this Deadline to a storage line in the format
     * {@code D!@!<done>!@!<description>!@!<YYYY-MM-DD>}.
     *
     * @return serialised storage string
     */
    @Override
    public String serialiseTask() {
        return this.getType() + Constants.STORAGE_SEPARATOR
                + this.isDone() + Constants.STORAGE_SEPARATOR
                + this.getDescription() + Constants.STORAGE_SEPARATOR
                + deadlineDate;
    }
}

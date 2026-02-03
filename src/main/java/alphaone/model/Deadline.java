package alphaone.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Task that has an associated deadline date.
 */
public class Deadline extends Task {
    private final LocalDate deadline;
    private final DateTimeFormatter stringDateTimeFormatter = DateTimeFormatter
            .ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

    /**
     * Creates a Deadline with a description and a date string.
     *
     * @param description task description.
     * @param deadline date string in ISO format (YYYY-MM-DD).
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = LocalDate.parse(deadline);
    }

    /**
     * Creates a Deadline from stored state.
     *
     * @param wasDone whether task was previously completed.
     * @param description the stored description.
     * @param deadline the stored date string.
     */
    public Deadline(boolean wasDone, String description, String deadline) {
        super(description);
        this.deadline = LocalDate.parse(deadline);
        if (wasDone) {
            this.markDone();
        }
    }

    /**
     * Returns the short type identifier for Deadline.
     *
     * @return the single-letter type code.
     */
    public String getType() {
        return ("D");
    }

    /**
     * Returns the parsed deadline as LocalDate.
     *
     * @return the deadline date.
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (by: %s)", this.getType(),
                this.getStatus(), super.getDescription(), deadline.format(stringDateTimeFormatter));
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s!@!%s", this.getType(), this.isDone(), this.getDescription(), deadline);
    }
}

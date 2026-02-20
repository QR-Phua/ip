package alphaone.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import alphaone.util.Constants;

/**
 * Task representing an event with a start and end datetime.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("MMMM dd, yyyy hh:mm a", Locale.ENGLISH);
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an Event from user input (parses strings using yyyy-MM-dd HHmm format).
     *
     * @param description   event description.
     * @param startDateTime start datetime string in pattern yyyy-MM-dd HHmm.
     * @param endDateTime   end datetime string in pattern yyyy-MM-dd HHmm.
     */
    public Event(String description, String startDateTime, String endDateTime) {
        super(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.INPUT_DATETIME_PATTERN);
        this.startDateTime = LocalDateTime.parse(startDateTime, formatter);
        this.endDateTime = LocalDateTime.parse(endDateTime, formatter);
    }

    /**
     * Reconstructs an Event from stored state.
     *
     * @param wasDone          whether the event was previously marked done.
     * @param description      stored description.
     * @param storedStartDateTime stored start datetime (ISO format).
     * @param storedEndDateTime   stored end datetime (ISO format).
     */
    public Event(boolean wasDone, String description, String storedStartDateTime, String storedEndDateTime) {
        super(description);
        this.startDateTime = LocalDateTime.parse(storedStartDateTime);
        this.endDateTime = LocalDateTime.parse(storedEndDateTime);
        if (wasDone) {
            this.markDone();
        }
    }

    /**
     * Returns the short type identifier for Event.
     *
     * @return "E"
     */
    @Override
    public String getType() {
        return "E";
    }

    /**
     * Returns the parsed start datetime.
     *
     * @return the event start as LocalDateTime.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the parsed end datetime.
     *
     * @return the event end as LocalDateTime.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Returns a human-readable string of the form
     * {@code [E] [status] description (from <start> to <end>)}.
     *
     * @return formatted display string
     */
    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (from %s to %s)", this.getType(),
                this.getStatusIcon(), super.getDescription(),
                startDateTime.format(DISPLAY_DATE_FORMATTER),
                endDateTime.format(DISPLAY_DATE_FORMATTER));
    }

    /**
     * Serialises this Event to a storage line in the format
     * {@code E!@!<done>!@!<description>!@!<startISO>!@!<endISO>}.
     *
     * @return serialised storage string
     */
    @Override
    public String serialiseTask() {
        return this.getType() + Constants.STORAGE_SEPARATOR
                + this.isDone() + Constants.STORAGE_SEPARATOR
                + this.getDescription() + Constants.STORAGE_SEPARATOR
                + startDateTime + Constants.STORAGE_SEPARATOR
                + endDateTime;
    }
}

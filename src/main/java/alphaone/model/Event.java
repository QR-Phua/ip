package alphaone.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Task representing an event with a start and end datetime.
 */
public class Event extends Task {
    private final LocalDateTime eventStart;
    private final LocalDateTime eventEnd;
    private final DateTimeFormatter stringDateTimeFormatter = DateTimeFormatter
            .ofPattern("MMMM dd, yyyy h:mm a", Locale.ENGLISH);

    /**
     * Create an Event from user input (parsing using yyyy-MM-dd HHmm).
     *
     * @param description event description.
     * @param eventStart start datetime string in pattern yyyy-MM-dd HHmm.
     * @param eventEnd end datetime string in pattern yyyy-MM-dd HHmm.
     */
    public Event(String description, String eventStart, String eventEnd) {
        super(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        this.eventStart = LocalDateTime.parse(eventStart, formatter);
        this.eventEnd = LocalDateTime.parse(eventEnd, formatter);
    }

    /**
     * Reconstruct an Event from stored state.
     *
     * @param wasDone whether the event was previously marked done.
     * @param description stored description.
     * @param eventStart stored start datetime.
     * @param eventEnd stored end datetime.
     */
    public Event(boolean wasDone, String description, String eventStart, String eventEnd) {
        super(description);
        this.eventStart = LocalDateTime.parse(eventStart);
        this.eventEnd = LocalDateTime.parse(eventEnd);
        if (wasDone) {
            this.markDone();
        }
    }

    /**
     * Returns the short type identifier for Event.
     *
     * @return the single-letter type code.
     */
    public String getType() {
        return ("E");
    }

    /**
     * Returns the parsed start datetime.
     *
     * @return the event start as LocalDateTime.
     */
    public LocalDateTime getEventStart() {
        return eventStart;
    }

    /**
     * Returns the parsed end datetime.
     *
     * @return the event end as LocalDateTime.
     */
    public LocalDateTime getEventEnd() {
        return eventEnd;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (from %s to %s)", this.getType(),
                this.getStatus(), super.getDescription(), eventStart.format(stringDateTimeFormatter),
                eventEnd.format(stringDateTimeFormatter));
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s!@!%s!@!%s", this.getType(), this.isDone(),
                this.getDescription(), eventStart, eventEnd);
    }

}

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Event extends Task {
    private final LocalDateTime eventStart;
    private final LocalDateTime eventEnd;
    private final DateTimeFormatter stringDateTimeFormatter = DateTimeFormatter
            .ofPattern("MMMM dd, yyyy h:mm a", Locale.ENGLISH);

    public Event(String description, String eventStart, String eventEnd) {
        super(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        this.eventStart = LocalDateTime.parse(eventStart, formatter);
        this.eventEnd = LocalDateTime.parse(eventEnd, formatter);
    }

    public Event(boolean wasDone, String description, String eventStart, String eventEnd) {
        super(description);
        this.eventStart = LocalDateTime.parse(eventStart);
        this.eventEnd = LocalDateTime.parse(eventEnd);
        if (wasDone) {
            this.markDone();
        }
    }

    public String getType() {
        return ("E");
    }

    public LocalDateTime getEventStart() {
        return eventStart;
    }

    public LocalDateTime getEventEnd() {
        return eventEnd;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (from %s to %s)",this.getType(),
                this.getStatus(), super.getDescription(), eventStart.format(stringDateTimeFormatter),
                eventEnd.format(stringDateTimeFormatter));
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s!@!%s!@!%s",this.getType(), this.isDone(),
                this.getDescription(), eventStart, eventEnd);
    }

}

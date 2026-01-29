public class Event extends Task {
    private String eventStart;
    private String eventEnd;

    public Event(String description, String eventStart, String eventEnd) {
        super(description);
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
    }

    public Event(boolean wasDone, String description, String eventStart, String eventEnd) {
        super(description);
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        if (wasDone) {
            this.markDone();
        }
    }

    public String getType() {
        return ("E");
    }

    public String getEventStart() {
        return eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (from %s to %s)",this.getType(), this.getStatus(), super.getDescription(), eventStart, eventEnd);
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s!@!%s!@!%s",this.getType(), this.isDone(), this.getDescription(), eventStart, eventEnd);
    }

}

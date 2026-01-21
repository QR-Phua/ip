public class Event extends Task {
    private String eventStart;
    private String eventEnd;
    public Event(String description) {
        super(description);
    }

    public Event(String description, String eventStart, String eventEnd) {
        super(description);
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
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

    public String getDescription() {
        return String.format("%s (from %s to %s)", super.getDescription(), eventStart, eventEnd);
    }

}

public class Deadline extends Task {
    private String deadline;
    public Deadline(String description) {
        super(description);
    }

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    public String getType() {
        return ("D");
    }

    public String getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return String.format("%s (by: %s)", super.getDescription(), deadline);
    }
}

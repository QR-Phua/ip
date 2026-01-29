public class Deadline extends Task {
    private String deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    public Deadline(boolean wasDone, String description, String deadline) {
        super(description);
        this.deadline = deadline;
        if (wasDone) {
            this.markDone();
        }
    }

    public String getType() {
        return ("D");
    }

    public String getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (by: %s)",this.getType(), this.getStatus(), super.getDescription(), deadline);
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s!@!%s",this.getType(), this.isDone(), this.getDescription(), deadline);
    }
}

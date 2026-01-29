import java.time.LocalDate;

public class Deadline extends Task {
    private LocalDate deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = LocalDate.parse(deadline);
    }

    public Deadline(boolean wasDone, String description, String deadline) {
        super(description);
        this.deadline = LocalDate.parse(deadline);
        if (wasDone) {
            this.markDone();
        }
    }

    public String getType() {
        return ("D");
    }

    public LocalDate getDeadline() {
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

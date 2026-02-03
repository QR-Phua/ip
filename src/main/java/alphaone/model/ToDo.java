package alphaone.model;
/**
 * A simple task representing a to-do item without associated date/time.
 */
public class ToDo extends Task {
    /**
     * Creates a ToDo with the given description.
     *
     * @param description task description.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Creates a ToDo from stored state.
     *
     * @param wasDone whether the task was previously marked done.
     * @param description stored description.
     */
    public ToDo(boolean wasDone, String description) {
        super(description);
        if (wasDone) {
            this.markDone();
        }
    }

    /**
     * Returns the short type identifier for ToDo.
     *
     * @return the single-letter type code.
     */
    public String getType() {
        return ("T");
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s", this.getType(), this.getStatus(), this.getDescription());
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s", this.getType(), this.isDone(), this.getDescription());
    }
}

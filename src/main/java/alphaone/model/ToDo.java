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
     * @param wasDone     whether the task was previously marked done.
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
     * @return "T"
     */
    @Override
    public String getType() {
        return "T";
    }
}

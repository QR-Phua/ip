package alphaone.model;

import alphaone.util.Constants;

/**
 * Base class representing a generic task with a description and completion state.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new Task with the given description and default not-done state.
     *
     * @param description short textual description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the textual description of this task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a one-character icon representing the completion state.
     *
     * @return "X" if done, otherwise a single space.
     */
    public String getStatusIcon() {
        return isDone() ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the short single-letter type identifier for this task subclass.
     *
     * @return a single-letter type code.
     */
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s", this.getType(), this.getStatusIcon(), this.getDescription());
    }

    /**
     * Serialises the task to a compact line format used by {@code Storage}.
     *
     * @return a serialised representation of the task.
     */
    public String serialiseTask() {
        return this.getType() + Constants.STORAGE_SEPARATOR
                + this.isDone() + Constants.STORAGE_SEPARATOR
                + this.getDescription();
    }
}

package alphaone.model;

/**
 * Base class representing a generic task with a description and completion state.
 */
public class Task {
    private String description;
    private boolean done;

    /**
     * Creates a new Task with the given description and default not-done state.
     *
     * @param description short textual description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.done = false;
    }

    /**
     * Returns whether the underlying internal flag is set for this task.
     *
     * @return true if set, false otherwise.
     */
    public boolean done() {
        return done;
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
        return done;
    }

    /**
     * Returns a one-character status symbol representing completion.
     *
     * @return "X" if done, otherwise a single space.
     */
    public String getStatus() {
        return (isDone() ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        this.done = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markNotDone() {
        this.done = false;
    }

    /**
     * Returns a short type identifier for the task implementation.
     *
     * @return a single-letter type code.
     */
    public String getType() {
        return "Task";
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s",this.getType(), this.getStatus(), this.getDescription());
    }

    /**
     * Serialises the task to a compact line format used by {@code Storage}.
     *
     * @return a serialised representation of the task.
     */
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s",this.getType(), this.isDone(), this.getDescription());
    }

}

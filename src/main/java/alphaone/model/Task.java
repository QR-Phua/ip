package alphaone.model;

public class Task {
    private String description;
    private boolean done;

    public Task(String description) {
        this.description = description;
        this.done = false;
    }

    public boolean done() {
        return done;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public String getStatus() {
        return (isDone() ? "X" : " ");
    }

    public void markDone() {
        this.done = true;
    }

    public void markNotDone() {
        this.done = false;
    }

    public String getType() {
        return "Task";
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s",this.getType(), this.getStatus(), this.getDescription());
    }

    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s",this.getType(), this.isDone(), this.getDescription());
    }

}

package alphaone.model;

public class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }
    public ToDo(boolean wasDone, String description) {
        super(description);
        if (wasDone) {
            this.markDone();
        }
    }

    public String getType() {
        return ("T");
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s",this.getType(), this.getStatus(), this.getDescription());
    }

    @Override
    public String serialiseTask() {
        return String.format("%s!@!%s!@!%s",this.getType(), this.isDone(), this.getDescription());
    }

}
public class InvalidCommandException extends Exception {
    private final AlphaOne.TaskType taskType;
    public InvalidCommandException() {
        super("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Invalid command! Please try again.
                +––––––––––––––––––––––––––––––––––––––––––––––+""");
        this.taskType = null;
    }
    public InvalidCommandException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
    }

    @Override
    public String getMessage() {
        if (taskType == null) {
            return super.getMessage();
        }
        switch (taskType) {
            case TODO -> {
                return """
                        +––––––––––––––––––––––––––––––––––––––––––––––+
                        Invalid TODO command! Please try again.
                        Example: todo [task description]
                        +––––––––––––––––––––––––––––––––––––––––––––––+
                        """;
            }
            case DEADLINE -> {
                return """
                        +––––––––––––––––––––––––––––––––––––––––––––––+
                        Invalid DEADLINE command! Please try again.
                        Example: deadline [task description] /by [task due date]
                        +––––––––––––––––––––––––––––––––––––––––––––––+
                        """;
            }
            case EVENT -> {
                return """
                        +––––––––––––––––––––––––––––––––––––––––––––––+
                        Invalid EVENT command! Please try again.
                        Example: event [event description] /from [date time] /to [date time]
                        +––––––––––––––––––––––––––––––––––––––––––––––+
                        """;
            }
            default -> {
                return super.getMessage();
            }
        }
    }
}

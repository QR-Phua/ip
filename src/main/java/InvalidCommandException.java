public class InvalidCommandException extends Exception {
    private final AlphaOne.TaskType taskType;
    private final AlphaOne.CommandType commandType;
    public InvalidCommandException() {
        super("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Invalid command! Please try again.
                +––––––––––––––––––––––––––––––––––––––––––––––+""");
        this.taskType = null;
        this.commandType = null;
    }
    public InvalidCommandException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
        this.commandType = null;
    }

    public InvalidCommandException(AlphaOne.CommandType commandType) {
        super();
        this.commandType = commandType;
        this.taskType = null;
    }

    @Override
    public String getMessage() {
        if (taskType != null) {
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
        } else if (commandType != null) {
            switch (commandType) {
                case MARK -> {
                    return """
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid MARK command! Please try again.
                            Example: mark [task number]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """;
                }
                case UNMARK -> {
                    return """
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid UNMARK command! Please try again.
                            Example: unmark [task number]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """;
                }
                case DELETE -> {
                    return """
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid DELETE command! Please try again.
                            Example: delete [task number]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """;
                }
                case BYE -> {
                    return """
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid BYE command! No other parameters required.
                            Example: bye
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """;
                }
                case LIST -> {
                    return """
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid LIST command! No other parameters required.
                            Example: list
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """;
                }
                default -> {
                    return super.getMessage();
                }
            }
        }

        return super.getMessage();
    }
}

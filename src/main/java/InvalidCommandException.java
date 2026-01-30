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
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid TODO command! Please try again.
                            Example: todo [task description]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                case DEADLINE -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid DEADLINE command! Please try again.
                            Example: deadline [task description] /by [task due date]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                case EVENT -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid EVENT command! Please try again.
                            Example: event [event description] /from [date time] /to [date time]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                default -> {
                    return super.getMessage();
                }
            }
        } else if (commandType != null) {
            switch (commandType) {
                case MARK -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid MARK command! Please try again.
                            Example: mark [task number]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                case UNMARK -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid UNMARK command! Please try again.
                            Example: unmark [task number]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                case DELETE -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid DELETE command! Please try again.
                            Example: delete [task number]
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                case BYE -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid BYE command! No other parameters required.
                            Example: bye
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                case LIST -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid LIST command! No other parameters required.
                            Example: list
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                } case FIND -> {
                    return ("""
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            Invalid FIND command! Please enter keyword(s)
                            Example: find assignment
                            +––––––––––––––––––––––––––––––––––––––––––––––+
                            """).stripTrailing();
                }
                default -> {
                    return super.getMessage();
                }
            }
        }

        return super.getMessage();
    }
}

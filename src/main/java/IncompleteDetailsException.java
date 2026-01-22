public class IncompleteDetailsException extends Exception{
    private AlphaOne.TaskType taskType;

    public IncompleteDetailsException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
    }

    @Override
    public String getMessage() {
        switch (taskType) {
            case TODO -> {
                return """
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    Incomplete details to create task!
                    Please add in what you would like to do?
                    Example: todo cook a feast
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    """;
            }
            case DEADLINE -> {
                return """
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    Incomplete details to create task!
                    Please add in what you would like to do followed with /by to set the deadline.
                    Example: deadline write report /by tomorrow evening
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    """;
            }
            case EVENT -> {
                return """
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    Incomplete details to create task!
                    Please add in what you would like to do followed with /from and /to to set the duration?
                    Example: event attend wedding on saturday /from 12pm /to 6pm
                    +––––––––––––––––––––––––––––––––––––––––––––––+
                    """;
            }
            default -> {
                return """
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Incomplete details to create task!
                %s
                +––––––––––––––––––––––––––––––––––––––––––––––+""";
            }
        }
    }
}

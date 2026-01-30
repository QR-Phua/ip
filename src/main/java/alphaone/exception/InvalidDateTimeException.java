package alphaone.exception;

import alphaone.AlphaOne;

public class InvalidDateTimeException extends Exception{
    private final AlphaOne.TaskType taskType;

    public InvalidDateTimeException(AlphaOne.TaskType taskType) {
        super();
        this.taskType = taskType;
    }

    @Override
    public String getMessage() {
        switch (taskType) {
        case DEADLINE -> {
            return ("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Datetime information provided is invalid!
                For Deadline tasks, use YYYY-MM-DD
                +––––––––––––––––––––––––––––––––––––––––––––––+""").stripTrailing();
        }
        case EVENT -> {
            return ("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Datetime information provided is invalid!
                For Event tasks, use YYYY-MM-DD HHMM
                +––––––––––––––––––––––––––––––––––––––––––––––+""").stripTrailing();
        }
        default -> {
            return ("""
                +––––––––––––––––––––––––––––––––––––––––––––––+
                Datetime information provided is invalid!
                +––––––––––––––––––––––––––––––––––––––––––––––+""").stripTrailing();
        }
        }
    }

}

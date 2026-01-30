package alphaone.parser;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import alphaone.exception.InvalidCommandException;
import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidDateTimeException;
import alphaone.AlphaOne;

public class Parser {

    public static String[] splitInput(String input) {
        return input.split("\\s+");
    }

    public static String joinFromIndex(String[] tokens, int start) {
        if (start >= tokens.length) return "";
        return String.join(" ", Arrays.asList(tokens).subList(start, tokens.length)).trim();
    }

    public static void validateDate(String input, AlphaOne.TaskType type) throws InvalidDateTimeException {
        if (type.equals(AlphaOne.TaskType.DEADLINE)) {
            try {
                LocalDate.parse(input);
            } catch (DateTimeParseException dtpe) {
                throw new InvalidDateTimeException(type);
            }
        } else {
            try {
                LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            } catch (DateTimeParseException dtpe) {
                throw new InvalidDateTimeException(type);
            }
        }
    }

    public static ArrayList<String> descriptionPrep(String[] commands, AlphaOne.TaskType taskType) throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        switch (taskType) {
        case DEADLINE -> {
            List<String> stringList = new ArrayList<>(Arrays.asList(commands));
            stringList.remove(0);
            int finder = stringList.indexOf("/by");
            if (finder == -1 || finder == 0) {
                throw new InvalidCommandException(taskType);
            }
            List<String> deadlineList = stringList.subList(finder +1, stringList.size());
            if (deadlineList.isEmpty()) {
                throw new IncompleteDetailsException(taskType);
            }
            String deadline = String.join(" ", deadlineList);

            validateDate(deadline, taskType);

            List<String> descriptionList = stringList.subList(0, finder);
            String description = String.join(" ", descriptionList);

            return new ArrayList<>(Arrays.asList(description, deadline));
        }
        case EVENT -> {
            List<String> stringList = new ArrayList<>(Arrays.asList(commands));
            stringList.remove(0);
            int finderFrom = stringList.indexOf("/from");
            int finderTo = stringList.indexOf("/to");
            if (finderTo == -1 || finderFrom == -1 || finderTo <= finderFrom + 1) {
                throw new InvalidCommandException(taskType);
            }
            List<String> fromList = stringList.subList(finderFrom +1, finderTo);
            if (fromList.isEmpty()) {
                throw new IncompleteDetailsException(taskType);
            }

            String fromDesc = String.join(" ", fromList);

            validateDate(fromDesc, taskType);

            List<String> ToList = stringList.subList(finderTo +1, stringList.size());
            if (ToList.isEmpty()) {
                throw new IncompleteDetailsException(taskType);
            }

            String toDesc = String.join(" ", ToList);

            validateDate(toDesc, taskType);

            List<String> descList = stringList.subList(0, finderFrom);
            String description = String.join(" ", descList);

            return new ArrayList<>(Arrays.asList(description, fromDesc, toDesc));
        }
        default -> throw new InvalidCommandException();
        }
    }

}

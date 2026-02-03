package alphaone.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alphaone.AlphaOne;
import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidDateTimeException;

/**
 * Utility class that provides parsing helpers for command-line input.
 *
 * The Parser contains small stateless helpers to split input tokens, join tokens
 * from an index, validate date/time inputs and extract task descriptions and
 * timings for deadline and event tasks.
 */
public class Parser {

    /**
     * Splits raw user input into tokens separated by whitespace.
     *
     * @param input the raw input line provided by the user.
     * @return an array of tokens extracted from {@code input}.
     */
    public static String[] splitInput(String input) {
        return input.split("\\s+");
    }

    /**
     * Joins tokens from the provided start index to the end into a single string.
     *
     * @param tokens the array of tokens to join.
     * @param start the index at which to begin joining (inclusive).
     * @return the joined string, or an empty string if {@code start} is past the end.
     */
    public static String joinFromIndex(String[] tokens, int start) {
        if (start >= tokens.length) {
            return "";
        }
        return String.join(" ", Arrays.asList(tokens).subList(start, tokens.length)).trim();
    }

    /**
     * Validates a date or datetime string according to the task type.
     *
     * If the format is invalid an {@link InvalidDateTimeException} is thrown.
     *
     * @param input the date/datetime string to validate.
     * @param type the task type that determines expected format.
     * @throws InvalidDateTimeException if {@code input} cannot be parsed for {@code type}.
     */
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

    /**
     * Extracts and validates description and date/time parts for deadline and event commands.
     *
     * For DEADLINE: expects input tokens to contain a "/by" marker. Returns [description, deadline].
     * For EVENT: expects input tokens to contain "/from" and "/to" markers. Returns [description, from, to].
     *
     * @param commands the tokenised user input (first token is the command word).
     * @param taskType the task type to parse (DEADLINE or EVENT).
     * @return an ArrayList of strings containing parsed segments as described above.
     * @throws InvalidCommandException if required markers are missing or misplaced.
     * @throws IncompleteDetailsException if required segments are empty.
     * @throws InvalidDateTimeException if any date/time segment fails validation.
     */
    public static ArrayList<String> descriptionPrep(
            String[] commands,
            AlphaOne.TaskType taskType)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        switch (taskType) {
        case DEADLINE -> {
            List<String> stringList = new ArrayList<>(Arrays.asList(commands));
            stringList.remove(0);
            int finder = stringList.indexOf("/by");
            if (finder == -1 || finder == 0) {
                throw new InvalidCommandException(taskType);
            }
            List<String> deadlineList = stringList.subList(finder + 1, stringList.size());
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
            List<String> fromList = stringList.subList(finderFrom + 1, finderTo);
            if (fromList.isEmpty()) {
                throw new IncompleteDetailsException(taskType);
            }

            String fromDesc = String.join(" ", fromList);

            validateDate(fromDesc, taskType);

            List<String> toList = stringList.subList(finderTo + 1, stringList.size());
            if (toList.isEmpty()) {
                throw new IncompleteDetailsException(taskType);
            }

            String toDesc = String.join(" ", toList);

            validateDate(toDesc, taskType);

            List<String> descList = stringList.subList(0, finderFrom);
            String description = String.join(" ", descList);

            return new ArrayList<>(Arrays.asList(description, fromDesc, toDesc));
        }
        default -> throw new InvalidCommandException();
        }
    }

}

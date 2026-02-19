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
import alphaone.util.Constants;

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
     * @param start  the index at which to begin joining (inclusive).
     * @return the joined string, or an empty string if {@code start} is past the end.
     */
    public static String joinFromIndex(String[] tokens, int start) {
        if (start >= tokens.length) {
            return "";
        }
        return String.join(" ", Arrays.asList(tokens).subList(start, tokens.length)).trim();
    }

    /**
     * Extracts and validates description and date/time parts for deadline and event commands.
     *
     * For DEADLINE: expects input tokens to contain a "/by" marker. Returns [description, deadline].
     * For EVENT: expects input tokens to contain "/from" and "/to" markers. Returns [description, from, to].
     *
     * @param commands raw token array from user input (including the command word at index 0).
     * @param taskType the task type; must be DEADLINE or EVENT.
     * @return an ArrayList of strings containing parsed segments as described above.
     * @throws InvalidCommandException    if the required marker tokens are missing or misplaced.
     * @throws IncompleteDetailsException if any required segment (description, date) is empty.
     * @throws InvalidDateTimeException   if a date/time segment fails format validation.
     */
    public static ArrayList<String> parseTaskArguments(
            String[] commands,
            AlphaOne.TaskType taskType)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        switch (taskType) {
        case DEADLINE -> {
            List<String> tokens = new ArrayList<>(Arrays.asList(commands));
            tokens.remove(0);
            int byMarkerIndex = tokens.indexOf("/by");
            if (byMarkerIndex == -1 || byMarkerIndex == 0) {
                throw new InvalidCommandException(taskType);
            }
            String deadline = extractSegment(tokens, byMarkerIndex + 1, tokens.size(), taskType);
            validateDateTime(deadline, taskType);
            String description = String.join(" ", tokens.subList(0, byMarkerIndex));
            return new ArrayList<>(Arrays.asList(description, deadline));
        }
        case EVENT -> {
            List<String> tokens = new ArrayList<>(Arrays.asList(commands));
            tokens.remove(0);
            int fromMarkerIndex = tokens.indexOf("/from");
            int toMarkerIndex = tokens.indexOf("/to");
            if (toMarkerIndex == -1 || fromMarkerIndex == -1 || toMarkerIndex <= fromMarkerIndex + 1) {
                throw new InvalidCommandException(taskType);
            }
            String fromDateTime = extractSegment(tokens, fromMarkerIndex + 1, toMarkerIndex, taskType);
            validateDateTime(fromDateTime, taskType);
            String toDateTime = extractSegment(tokens, toMarkerIndex + 1, tokens.size(), taskType);
            validateDateTime(toDateTime, taskType);
            String description = String.join(" ", tokens.subList(0, fromMarkerIndex));
            return new ArrayList<>(Arrays.asList(description, fromDateTime, toDateTime));
        }
        default -> throw new InvalidCommandException();
        }
    }

    /**
     * Validates that the given date/time string matches the expected format for the task type.
     *
     * @param input the date/time string to validate.
     * @param type  the task type, used to select the expected format.
     * @throws InvalidDateTimeException if {@code input} cannot be parsed for the given type.
     */
    public static void validateDateTime(String input, AlphaOne.TaskType type) throws InvalidDateTimeException {
        if (type.equals(AlphaOne.TaskType.DEADLINE)) {
            try {
                LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                throw new InvalidDateTimeException(type);
            }
        } else {
            try {
                LocalDateTime.parse(input, DateTimeFormatter.ofPattern(Constants.INPUT_DATETIME_PATTERN));
            } catch (DateTimeParseException e) {
                throw new InvalidDateTimeException(type);
            }
        }
    }

    /**
     * Extracts the text between {@code fromIndex} (inclusive) and {@code toIndex} (exclusive)
     * from {@code tokens}, joining with spaces. Throws {@link IncompleteDetailsException} if
     * the resulting segment is empty.
     */
    private static String extractSegment(List<String> tokens, int fromIndex, int toIndex,
            AlphaOne.TaskType taskType) throws IncompleteDetailsException {
        List<String> segment = tokens.subList(fromIndex, toIndex);
        if (segment.isEmpty()) {
            throw new IncompleteDetailsException(taskType);
        }
        return String.join(" ", segment);
    }
}

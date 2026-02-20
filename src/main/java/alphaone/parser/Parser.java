package alphaone.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alphaone.core.AlphaOne;
import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidDateTimeException;
import alphaone.util.Constants;

/**
 * Utility class that provides parsing helpers for command-line input.
 *
 * <p>The Parser contains small stateless helpers to split input tokens, join tokens
 * from an index, validate date/time inputs and extract task descriptions and
 * timings for deadline and event tasks.
 */
public class Parser {
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

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
     * <p>For DEADLINE: expects input tokens to contain a "/by" marker. Returns [description, deadline].
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
            return parseDeadlineArguments(commands);
        }
        case EVENT -> {
            return parseEventArguments(commands);
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
        switch (type) {
        case DEADLINE -> {
            try {
                LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                throw new InvalidDateTimeException(type);
            }
        }
        case EVENT -> {
            try {
                LocalDateTime.parse(input, DateTimeFormatter.ofPattern(Constants.INPUT_DATETIME_PATTERN));
            } catch (DateTimeParseException e) {
                throw new InvalidDateTimeException(type);
            }
        }
        default -> throw new InvalidDateTimeException(type);
        }
    }

    /**
     * Extracts the text between {@code fromIndex} (inclusive) and {@code toIndex} (exclusive)
     * from {@code tokens}, joining with spaces.
     *
     * @param tokens    the token list to slice.
     * @param fromIndex the start index (inclusive).
     * @param toIndex   the end index (exclusive).
     * @param taskType  the task type, used to construct the exception message.
     * @return the joined segment string.
     * @throws IncompleteDetailsException if the resulting segment is empty.
     */
    private static String extractSegment(List<String> tokens, int fromIndex, int toIndex,
            AlphaOne.TaskType taskType) throws IncompleteDetailsException {
        List<String> segment = tokens.subList(fromIndex, toIndex);
        if (segment.isEmpty()) {
            throw new IncompleteDetailsException(taskType);
        }
        return String.join(" ", segment);
    }

    /**
     * Parses tokens for a DEADLINE command into [description, deadline].
     *
     * @param commands raw token array including the command word at index 0.
     * @return list containing the description and deadline date string.
     * @throws InvalidCommandException    if the /by marker is missing or misplaced.
     * @throws IncompleteDetailsException if the deadline date segment is empty.
     * @throws InvalidDateTimeException   if the deadline date fails format validation.
     */
    private static ArrayList<String> parseDeadlineArguments(String[] commands)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        List<String> tokens = new ArrayList<>(Arrays.asList(commands));
        tokens.remove(0);
        int byMarkerIndex = tokens.indexOf(BY_MARKER);
        boolean isByMarkerMissingOrAtStart = byMarkerIndex == -1 || byMarkerIndex == 0;
        if (isByMarkerMissingOrAtStart) {
            throw new InvalidCommandException(AlphaOne.TaskType.DEADLINE);
        }
        String deadline = extractSegment(tokens, byMarkerIndex + 1, tokens.size(), AlphaOne.TaskType.DEADLINE);
        validateDateTime(deadline, AlphaOne.TaskType.DEADLINE);
        String description = String.join(" ", tokens.subList(0, byMarkerIndex));
        return new ArrayList<>(Arrays.asList(description, deadline));
    }

    /**
     * Parses tokens for an EVENT command into [description, fromDateTime, toDateTime].
     *
     * @param commands raw token array including the command word at index 0.
     * @return list containing the description, start datetime, and end datetime strings.
     * @throws InvalidCommandException    if the /from or /to markers are missing or misplaced.
     * @throws IncompleteDetailsException if the description or a datetime segment is empty.
     * @throws InvalidDateTimeException   if a datetime fails format validation or start is not before end.
     */
    private static ArrayList<String> parseEventArguments(String[] commands)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        List<String> tokens = new ArrayList<>(Arrays.asList(commands));
        tokens.remove(0);
        int fromMarkerIndex = tokens.indexOf(FROM_MARKER);
        int toMarkerIndex = tokens.indexOf(TO_MARKER);
        boolean areEventMarkersInvalid = toMarkerIndex == -1 || fromMarkerIndex == -1
                || toMarkerIndex <= fromMarkerIndex + 1;
        if (areEventMarkersInvalid) {
            throw new InvalidCommandException(AlphaOne.TaskType.EVENT);
        }
        String fromDateTime = extractSegment(tokens, fromMarkerIndex + 1, toMarkerIndex, AlphaOne.TaskType.EVENT);
        validateDateTime(fromDateTime, AlphaOne.TaskType.EVENT);
        String toDateTime = extractSegment(tokens, toMarkerIndex + 1, tokens.size(), AlphaOne.TaskType.EVENT);
        validateDateTime(toDateTime, AlphaOne.TaskType.EVENT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.INPUT_DATETIME_PATTERN);
        LocalDateTime parsedFrom = LocalDateTime.parse(fromDateTime, formatter);
        LocalDateTime parsedTo = LocalDateTime.parse(toDateTime, formatter);
        boolean isStartNotBeforeEnd = !parsedFrom.isBefore(parsedTo);
        if (isStartNotBeforeEnd) {
            throw new InvalidDateTimeException(AlphaOne.TaskType.EVENT,
                    InvalidDateTimeException.Reason.EVENT_ORDER);
        }
        String description = String.join(" ", tokens.subList(0, fromMarkerIndex));
        if (description.isBlank()) {
            throw new IncompleteDetailsException(AlphaOne.TaskType.EVENT);
        }
        return new ArrayList<>(Arrays.asList(description, fromDateTime, toDateTime));
    }
}

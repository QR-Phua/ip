package alphaone.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import alphaone.core.AlphaOne;
import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidDateTimeException;

public class ParserUnitTest {

    @Test
    public void splitInput_multipleSpaces_tokensCorrectlySplit() {
        String[] tokens = Parser.splitInput("hello   world");
        assertArrayEquals(new String[]{"hello", "world"}, tokens);
        assertEquals("world", Parser.joinFromIndex(tokens, 1));
        assertEquals("", Parser.joinFromIndex(tokens, 10));
    }

    @Test
    public void validateDateTime_deadline_validAndInvalid() throws Exception {
        Parser.validateDateTime("2026-06-15", AlphaOne.TaskType.DEADLINE);
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDateTime(
                "15/06/2026", AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void validateDateTime_event_validAndInvalid() throws Exception {
        Parser.validateDateTime("2026-06-15 1430", AlphaOne.TaskType.EVENT);
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDateTime(
                "not-a-datetime", AlphaOne.TaskType.EVENT));
    }

    @Test
    public void parseTaskArguments_deadline_successAndFailures() throws Exception {
        String[] good = new String[]{"deadline", "submit", "report", "/by", "2026-06-15"};
        ArrayList<String> out = Parser.parseTaskArguments(good, AlphaOne.TaskType.DEADLINE);
        assertEquals("submit report", out.get(0));
        assertEquals("2026-06-15", out.get(1));

        String[] noByMarker = new String[]{"deadline", "submit", "report"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                noByMarker, AlphaOne.TaskType.DEADLINE));

        String[] emptyDeadline = new String[]{"deadline", "submit", "/by"};
        assertThrows(IncompleteDetailsException.class, () -> Parser.parseTaskArguments(
                emptyDeadline, AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void parseTaskArguments_event_successAndFailures() throws Exception {
        String[] good = new String[]{"event", "seminar", "/from", "2026-06-15 0900", "/to", "2026-06-15 1200"};
        ArrayList<String> out = Parser.parseTaskArguments(good, AlphaOne.TaskType.EVENT);
        assertEquals("seminar", out.get(0));
        assertEquals("2026-06-15 0900", out.get(1));
        assertEquals("2026-06-15 1200", out.get(2));

        String[] missingTo = new String[]{"event", "seminar", "/from", "2026-06-15 0900"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                missingTo, AlphaOne.TaskType.EVENT));

        String[] adjacent = new String[]{"event", "seminar", "/from", "/to", "2026-06-15 1200"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                adjacent, AlphaOne.TaskType.EVENT));
    }
}

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

public class ParserTest {

    @Test
    public void splitInput_multipleSpaces_tokensCorrectlySplit() {
        String[] tokens = Parser.splitInput("a   b\tc  ");
        assertArrayEquals(new String[]{"a", "b", "c"}, tokens);
        assertEquals("b c", Parser.joinFromIndex(tokens, 1));
        assertEquals("", Parser.joinFromIndex(tokens, 5));
    }

    @Test
    public void validateDateTime_validDeadlineDate_noExceptionThrown() throws Exception {
        Parser.validateDateTime("2026-01-31", AlphaOne.TaskType.DEADLINE);
    }

    @Test
    public void validateDateTime_invalidDeadlineDate_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDateTime(
                "31-01-2026", AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void validateDateTime_validEventDateTime_noExceptionThrown() throws Exception {
        Parser.validateDateTime("2026-01-01 0900", AlphaOne.TaskType.EVENT);
    }

    @Test
    public void validateDateTime_invalidEventDateTime_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDateTime(
                "2026/01/01 09:00", AlphaOne.TaskType.EVENT));
    }

    @Test
    public void parseTaskArguments_validDeadlineInput_returnsDescriptionAndDate() throws Exception {
        String[] tokens = new String[]{"deadline", "homework", "/by", "2026-01-31"};
        ArrayList<String> out = Parser.parseTaskArguments(tokens, AlphaOne.TaskType.DEADLINE);
        assertEquals(2, out.size());
        assertEquals("homework", out.get(0));
        assertEquals("2026-01-31", out.get(1));
    }

    @Test
    public void parseTaskArguments_deadlineMissingByMarker_throwsInvalidCommandException() {
        String[] tokens = new String[]{"deadline", "homework", "2026-01-31"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                tokens, AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void parseTaskArguments_deadlineMissingDate_throwsIncompleteDetailsException() {
        String[] tokens = new String[]{"deadline", "todo", "/by"};
        assertThrows(IncompleteDetailsException.class, () -> Parser.parseTaskArguments(
                tokens, AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void parseTaskArguments_validEventInput_returnsDescriptionAndDateTimes() throws Exception {
        String[] tokens = new String[]{"event", "party", "/from", "2026-01-01", "0900", "/to", "2026-01-01", "1700"};
        ArrayList<String> out = Parser.parseTaskArguments(tokens, AlphaOne.TaskType.EVENT);
        assertEquals(3, out.size());
        assertEquals("party", out.get(0));
        assertEquals("2026-01-01 0900", out.get(1));
        assertEquals("2026-01-01 1700", out.get(2));
    }

    @Test
    public void parseTaskArguments_eventMissingToMarker_throwsInvalidCommandException() {
        String[] tokens = new String[]{"event", "party", "/from", "2026-01-01"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                tokens, AlphaOne.TaskType.EVENT));
    }

    @Test
    public void parseTaskArguments_eventAdjacentFromAndToMarkers_throwsInvalidCommandException() {
        // /from and /to adjacent means no datetime between them
        String[] tokens = new String[]{"event", "party", "/from", "/to", "2026-01-01", "1700"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                tokens, AlphaOne.TaskType.EVENT));
    }
}

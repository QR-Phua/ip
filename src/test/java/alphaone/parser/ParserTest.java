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
    public void validateDateTime_deadline_validAndInvalid() throws Exception {
        // valid ISO date for deadlines
        Parser.validateDateTime("2026-01-31", AlphaOne.TaskType.DEADLINE);
        // invalid format should throw
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDateTime(
                "31-01-2026", AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void validateDateTime_event_validAndInvalid() throws Exception {
        // valid datetime pattern yyyy-MM-dd HHmm
        Parser.validateDateTime("2026-01-01 0900", AlphaOne.TaskType.EVENT);
        // invalid format should throw
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDateTime(
                "2026/01/01 09:00", AlphaOne.TaskType.EVENT));
    }

    @Test
    public void parseTaskArguments_deadline_successAndFailures() throws Exception {
        String[] good = new String[]{"deadline", "homework", "/by", "2026-01-31"};
        ArrayList<String> out = Parser.parseTaskArguments(good, AlphaOne.TaskType.DEADLINE);
        assertEquals(2, out.size());
        assertEquals("homework", out.get(0));
        assertEquals("2026-01-31", out.get(1));

        String[] missingBy = new String[]{"deadline", "homework", "2026-01-31"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                missingBy, AlphaOne.TaskType.DEADLINE));

        String[] incomplete = new String[]{"deadline", "todo", "/by"};
        assertThrows(IncompleteDetailsException.class, () -> Parser.parseTaskArguments(
                incomplete, AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void parseTaskArguments_event_successAndFailures() throws Exception {
        String[] good = new String[]{"event", "party", "/from", "2026-01-01", "0900", "/to", "2026-01-01", "1700"};
        ArrayList<String> out = Parser.parseTaskArguments(good, AlphaOne.TaskType.EVENT);
        assertEquals(3, out.size());
        assertEquals("party", out.get(0));
        assertEquals("2026-01-01 0900", out.get(1));
        assertEquals("2026-01-01 1700", out.get(2));

        String[] missingTokens = new String[]{"event", "party", "/from", "2026-01-01"};
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                missingTokens, AlphaOne.TaskType.EVENT));

        String[] incompleteFrom = new String[]{"event", "party", "/from", "/to", "2026-01-01", "1700"};
        // Parser throws InvalidCommandException when /from and /to are adjacent (no description between them)
        assertThrows(InvalidCommandException.class, () -> Parser.parseTaskArguments(
                incompleteFrom, AlphaOne.TaskType.EVENT));
    }
}

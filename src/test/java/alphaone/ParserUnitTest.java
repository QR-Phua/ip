package alphaone;

import alphaone.parser.Parser;
import alphaone.AlphaOne;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidDateTimeException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParserUnitTest {

    @Test
    public void splitAndJoinTokens() {
        String[] tokens = Parser.splitInput("a   b\tc  ");
        assertArrayEquals(new String[]{"a", "b", "c"}, tokens);
        assertEquals("b c", Parser.joinFromIndex(tokens, 1));
        assertEquals("", Parser.joinFromIndex(tokens, 5));
    }

    @Test
    public void validateDate_deadline_validAndInvalid() throws Exception {
        // valid ISO date for deadlines
        Parser.validateDate("2026-01-31", AlphaOne.TaskType.DEADLINE);
        // invalid format should throw
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDate("31-01-2026", AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void validateDate_event_validAndInvalid() throws Exception {
        // valid datetime pattern yyyy-MM-dd HHmm
        Parser.validateDate("2026-01-01 0900", AlphaOne.TaskType.EVENT);
        // invalid format should throw
        assertThrows(InvalidDateTimeException.class, () -> Parser.validateDate("2026/01/01 09:00", AlphaOne.TaskType.EVENT));
    }

    @Test
    public void descriptionPrep_deadline_successAndFailures() throws Exception {
        String[] good = new String[]{"deadline", "homework", "/by", "2026-01-31"};
        ArrayList<String> out = Parser.descriptionPrep(good, AlphaOne.TaskType.DEADLINE);
        assertEquals(2, out.size());
        assertEquals("homework", out.get(0));
        assertEquals("2026-01-31", out.get(1));

        String[] missingBy = new String[]{"deadline", "homework", "2026-01-31"};
        assertThrows(InvalidCommandException.class, () -> Parser.descriptionPrep(missingBy, AlphaOne.TaskType.DEADLINE));

        String[] incomplete = new String[]{"deadline", "todo", "/by"};
        assertThrows(IncompleteDetailsException.class, () -> Parser.descriptionPrep(incomplete, AlphaOne.TaskType.DEADLINE));
    }

    @Test
    public void descriptionPrep_event_successAndFailures() throws Exception {
        String[] good = new String[]{"event", "party", "/from", "2026-01-01", "0900", "/to", "2026-01-01", "1700"};
        ArrayList<String> out = Parser.descriptionPrep(good, AlphaOne.TaskType.EVENT);
        assertEquals(3, out.size());
        assertEquals("party", out.get(0));
        assertEquals("2026-01-01 0900", out.get(1));
        assertEquals("2026-01-01 1700", out.get(2));

        String[] missingTokens = new String[]{"event", "party", "/from", "2026-01-01"};
        assertThrows(InvalidCommandException.class, () -> Parser.descriptionPrep(missingTokens, AlphaOne.TaskType.EVENT));

        String[] incompleteFrom = new String[]{"event", "party", "/from", "/to", "2026-01-01", "1700"};
        // Parser throws InvalidCommandException when /from and /to are adjacent (no description between them)
        assertThrows(InvalidCommandException.class, () -> Parser.descriptionPrep(incompleteFrom, AlphaOne.TaskType.EVENT));
    }
}

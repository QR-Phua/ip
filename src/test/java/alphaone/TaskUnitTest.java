package alphaone;

import alphaone.model.Task;
import alphaone.model.ToDo;
import alphaone.model.Deadline;
import alphaone.model.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskUnitTest {

    @Test
    public void taskMarkingAndStatus() {
        Task t = new Task("read");
        assertEquals(" ", t.getStatus());
        t.markDone();
        assertTrue(t.isDone());
        assertEquals("X", t.getStatus());
        t.markNotDone();
        assertFalse(t.isDone());
    }

    @Test
    public void todoToStringAndSerialise() {
        ToDo td = new ToDo("buy milk");
        String s = td.toString();
        assertTrue(s.contains("buy milk"));
        assertTrue(td.serialiseTask().startsWith("T!@!"));
    }

    @Test
    public void deadlineParsingAndFormatting() {
        Deadline d = new Deadline("submit", "2026-01-31");
        assertEquals(LocalDate.parse("2026-01-31"), d.getDeadline());
        assertTrue(d.toString().contains("submit"));
        assertTrue(d.serialiseTask().contains("2026-01-31"));
    }

    @Test
    public void eventParsingAndFormatting() {
        Event e = new Event("party", "2026-01-01 0900", "2026-01-01 1700");
        assertEquals(LocalDateTime.parse("2026-01-01T09:00"), e.getEventStart());
        assertEquals(LocalDateTime.parse("2026-01-01T17:00"), e.getEventEnd());
        assertTrue(e.toString().contains("party"));
    }
}

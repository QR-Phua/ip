package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskUnitTest {

    @Test
    public void markAndUnmark_validTask_statusChangesCorrectly() {
        // Use a concrete subclass since Task is abstract
        ToDo t = new ToDo("read");
        assertEquals(" ", t.getStatusIcon());
        t.markDone();
        assertTrue(t.isDone());
        assertEquals("X", t.getStatusIcon());
        t.markNotDone();
        assertFalse(t.isDone());
    }

    @Test
    public void toString_todo_containsDescription() {
        ToDo td = new ToDo("buy milk");
        String s = td.toString();
        assertTrue(s.contains("buy milk"));
        assertTrue(td.serialiseTask().startsWith("T!@!"));
    }

    @Test
    public void toString_deadline_containsDescriptionAndDate() {
        Deadline d = new Deadline("submit", "2026-01-31");
        assertEquals(LocalDate.parse("2026-01-31"), d.getDeadlineDate());
        assertTrue(d.toString().contains("submit"));
        assertTrue(d.serialiseTask().contains("2026-01-31"));
    }

    @Test
    public void parseEvent_validDateTimes_parsedCorrectly() {
        Event e = new Event("party", "2026-01-01 0900", "2026-01-01 1700");
        assertEquals(LocalDateTime.parse("2026-01-01T09:00"), e.getStartDateTime());
        assertEquals(LocalDateTime.parse("2026-01-01T17:00"), e.getEndDateTime());
        assertTrue(e.toString().contains("party"));
    }
}

package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;


public class DeadlineTest {

    @Test
    public void toString_deadline_containsDescriptionAndDate() {
        Deadline d = new Deadline("submit", "2026-01-31");
        assertEquals(LocalDate.parse("2026-01-31"), d.getDeadlineDate());
        assertTrue(d.toString().contains("submit"));
        assertTrue(d.serialiseTask().contains("2026-01-31"));
    }
}

package alphaone.model;

import alphaone.model.Deadline;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DeadlineTest {

    @Test
    public void deadlineParsingAndFormatting() {
        Deadline d = new Deadline("submit", "2026-01-31");
        assertEquals(LocalDate.parse("2026-01-31"), d.getDeadline());
        assertTrue(d.toString().contains("submit"));
        assertTrue(d.serialiseTask().contains("2026-01-31"));
    }
}

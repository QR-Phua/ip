package alphaone.model;

import alphaone.model.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void eventParsingAndFormatting() {
        Event e = new Event("party", "2026-01-01 0900", "2026-01-01 1700");
        assertEquals(LocalDateTime.parse("2026-01-01T09:00"), e.getEventStart());
        assertEquals(LocalDateTime.parse("2026-01-01T17:00"), e.getEventEnd());
        assertTrue(e.toString().contains("party"));
    }
}

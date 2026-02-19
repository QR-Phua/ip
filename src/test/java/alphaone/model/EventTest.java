package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void parseEvent_validDateTimes_parsedCorrectly() {
        Event e = new Event("party", "2026-01-01 0900", "2026-01-01 1700");
        assertEquals(LocalDateTime.parse("2026-01-01T09:00"), e.getStartDateTime());
        assertEquals(LocalDateTime.parse("2026-01-01T17:00"), e.getEndDateTime());
        assertTrue(e.toString().contains("party"));
    }
}

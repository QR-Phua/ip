package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

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
}

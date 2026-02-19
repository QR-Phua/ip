package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

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
}

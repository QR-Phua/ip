package alphaone.model;

import alphaone.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

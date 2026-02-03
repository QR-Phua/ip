package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void todoToStringAndSerialise() {
        ToDo td = new ToDo("buy milk");
        String s = td.toString();
        assertTrue(s.contains("buy milk"));
        assertTrue(td.serialiseTask().startsWith("T!@!"));
    }
}

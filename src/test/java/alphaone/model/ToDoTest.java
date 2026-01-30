package alphaone.model;

import alphaone.model.ToDo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToDoTest {

    @Test
    public void todoToStringAndSerialise() {
        ToDo td = new ToDo("buy milk");
        String s = td.toString();
        assertTrue(s.contains("buy milk"));
        assertTrue(td.serialiseTask().startsWith("T!@!"));
    }
}

package alphaone.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import alphaone.exception.InvalidTaskItemException;

public class TaskListTest {

    @Test
    public void addTask_validTodo_addedToInternalMap() {
        TaskList tl = new TaskList();
        tl.addTask("a task", alphaone.core.AlphaOne.TaskType.TODO);
        HashMap<Integer, Task> map = tl.getInternalMap();
        assertEquals(1, map.size());
        assertInstanceOf(ToDo.class, map.get(1));
    }

    @Test
    public void verifyTaskExists_throwsOnMissing() {
        TaskList tl = new TaskList();
        assertThrows(InvalidTaskItemException.class, () -> tl.verifyTaskExists(5));
    }

    @Test
    public void searchKeyword_findsMatches() {
        TaskList tl = new TaskList();
        tl.addTask("alpha item", alphaone.core.AlphaOne.TaskType.TODO);
        tl.addTask("beta item", alphaone.core.AlphaOne.TaskType.TODO);
        HashMap<Integer, Task> res = tl.searchKeyword("alpha");
        assertEquals(1, res.size());
        assertTrue(res.values().iterator().next().getDescription().contains("alpha"));
    }

    @Test
    public void setInternalMap_updatesCounter() {
        TaskList tl = new TaskList();
        HashMap<Integer, Task> newMap = new HashMap<>();
        newMap.put(5, new ToDo("five"));
        tl.setInternalMap(newMap);
        tl.addTask("six", alphaone.core.AlphaOne.TaskType.TODO);
        HashMap<Integer, Task> map = tl.getInternalMap();
        assertTrue(map.containsKey(6));
    }

    @Test
    public void markAndUnmark_existingTask_isDoneChanges() {
        TaskList tl = new TaskList();
        tl.addTask("alpha", alphaone.core.AlphaOne.TaskType.TODO);
        tl.markDone(1);
        assertTrue(tl.getInternalMap().get(1).isDone());
        tl.unmarkDone(1);
        assertFalse(tl.getInternalMap().get(1).isDone());
    }

    @Test
    public void deleteTask_existingTask_removedFromMap() {
        TaskList tl = new TaskList();
        tl.addTask("x", alphaone.core.AlphaOne.TaskType.TODO);
        tl.addTask("y", alphaone.core.AlphaOne.TaskType.TODO);
        tl.deleteTask(1);
        assertFalse(tl.getInternalMap().containsKey(1));
        assertTrue(tl.getInternalMap().containsKey(2));
    }
}

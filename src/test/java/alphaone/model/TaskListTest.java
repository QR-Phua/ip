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
    public void addAndRetrieveInternalMap() {
        TaskList tl = new TaskList();
        tl.addTask("a task", alphaone.AlphaOne.TaskType.TODO);
        HashMap<Integer, Task> map = tl.getInternalMap();
        assertEquals(1, map.size());
        assertInstanceOf(ToDo.class, map.get(1));
    }

    @Test
    public void taskExistenceChecker_throwsOnMissing() {
        TaskList tl = new TaskList();
        assertThrows(InvalidTaskItemException.class, () -> tl.taskExistenceChecker(5));
    }

    @Test
    public void searchKeyword_findsMatches() {
        TaskList tl = new TaskList();
        tl.addTask("alpha item", alphaone.AlphaOne.TaskType.TODO);
        tl.addTask("beta item", alphaone.AlphaOne.TaskType.TODO);
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
        tl.addTask("six", alphaone.AlphaOne.TaskType.TODO);
        HashMap<Integer, Task> map = tl.getInternalMap();
        assertTrue(map.containsKey(6));
    }

    @Test
    public void markAndUnmarkBehavior() {
        TaskList tl = new TaskList();
        tl.addTask("alpha", alphaone.AlphaOne.TaskType.TODO);
        tl.markDone(1);
        assertTrue(tl.getInternalMap().get(1).isDone());
        tl.unmarkDone(1);
        assertFalse(tl.getInternalMap().get(1).isDone());
    }

    @Test
    public void deleteTaskRemovesEntry() {
        TaskList tl = new TaskList();
        tl.addTask("x", alphaone.AlphaOne.TaskType.TODO);
        tl.addTask("y", alphaone.AlphaOne.TaskType.TODO);
        tl.deleteTask(1);
        assertFalse(tl.getInternalMap().containsKey(1));
        assertTrue(tl.getInternalMap().containsKey(2));
    }
}

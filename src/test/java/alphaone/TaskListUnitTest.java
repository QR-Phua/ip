package alphaone;

import alphaone.model.TaskList;
import alphaone.model.Task;
import alphaone.model.ToDo;
import alphaone.model.Deadline;
import alphaone.exception.InvalidTaskItemException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TaskListUnitTest {

    @Test
    public void addAndRetrieveInternalMap() {
        TaskList tl = new TaskList();
        tl.addTask("a task", alphaone.AlphaOne.TaskType.TODO);
        HashMap<Integer, Task> map = tl.getInternalMap();
        assertEquals(1, map.size());
        assertTrue(map.get(1) instanceof ToDo);
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
}

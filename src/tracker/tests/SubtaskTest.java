package tracker.tests;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void testSubtaskInitialization() {
        Epic parentEpic = new Epic("Родительский эпик", "Описание родительского эпика", 1, new ArrayList<>());
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", 2, Status.IN_PROGRESS, parentEpic);

        assertEquals("Подзадача 1", subtask.getName(), "Название подзадачи должно совпадать");
        assertEquals("Описание подзадачи", subtask.getDescription(), "Описание подзадачи должно совпадать");
        assertEquals(2, subtask.getId(), "ID подзадачи должен совпадать");
        assertEquals(Status.IN_PROGRESS, subtask.getStatus(), "Статус подзадачи должен совпадать");
        assertEquals(parentEpic, subtask.getParentEpic(), "Родительский эпик должен совпадать");
    }

}
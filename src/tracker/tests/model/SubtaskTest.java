package tracker.tests.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    Subtask subtask;
    Epic epic;

    @BeforeEach
    void init(){
        epic = new Epic("Эпик 1", "Описание эпика", new ArrayList<>());
        subtask = new Subtask("Подзадача 1", "Описание подзадачи", Status.IN_PROGRESS, epic);
    }
    @Test
    void testSubtaskInitialization() {

        assertEquals("Подзадача 1", subtask.getName(), "Название подзадачи должно совпадать");
        assertEquals("Описание подзадачи", subtask.getDescription(), "Описание подзадачи должно совпадать");
        assertEquals(2, subtask.getId(), "ID подзадачи должен совпадать");
        assertEquals(Status.IN_PROGRESS, subtask.getStatus(), "Статус подзадачи должен совпадать");
        assertEquals(epic, subtask.getParentEpic(), "Родительский эпик должен совпадать");
    }

}
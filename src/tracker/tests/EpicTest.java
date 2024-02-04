package tracker.tests;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicInitialization() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", 1, new ArrayList<>());

        assertEquals("Эпик 1", epic.getName(), "Название эпика должно совпадать");
        assertEquals("Описание эпика", epic.getDescription(), "Описание эпика должно совпадать");
        assertEquals(1, epic.getId(), "ID эпика должен совпадать");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    void testCleanSubtaskIds() {
        Epic epic = new Epic("Эпик 1", "Описание эпоса", 1, new ArrayList<>());
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", 2, Status.NEW, epic);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", 3, Status.IN_PROGRESS, epic);

        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);

        epic.cleanSubtaskIds();

        assertEquals(0, subtask1.getId(), "ID подзадачи 1 должен быть сброшен");
        assertEquals(0, subtask2.getId(), "ID подзадачи 2 должен быть сброшен");
    }

}
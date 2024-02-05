package tracker.tests.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    @BeforeEach
    void init(){
        epic = new Epic("Эпик 1", "Описание эпика", 1, new ArrayList<>());
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", 2, Status.NEW, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", 3, Status.IN_PROGRESS, epic);
    }
    @Test
    void testCleanSubtaskIds() {
        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);

        epic.cleanSubtaskIds();

        assertEquals(0, subtask1.getId(), "ID подзадачи 1 должен быть сброшен");
        assertEquals(0, subtask2.getId(), "ID подзадачи 2 должен быть сброшен");
    }

}
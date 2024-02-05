package tracker.tests.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task;
    Task task1;
    Task task2;
    @BeforeEach
    void init(){
        task = new Task("Тестовая задача", "Описание тестовой задачи", 1, Status.DONE);
        task1 = new Task("Задача 1", "Описание 1", 1, Status.IN_PROGRESS);
        task2 = new Task("Задача 1", "Описание 1", 1, Status.IN_PROGRESS);
    }

    @Test
    void testTaskInitialization() {

        assertEquals("Тестовая задача", task.getName(), "Название задачи должно совпадать");
        assertEquals("Описание тестовой задачи", task.getDescription(), "Описание задачи должно совпадать");
        assertEquals(Status.DONE, task.getStatus(), "Статус задачи должен совпадать");
    }

    @Test
    void testTaskEquality() {

        assertEquals(task1, task2, "Задачи должны быть равны");
    }

}
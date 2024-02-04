package tracker.tests;

import org.junit.jupiter.api.Test;
import tracker.model.Status;
import tracker.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testTaskInitialization() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", 1, Status.DONE);

        assertEquals("Тестовая задача", task.getName(), "Название задачи должно совпадать");
        assertEquals("Описание тестовой задачи", task.getDescription(), "Описание задачи должно совпадать");
        assertEquals(Status.DONE, task.getStatus(), "Статус задачи должен совпадать");
    }

    @Test
    void testTaskEquality() {
        Task task1 = new Task("Задача 1", "Описание 1", 1, Status.IN_PROGRESS);
        Task task2 = new Task("Задача 1", "Описание 1", 1, Status.IN_PROGRESS);

        assertEquals(task1, task2, "Задачи должны быть равны");
    }

}
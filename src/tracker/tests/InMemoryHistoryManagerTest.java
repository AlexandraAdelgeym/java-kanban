package tracker.tests;

import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryHistoryManager;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.Status;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testAddToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи",1, Status.NEW);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(1, history.size(), "Размер истории должен быть 1");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной задачей");
    }

    @Test
    void testAddToHistoryLimitedSize() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Тестовая задача " + i, "Описание тестовой задачи " + i, i+1, Status.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(10, history.size(), "Размер истории должен быть ограничен 10 элементами");
    }

    @Test
    void testAddToHistoryOrdered() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Тестовая задача 1", "Описание тестовой задачи 1", 1, Status.NEW);
        Task task2 = new Task("Тестовая задача 2", "Описание тестовой задачи 2", 2, Status.IN_PROGRESS);
        Task task3 = new Task("Тестовая задача 3", "Описание тестовой задачи 3", 3, Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(3, history.size(), "Размер истории должен быть 3");
        assertEquals(task1, history.get(0), "Задача 1 должна быть первой в истории");
        assertEquals(task2, history.get(1), "Задача 2 должна быть второй в истории");
        assertEquals(task3, history.get(2), "Задача 3 должна быть третьей в истории");
    }

    @Test
    void testGetHistoryEmpty() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(0, history.size(), "Размер истории должен быть 0");
    }

}
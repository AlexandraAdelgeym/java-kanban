package tracker.tests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryHistoryManager;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddToHistory() {
        Task task = new Task("Test Task", "Description", 1, Status.NEW);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void testAddToHistoryLimitedSize() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Test Task " + i, "Description " + i, i, Status.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(10, history.size());
    }

    @Test
    void testAddToHistoryOrdered() {
        Task task1 = new Task("Test Task 1", "Description 1", 1, Status.NEW);
        Task task2 = new Task("Test Task 2", "Description 2", 2, Status.IN_PROGRESS);
        Task task3 = new Task("Test Task 3", "Description 3", 3, Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void testGetHistoryEmpty() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(0, history.size());
    }




}


package tracker.tests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    abstract void setUp();

    @Test
    void addNewTask() {
        Task task = new Task("Task 1", "Description 1", Status.NEW,Duration.ofMinutes(60), LocalDateTime.now());
        int taskId = taskManager.addNewTask(task);
        assertNotNull(taskManager.getTaskById(taskId));
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        assertNotNull(taskManager.getEpicById(epicId));
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1",new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
            Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic, Duration.ofMinutes(60), LocalDateTime.now());
        int subtaskId = taskManager.addNewSubtask(subtask);
        assertNotNull(taskManager.getSubtaskById(subtaskId));
    }

    @Test
    void calculateEpicStatus_AllNew() {
        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic, Duration.ofMinutes(60), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.calculateEpicStatus(epic);
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus());
    }

    @Test
    void calculateEpicStatus_AllDone() {
        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.DONE, epic, Duration.ofMinutes(60), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 1", Status.DONE, epic, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.calculateEpicStatus(epic);
        assertEquals(Status.DONE, taskManager.getEpicById(epicId).getStatus());
    }

    @Test
    void calculateEpicStatus_Mixed() {
        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic, Duration.ofMinutes(60), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 1", Status.DONE, epic, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.calculateEpicStatus(epic);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());
    }

    @Test
    void calculateEpicStatus_AllInProgress() {
        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.IN_PROGRESS, epic, Duration.ofMinutes(60), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 1", Status.IN_PROGRESS, epic, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.calculateEpicStatus(epic);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());
    }

    @Test
    void addNewTask_TimeSlotConflict() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60),LocalDateTime.now().plusMinutes(30));
        taskManager.addNewTask(task1);
        int task2Id = taskManager.addNewTask(task2);
        assertEquals(-1, task2Id);
    }

    @Test
    void getHistory_Empty() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void getHistory_WithTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(120));
        int taskId1 = taskManager.addNewTask(task1);
        int taskId2 = taskManager.addNewTask(task2);
        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(taskId1, history.get(0).getId());
        assertEquals(taskId2, history.get(1).getId());
    }
}


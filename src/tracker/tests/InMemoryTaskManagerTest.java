package tracker.tests;

import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void testAddAndGetTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", 1, Status.NEW);

        int taskId = taskManager.addNewTask(task);
        Task retrievedTask = taskManager.getTaskById(taskId);

        assertNotNull(retrievedTask, "Полученная задача не должна быть пустой");
        assertEquals(task, retrievedTask, "Полученная задача должна совпадать с добавленной задачей");
    }

    @Test
    void testGenerateId() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        int id1 = taskManager.generateId();
        int id2 = taskManager.generateId();

        assertNotEquals(id1, id2, "ID должны различаться");
    }

    @Test
    void testAddAndGetAllTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", 1, Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", 2, Status.IN_PROGRESS);

        int taskId1 = taskManager.addNewTask(task1);
        int taskId2 = taskManager.addNewTask(task2);

        ArrayList<Task> allTasks = taskManager.getAllTasks();

        assertNotNull(allTasks, "Список задач не должен быть пустым");
        assertEquals(2, allTasks.size(), "Размер списка задач должен быть 2");
        assertTrue(allTasks.contains(task1), "Список задач должен содержать задачу 1");
        assertTrue(allTasks.contains(task2), "Список задач должен содержать задачу 2");
    }

    @Test
    void testDeleteTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", 1, Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", 2, Status.IN_PROGRESS);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.deleteTasks();

        ArrayList<Task> allTasks = taskManager.getAllTasks();

        assertNotNull(allTasks, "Список задач не должен быть пустым");
        assertEquals(0, allTasks.size(), "Размер списка задач должен быть 0 после удаления");
    }


    @Test
    void testDeleteEpics() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, new ArrayList<>());
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", 2, new ArrayList<>());

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        taskManager.deleteEpics();

        ArrayList<Task> allTasks = taskManager.getAllTasks();

        assertNotNull(allTasks, "Список задач не должен быть пустым");
        assertEquals(0, allTasks.size(), "Размер списка задач должен быть 0 после удаления эпиков");
    }


    @Test
    void testCalculateEpicStatus() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик", "Описание эпика", 1, new ArrayList<>());
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1, Status.NEW, epic);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 2, Status.IN_PROGRESS, epic);

        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.calculateEpicStatus(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void testUpdateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", 1, Status.NEW);

        int taskId = taskManager.addNewTask(task);
        Task updatedTask = new Task("Обновленная задача", "Новое описание", taskId, Status.IN_PROGRESS);

        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTaskById(taskId);

        assertNotNull(retrievedTask, "Полученная задача не должна быть пустой");
        assertEquals(updatedTask, retrievedTask, "Полученная задача должна быть обновленной");
    }

    @Test
    void testRemoveTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", 1, Status.NEW);

        int taskId = taskManager.addNewTask(task);
        taskManager.removeTaskById(taskId);

        Task retrievedTask = taskManager.getTaskById(taskId);

        assertNull(retrievedTask, "Удаленная задача должна быть null");
    }


}
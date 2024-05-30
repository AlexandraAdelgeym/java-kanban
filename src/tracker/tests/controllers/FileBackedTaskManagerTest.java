package tracker.tests.controllers;

import org.junit.jupiter.api.Test;
import tracker.controllers.FileBackedTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("temp", ".txt");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        assertTrue(tempFile.exists());
        assertEquals(0, tempFile.length());

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        assertEquals(0, loadedManager.getAllTasks().size());
    }

    @Test
    public void testSaveAndLoadTasks() throws IOException {
        File tempFile = File.createTempFile("temp", ".txt");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofMinutes(45), LocalDateTime.now());
        Epic epic = new Epic("Epic", "Epic description", new ArrayList<>());
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.DONE, epic, Duration.ofMinutes(30), LocalDateTime.now());

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);

        taskManager.save();

        System.out.println("File Content before loading:");
        System.out.println(Files.readString(tempFile.toPath()));

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        System.out.println("File Content after loading:");
        System.out.println(Files.readString(tempFile.toPath()));

        HashMap<Integer, Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(4, loadedTasks.size());
        assertTrue(loadedTasks.containsValue(task1));
        assertTrue(loadedTasks.containsValue(task2));
        assertTrue(loadedTasks.containsValue(epic));
        assertTrue(loadedTasks.containsValue(subtask));
    }

    @Test
    void addNewTask() {
        File tempFile = new File("temp.txt");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        int taskId = taskManager.addNewTask(task);
        assertNotNull(taskManager.getTaskById(taskId));
    }

    @Test
    void addNewEpic() {
        File tempFile = new File("temp.txt");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        assertNotNull(taskManager.getEpicById(epicId));
    }

    @Test
    void addNewSubtask() {
        File tempFile = new File("temp.txt");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic 1", "Description 1", new ArrayList<>());
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic, Duration.ofMinutes(60), LocalDateTime.now());
        int subtaskId = taskManager.addNewSubtask(subtask);
        assertNotNull(taskManager.getSubtaskById(subtaskId));
    }
}

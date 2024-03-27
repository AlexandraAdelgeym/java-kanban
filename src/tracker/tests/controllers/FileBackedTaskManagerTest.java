package tracker.tests.controllers;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.controllers.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        Epic epic = new Epic("Epic", "Epic description", new ArrayList<>());
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.DONE, epic);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);

        taskManager.saveToFile();

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

}


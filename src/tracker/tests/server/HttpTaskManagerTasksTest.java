package tracker.tests.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tracker.server.HttpTaskServer;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.Task;
import tracker.model.Status;
import tracker.controllers.TaskManager;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private static final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Tasks should be returned");
        assertEquals(1, tasksFromManager.size(), "There should be one task");
        assertEquals("Test Task", tasksFromManager.get(0).getName(), "Task name should match");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertNotNull(tasksFromResponse, "Tasks should be returned");
        assertEquals(1, tasksFromResponse.size(), "There should be one task");
        assertEquals("Test Task", tasksFromResponse.get(0).getName(), "Task name should match");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Initial Task", "Initial Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int taskId = manager.addNewTask(task);

        Task updatedTask = new Task("Updated Task", "Updated Description", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        updatedTask.setId(taskId);
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromManager = manager.getTaskById(taskId);
        assertNotNull(taskFromManager, "Task should be returned");
        assertEquals("Updated Task", taskFromManager.getName(), "Task name should match");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task to Delete", "Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int taskId = manager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromManager = manager.getTaskById(taskId);
        assertNull(taskFromManager, "Task should be deleted");
    }
}


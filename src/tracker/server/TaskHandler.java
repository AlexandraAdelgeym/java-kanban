package tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                handleGetTasks(exchange);
                break;
            case "POST":
                handleAddOrUpdateTask(exchange);
                break;
            case "DELETE":
                handleDeleteTasks(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response, 200);
    }

    private void handleAddOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(isr, Task.class);

        if (task.getId() == 0) {
            int id = taskManager.addNewTask(task);
            sendText(exchange, "{\"id\":" + id + "}", 201);
        } else {
            taskManager.updateTask(task);
            exchange.sendResponseHeaders(204, -1); // No Content
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        exchange.sendResponseHeaders(204, -1); // No Content
    }
}


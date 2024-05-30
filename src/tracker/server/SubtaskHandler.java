package tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                handleGetSubtasks(exchange);
                break;
            case "POST":
                handleAddOrUpdateSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtasks(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendText(exchange, response, 200);
    }

    private void handleAddOrUpdateSubtask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(isr, Subtask.class);

        if (subtask.getId() == 0) {
            int id = taskManager.addNewSubtask(subtask);
            sendText(exchange, "{\"id\":" + id + "}", 201);
        } else {
            taskManager.updateSubtask(subtask);
            exchange.sendResponseHeaders(204, -1); // No Content
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasks();
        exchange.sendResponseHeaders(204, -1); // No Content
    }
}


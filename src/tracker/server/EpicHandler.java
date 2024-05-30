package tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                handleGetEpics(exchange);
                break;
            case "POST":
                handleAddOrUpdateEpic(exchange);
                break;
            case "DELETE":
                handleDeleteEpics(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());
        sendText(exchange, response, 200);
    }

    private void handleAddOrUpdateEpic(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(isr, Epic.class);

        if (epic.getId() == 0) {
            int id = taskManager.addNewEpic(epic);
            sendText(exchange, "{\"id\":" + id + "}", 201);
        } else {
            taskManager.updateEpic(epic);
            exchange.sendResponseHeaders(204, -1); // No Content
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        exchange.sendResponseHeaders(204, -1); // No Content
    }
}

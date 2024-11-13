package main.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.models.Subtask;
import main.service.TaskManager;
import main.util.Endpoint;
import main.util.Exception.NotFoundException;
import main.util.Exception.ValidationException;

import java.io.IOException;
import java.time.LocalDateTime;

import static main.util.TypeTask.*;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_SUBTASK_BY_ID: {
                handleGetSubtaskById(exchange);
                break;
            }
            case POST_SUBTASK: {
                handlePostSubtask(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                handleDeleteSubtask(exchange);
                break;
            }
            case DELETE_ALL_SUBTASK: {
                handleDeleteAllSubtask(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        getTask(exchange, SUBTASK);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        getTaskById(exchange, SUBTASK);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String body = readBodyRequest(exchange);
        if (body.isEmpty())
            return;

        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequest(exchange);
                return;
            }

            Subtask subtaskFromRequest = gson.fromJson(jsonElement, Subtask.class);
            Subtask subtask = new Subtask(subtaskFromRequest.getTitle(), subtaskFromRequest.getDescription(),
                    subtaskFromRequest.getEpicId(), subtaskFromRequest.getStartTime(),
                    (int) subtaskFromRequest.getDuration().toMinutes());

            if (subtaskFromRequest.getId() > 0) {
                subtask.setId(subtaskFromRequest.getId());
                subtask.setStatus(subtaskFromRequest.getStatus());
                subtask.setStartTime(subtaskFromRequest.getStartTime());
                subtask.setDuration(subtaskFromRequest.getDuration());
                Subtask updateTask = taskManager.updateSubtask(subtask);
                String json = gson.toJson(updateTask);
                System.out.println(LocalDateTime.now() + " Задача с id=" + subtask.getId() + " обновлена");
                System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
                sendText(exchange, json);
                System.out.println("Body: " + json);
            } else if (subtaskFromRequest.getId() == 0) {
                Subtask createdTask = taskManager.createSubtask(subtask);
                System.out.println(LocalDateTime.now() + " Задача с id=" + subtask.getId() + " создана");
                String json = gson.toJson(createdTask);
                System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
                sendText(exchange, json);
                System.out.println("Body: " + json);
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        } catch (ValidationException e) {
            System.err.println(e.getMessage());
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        deleteTask(exchange, SUBTASK);
    }

    private void handleDeleteAllSubtask(HttpExchange exchange) throws IOException {
        deleteAllTask(exchange, SUBTASK);
    }
}

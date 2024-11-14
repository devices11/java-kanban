package main.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.models.Task;
import main.service.TaskManager;
import main.util.Endpoint;
import main.util.Exception.NotFoundException;
import main.util.Exception.ValidationException;

import java.io.IOException;
import java.time.LocalDateTime;

import static main.util.TypeTask.*;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetTaskById(exchange);
                break;
            }
            case POST_TASK: {
                handlePostTask(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            case DELETE_ALL_TASK: {
                handleDeleteAllTask(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        getTask(exchange, TASK);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        getTaskById(exchange, TASK);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String body = readBodyRequest(exchange);
        if (body.isEmpty())
            return;

        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequest(exchange);
                return;
            }

            Task taskFromRequest = gson.fromJson(jsonElement, Task.class);
            Task task = new Task(taskFromRequest.getTitle(), taskFromRequest.getDescription(),
                    taskFromRequest.getStartTime(), (int) taskFromRequest.getDuration().toMinutes());

            if (taskFromRequest.getId() > 0) {
                task.setId(taskFromRequest.getId());
                task.setStatus(taskFromRequest.getStatus());
                task.setStartTime(taskFromRequest.getStartTime());
                task.setDuration(taskFromRequest.getDuration());
                Task updateTask = taskManager.updateTask(task);
                String json = gson.toJson(updateTask);
                System.out.println(LocalDateTime.now() + " Задача с id=" + task.getId() + " обновлена");
                System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
                sendText(exchange, json);
                System.out.println("Body: " + json);
            } else if (taskFromRequest.getId() == 0) {
                Task createdTask = taskManager.createTask(task);
                System.out.println(LocalDateTime.now() + " Задача с id=" + task.getId() + " создана");
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
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        deleteTask(exchange, TASK);
    }

    private void handleDeleteAllTask(HttpExchange exchange) throws IOException {
        deleteAllTask(exchange, TASK);
    }
}

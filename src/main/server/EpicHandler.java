package main.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.models.Epic;
import main.models.Subtask;
import main.service.TaskManager;
import main.util.Endpoint;
import main.util.Exception.NotFoundException;
import main.util.Exception.ValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static main.util.TypeTask.*;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC_BY_ID: {
                handleGetEpicById(exchange);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                handleGetEpicSubtasks(exchange);
                break;
            }
            case POST_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
                break;
            }
            case DELETE_ALL_EPIC: {
                handleDeleteAllEpic(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        getTask(exchange, EPIC);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        getTaskById(exchange, EPIC);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        try {
            int id = getId(exchange);
            if (taskManager.getEpicById(id) == null) {
                sendNotFound(exchange);
                return;
            }
            ArrayList<Subtask> subtasks = taskManager.getAllSubtaskInEpic(id);
            String json;
            json = gson.toJson(subtasks);
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, json);
            System.out.println("Body: " + json);

        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = readBodyRequest(exchange);
        if (body.isEmpty())
            return;

        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequest(exchange);
                return;
            }

            Epic epicFromRequest = gson.fromJson(jsonElement, Epic.class);
            Epic epic = new Epic(epicFromRequest.getTitle(), epicFromRequest.getDescription());

            if (epicFromRequest.getId() > 0) {
                epic.setId(epicFromRequest.getId());
                Epic updateEpic = taskManager.updateEpic(epic);
                String json = gson.toJson(updateEpic);
                System.out.println(LocalDateTime.now() + " Задача с id=" + epic.getId() + " обновлена");
                System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
                sendText(exchange, json);
                System.out.println("Body: " + json);
            } else if (epicFromRequest.getId() == 0) {
                Epic createdEpic = taskManager.createEpic(epic);
                System.out.println(LocalDateTime.now() + " Задача с id=" + epic.getId() + " создана");
                String json = gson.toJson(createdEpic);
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

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        deleteTask(exchange, EPIC);
    }

    private void handleDeleteAllEpic(HttpExchange exchange) throws IOException {
        deleteAllTask(exchange, EPIC);
    }
}
